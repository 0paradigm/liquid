<template>
  <t-loading :loading="isLoading" fullscreen/>
  <div style="display: flex; justify-content: center">
    <div class="container">
      <div style="display: flex; justify-content: space-between">
        <a-breadcrumb style="margin-top: 20px" :routes="routes">
          <template #item-render="{route, paths}">
            <a-link @click="jumpToTree(route.dir)">
              {{ route.label }}
            </a-link>
          </template>
        </a-breadcrumb>
        <t-radio-group variant="default-filled" v-model="value" style="margin-top: 20px; height: 40px;">
          <t-radio-button value="1">单文件</t-radio-button>
          <t-radio-button value="2">文件夹</t-radio-button>
        </t-radio-group>
      </div>

      <a-upload v-if="value==='1'"
                draggable
                :action=url
                :show-retry-button='false'
                style="width: 1000px; margin-top: 20px"
                :file-list="file_list"
                :data="{'taskId':taskId,
                 'path': path,
              }"
                @change="onChange"
      ></a-upload>

      <a-upload v-if="value==='2'"
                directory
                draggable
                :action=url
                :show-retry-button='false'
                style="width: 1000px; margin-top: 20px"
                :file-list="file_list"
                :data="{'taskId':taskId,
                 'path': path,
              }"
                @change="onChange"
      ></a-upload>

      <div class="comment-container">
        <user-avatar :user="this.me"/>
        <a-space direction="vertical" style="margin-left: 20px; ">
          <a-card style="width: 700px; border-radius: 10px" title="Commit changes">
            <a-input placeholder="Commit message" allow-clear style="margin-bottom: 10px" v-model="message"/>
            <a-textarea placeholder="Add an optional extended description..." allow-clear/>
          </a-card>
          <a-space>
            <a-button style="border-radius: 10px;" type="primary" @click="commitChange()"
                      :disabled="file_list.length == 0">Commit Changes
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
import {Message} from '@arco-design/web-vue';
import api from "@/api";
import {mapActions, mapGetters} from "vuex";
import userAvatar from "@/components/user/F1userAvatar.vue";

export default {
  name: "index.vue",
  components: {
    userAvatar,
  },
  computed: {
    ...mapGetters('rep', ['isLoading']),
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
      value: "1",
      url: '',
      taskId: '',
      path: '',
      file_list: [],
      message: '',
      owner: this.$route.params.user,
      rep: this.$route.params.rep,
      branch: this.$route.params.branch,
      me: localStorage.getItem('inUser')
    }
  },
  created() {
    document.title = `Upload file to ${this.owner}/${this.rep}/${this.path}`
    this.taskId = new Date().getTime()
    this.url = api.uploadRepUrl(this.owner, this.rep, this.branch)
    if (this.$route.params.dir !== '') {
      this.$route.params.dir.forEach(dir => this.path += dir + '/')
    }
  },

  methods: {
    ...mapActions('rep', ['commitUpload']),

    onChange(fileList) {
      this.file_list = [];
      fileList.forEach(item => {
        if (item.response) {
          item.name = item.response.data
        }
        this.file_list.push(item)
      })
    },
    jumpToTree(dir) {
      if (dir.toString() === [].toString() || dir.toString() !== this.$route.params.dir.toString()) {
        this.$router.push({
          name: 'tree', params: {
            branch: this.$route.params.branch || 'master',
            dir: dir
          }
        })
      }
    },

    commitChange() {
      if (this.message.replaceAll(' ', '') === '') {
        Message.warning('Please enter the commit message')
        return
      }
      const fileList = []
      this.file_list.forEach(i => fileList.push(i.response.data))
      var data = {addFiles: JSON.parse(JSON.stringify(fileList)), message: this.message, taskId: this.taskId}
      this.commitUpload([this.owner, this.rep, this.branch, data])
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