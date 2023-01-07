<template>
  <div ref="container" class="monaco-editor" style="height: 300px; "></div>
</template>
<script>
import * as monaco from 'monaco-editor'
import {toRaw} from 'vue'

export default {
  name: 'AcMonaco',
  props: {
    opts: {
      type: Object,
      default() {
        return {}
      }
    },
    isDiff: {
      default: false
    },
    oldValue: {
      type: String,
      default: '',
    },
    newValue: {
      type: String,
      default: '',
    }
  },
  data() {
    return {
      defaultOpts: {
        value: '',
        autoIndent: true
      },
      monacoEditor: {}
    }
  },
  watch: {
    opts: {
      handler() {
        this.init()
      },
      deep: true
    },
    oldValue: {
      handler() {
        this.init()
      }
    },
    newValue: {
      handler() {
        this.init()
      }
    }
  },
  mounted() {
    this.init()
  },
  methods: {
    init() {
      // 初始化container的内容，销毁之前生成的编辑器
      this.$refs.container.innerHTML = ''
      // 生成编辑器配置
      let editorOptions = Object.assign(this.defaultOpts, this.opts)
      if (this.isDiff) {
        editorOptions.readOnly = true;
        this.monacoDiffInstance = monaco.editor.createDiffEditor(this.$refs['container'], editorOptions)
        this.monacoDiffInstance.setModel({
          original: monaco.editor.createModel(this.oldValue, editorOptions.language),
          modified: monaco.editor.createModel(this.newValue, editorOptions.language)
        })
        this.monacoDiffInstance.updateOptions({
          renderSideBySide: false
        });
      } else {
        this.monacoEditor = monaco.editor.create(this.$refs.container, this.defaultOpts)
        this.monacoEditor.onDidChangeModelContent(() => {
          this.$emit('change', toRaw(this.monacoEditor).getValue())
        })

      }
    },

    upDateIsInlined(val) {
      this.monacoDiffInstance.updateOptions({
        renderSideBySide: !val
      });
    },

    getVal() {
      return toRaw(this.monacoEditor).getValue()
    }
  }
}
</script>
<style>
.monaco-editor {
  width: 100%;
}
</style>
