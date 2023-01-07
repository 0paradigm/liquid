<template>
  <div>
    <div style="display: flex; justify-content: space-between">
      <a-typography-paragraph style="margin-left: 5px">
        <icon-launch/>
        Showing
        <a-typography-text style="font-weight: 550">
          {{changeNum}} changed file
        </a-typography-text>
      </a-typography-paragraph>
      <a-switch @change="changeIsInlined" v-model="isInlined">
        <template #checked>
          ON
        </template>
        <template #unchecked>
          OFF
        </template>
      </a-switch>
    </div>
    <div style="display: flex; justify-content: center; margin-top: 10px">
      <div v-if="showSide" class="left-side" style="margin-right: 20px">
        <a-input-search
            :style="{width:'250px', borderRadius:'10px', marginBottom:'10px'}" placeholder="Filter changed files"
            v-model="searchKey"
        />
        <a-tree @select="handleSelect" :data="treeData" show-line v-model:selected-keys="selectKey">
          <template #switcher-icon="node, { isLeaf }">
            <IconDown v-if="!isLeaf" />
            <icon-drive-file v-if="isLeaf" />
          </template>
          <template #title="nodeData">
            <template v-if="index = getMatchIndex(nodeData?.title), index < 0">{{ nodeData?.title }}</template>
            <span v-else>..
          {{ nodeData?.title?.substr(0, index) }}
          <span style="color: var(--color-primary-light-4);">
            {{ nodeData?.title?.substr(index, searchKey.length) }}
          </span>{{ nodeData?.title?.substr(index + searchKey.length) }}
        </span>
          </template>
        </a-tree>
      </div>
      <div class="right-side" style="width: 100%">
        <a-card style="border-radius: 10px;"
                :header-style="{backgroundColor:'var(--color-neutral-1)', borderTopLeftRadius:'10px', borderTopRightRadius:'10px'}">
          <template #title>
            {{choose}}
          </template>

          <monaco-editor :new-value="newVal" :old-value="oldVal" ref="monaco" :is-diff="true" style="width: 100%"></monaco-editor>

        </a-card>

      </div>
    </div>
  </div>

</template>

<script>
import {h} from 'vue';
import {IconStar, IconDriveFile} from '@arco-design/web-vue/es/icon';
import monacoEditor from "@/components/file/F1monacoEditor";

export default {
  name: "fileChangeItem",
  components: {
    IconStar,
    monacoEditor
  },
  computed: {
    treeData() {
      if (!this.searchKey) {
        return this.originTreeData
      }
      return this.searchData(this.searchKey);
    },
    changeNum(){
      return this.changes.length
    }
  },
  props: {
    showSide: {default: true},
    originTreeData: {
      default: []
    },
    changes: {
      default: []
    },
    defaultChoose:{
      default: '',
    },
    oldValue: {
      default: ''
    },
    newValue: {
      default: '',
    },
  },
  watch: {
    'oldValue': function (val) {//监听props中的属性
      this.oldVal = val;
    },
    'newValue': function (val) {//监听props中的属性
      this.newVal = val;
    },
    'defaultChoose': function (val){
      this.choose = val
    }
  },
  data() {
    return {
      isInlined: true,
      searchKey: '',
      selectKey: [],
      oldVal: this.oldValue,
      newVal: this.newValue,
      choose: this.defaultChoose,
    }
  },
  methods: {
    handleSelect(selectedKeys){
      this.changes.forEach(item=>{
        if(item.file===selectedKeys[0]){
          this.oldVal = item.old
          this.newVal = item.new
          this.choose = item.file
          this.isInlined = true
        }
      })
    },
    searchData(keyword) {
      const loop = (data) => {
        const result = [];
        data.forEach(item => {
          if (item.title.toLowerCase().indexOf(keyword.toLowerCase()) > -1) {
            result.push({...item});
          } else if (item.children) {
            const filterData = loop(item.children);
            if (filterData.length) {
              result.push({
                ...item,
                children: filterData
              })
            }
          }
        })
        return result;
      }
      return loop(this.originTreeData);
    },
    getMatchIndex(title) {
      if (!this.searchKey) return -1;
      return title.toLowerCase().indexOf(this.searchKey.toLowerCase());
    },
    changeIsInlined(val) {
      this.$refs.monaco.upDateIsInlined(val)
    },
  }
}
</script>
