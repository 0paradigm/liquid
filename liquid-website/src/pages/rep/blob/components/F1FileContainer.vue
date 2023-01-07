<template>

  <div class="content-side">
    <a-list style="margin-top: 30px; background-color: white">
      <template #header>
        <div style="display: flex; justify-content: space-between">
          <div>
          </div>
          <div>
            <a-button v-if="!isEdit" style="border-radius:10px;margin-right:10px" @click="isEdit=true">Edit</a-button>
            <a-button v-if="isEdit" style="border-radius:10px;margin-right:10px" @click="setIsEditFalse()">Cancel
            </a-button>
          </div>
        </div>
      </template>

      <a-list-item>
        <div v-if="extName==='md'">
          <mavon-editor v-if="!isEdit"
                        style="width: 100%; height: 100%; background-color: white !important; border: none !important;"
                        language="en" v-model="oldOpts.value"
                        defaultOpen="preview" :toolbarsFlag="false" :editable="false" :subfield=false
                        :autofocus="false"/>
          <mavon-editor v-else
                        style="width: 100%; height: 100%; background-color: white !important; border: none !important;"
                        language="en" v-model="newOpts.value"
                        :subfield=false :autofocus="false"/>
        </div>
        <div v-else>
          <monaco-editor ref="show" v-if="!isEdit" :opts="oldOpts" style="height: 500px;"/>
          <monaco-editor ref="editor" v-else :opts="newOpts" style="height: 500px;"/>
        </div>
      </a-list-item>
    </a-list>

    <div v-if="isEdit" style="margin-bottom: 30px">
      <div class="comment-container">
        <user-avatar :user="this.me"/>
        <a-space direction="vertical" style="margin-left: 20px; ">
          <a-card style="width: 700px; border-radius: 10px" title="Commit changes">
            <a-input placeholder="Commit message" allow-clear style="margin-bottom: 10px" v-model="message"/>
            <a-textarea placeholder="Add an optional extended description..." allow-clear/>
          </a-card>
          <a-space>
            <a-button style="border-radius: 10px;" type="primary" @click="commitChange">Commit Changes
            </a-button>
            <a-button style="border-radius: 10px;" type="dashed" status="danger" @click="this.$router.go(-1)">Cancel
            </a-button>
          </a-space>
        </a-space>
      </div>
    </div>
  </div>
</template>

<script>
import monacoEditor from "@/components/file/F1monacoEditor";
import {mapActions, mapGetters, mapMutations} from "vuex";
import userAvatar from "@/components/user/F1userAvatar.vue";
import {Message} from "@arco-design/web-vue";
import request from "@/utils/request";
import {BACKEND_URL} from "@/utils/config";

export default {
  name: "FileContainer",
  components: {
    monacoEditor,
    userAvatar,
  },
  methods: {
    ...mapActions('rep', ['getFile', 'commitUpload']),
    ...mapMutations('rep', ['setIsLoading']),
    setIsEditFalse() {
      this.isEdit = false;
    },
    commitChange() {
      if (this.message.trim() === '') {
        Message.warning('Please enter the commit message')
        return
      }
      let dirr = JSON.parse(JSON.stringify(this.$route.params.dir))
      dirr.pop()

      request.post(BACKEND_URL + `/git/web/upload2/${this.owner}/${this.rep}/${this.branch}`, {
        taskId: this.taskId,
        path: encodeURI(dirr).replaceAll(',', '/'),
        fileName: this.$route.params.dir[this.$route.params.dir.length - 1],
        ctx: this.extName==='md' ?this.newOpts.value : this.$refs.editor.getVal()
      })
          .then(resp => {
            console.log(resp.data.data)
            let dirr = JSON.parse(JSON.stringify(this.$route.params.dir))
            dirr.pop()
            console.log(dirr)
            const data = dirr.length > 0
                ? {
                  addFiles: JSON.parse(JSON.stringify([`${encodeURI(dirr).replaceAll(',', '/')}/${this.$route.params.dir[this.$route.params.dir.length - 1]}`])),
                  message: this.message,
                  taskId: this.taskId
                }
                : {
                  addFiles: JSON.parse(JSON.stringify([`${this.$route.params.dir[this.$route.params.dir.length - 1]}`])),
                  message: this.message,
                  taskId: this.taskId
                }
            this.sleep(600).then(() => {
              this.commitUpload([this.owner, this.rep, this.branch, data])
            })
          })
    },
    sleep(time) {
      return new Promise((resolve) => setTimeout(resolve, time));
    }
  },
  computed: {
    ...mapGetters('rep', ['isLoading'])
  },

  created() {
    this.setIsLoading(true)
    this.getFile([this.owner, this.rep, this.branch, this.$route.params.dir]).then(res => {
      this.setIsLoading(false)
      var data = JSON.parse(res.data.data)
      this.oldOpts.value = data.content;
      this.newOpts.value = data.content;
      this.extName = data.extName;
      if (this.extName === 'md') {

      } else {
        this.oldOpts.language = data.extName;
        this.newOpts.language = data.extName
        this.$refs.show.init();
      }
    })
  },
  data() {
    return {
      showDeleteDialog: false,
      message: '',
      taskId: new Date().getTime(),
      me: localStorage.getItem('inUser'),
      owner: this.$route.params.user,
      rep: this.$route.params.rep,
      branch: this.$route.params.branch || 'master',
      isEdit: false,
      extName: '',
      oldOpts: {
        value: '',
        language: '',
        readOnly: true,
      },
      newOpts: {
        value: '',
        language: '',
        readOnly: false
      },
    }
  }
}
</script>

<style scoped>
.comment-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>