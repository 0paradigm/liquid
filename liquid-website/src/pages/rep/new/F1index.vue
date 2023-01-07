<template>
  <t-loading :loading="isLoading" fullscreen/>
  <div style="display: flex;justify-content: center;margin-top: 20px">
    <a-space direction="vertical">

      <a-space style="margin-bottom: 10px">
        <a-breadcrumb :routes="routes">
          <template #item-render="{route, paths}">
            <a-link>
              {{ route.label }}
            </a-link>
          </template>
        </a-breadcrumb>
        <a-input style="border-radius: 10px" placeholder="Name your file..." v-model="fileName"/>
      </a-space>

      <mavon-editor style="width: 100%; height: 100%" language="en" v-model="value" :subfield=false
                    :autofocus=false></mavon-editor>
      <div class="comment-container">
        <user-avatar :user="this.me"/>
        <a-space direction="vertical" style="margin-left: 20px; ">
          <a-card style="width: 700px; border-radius: 10px" title="Commit changes">
            <a-input placeholder="Commit message" allow-clear style="margin-bottom: 10px" v-model="message"/>
            <a-textarea placeholder="Add an optional extended description..." allow-clear/>
          </a-card>
          <a-space style="margin-bottom: 20px">
            <a-button style="border-radius: 10px;" type="primary" @click="commitChange()">Commit Changes
            </a-button>
            <a-button style="border-radius: 10px;" type="dashed" status="danger" @click="this.$router.go(-1)">Cancel
            </a-button>
          </a-space>
        </a-space>
      </div>

    </a-space>
  </div>
</template>

<script>
import fileHeader from "@/components/file/F1fileHeader";
import userAvatar from "@/components/user/F1userAvatar.vue";
import {Message} from "@arco-design/web-vue";
import request from "@/utils/request";
import {BACKEND_URL} from "@/utils/config";
import {mapActions} from "vuex";

export default {
  name: "index",
  components: {
    fileHeader,
    userAvatar
  },
  computed: {
    routes() {
      let routes = [];
      let dir = []
      routes.push({
        dir: [],
        label: this.$route.params.rep
      })
      if (this.$route.params.dir !== '') {
        this.$route.params.dir.forEach(item => {
          dir.push(item)
          routes.push({
            dir: JSON.parse(JSON.stringify(dir)),
            label: item
          })
        })
      }
      return routes
    }
  },
  data() {
    return {
      value: '',
      message: '',
      fileName: '',
      owner: this.$route.params.user,
      rep: this.$route.params.rep,
      branch: this.$route.params.branch,
      taskId: new Date().getTime(),
      me: localStorage.getItem('inUser'),
      isLoading: false
    }
  },
  methods: {
    ...mapActions('rep', ['commitUpload']),

    commitChange() {
      if (this.fileName === '') {
        Message.warning('Please name the file')
        return;
      }
      if (this.message.trim() === '') {
        Message.warning('Please enter the commit message')
        return
      }
      request.post(BACKEND_URL + `/git/web/upload2/${this.owner}/${this.rep}/${this.branch}`, {
        taskId: this.taskId,
        path: encodeURI(this.$route.params.dir).replaceAll(',', '/'),
        fileName: this.fileName,
        ctx: this.value,
      })
          .then(resp => {
            console.log(resp.data.data)
            const data = (this.$route.params.dir ?? []).length > 0
                ? {
                  addFiles: JSON.parse(JSON.stringify([`${encodeURI(this.$route.params.dir).replaceAll(',', '/')}/${this.fileName}`])),
                  message: this.message,
                  taskId: this.taskId
                }
                : {
                  addFiles: JSON.parse(JSON.stringify([`${this.fileName}`])),
                  message: this.message,
                  taskId: this.taskId
                }
            console.log(data.addFiles, (this.$route.params.dir ?? []).length > 0, this.$route.params.dir)
            this.sleep(600).then(() => {
              console.log(data)
              this.commitUpload([this.owner, this.rep, this.branch, data])
            })
          })
    },
    sleep(time) {
      return new Promise((resolve) => setTimeout(resolve, time));
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