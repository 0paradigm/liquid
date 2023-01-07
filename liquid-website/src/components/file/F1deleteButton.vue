<template>
  <a-button @click="doDownload" class="dl-button" style="border-radius: 10px; margin-right: 20px;" v-if="this.$route.name==='blob'">
    Download
  </a-button>
  <a-tooltip :content=content v-if="this.$route.name==='blob' && isColab">
    <a-button @click="visible=true" class="delete-button" style="border-radius: 10px;">
      <icon-delete/>
    </a-button>
  </a-tooltip>

  <a-modal v-model:visible="visible" width="auto" :hide-cancel="true" :footer="false" unmountOnClose>
    <template #title>
      Confirm to delete
    </template>
    <div style="margin-bottom: 30px">
      <div class="comment-container">
        <user-avatar :user="this.me"/>
        <a-space direction="vertical" style="margin-left: 20px; ">
          <a-card style="width: 700px; border-radius: 10px" title="Commit changes">
            <a-input placeholder="Commit message" allow-clear style="margin-bottom: 10px" v-model="message"/>
            <a-textarea placeholder="Add an optional extended description..." allow-clear/>
          </a-card>
          <a-space>
            <a-button style="border-radius: 10px;" type="primary" @click="commitDelete()">Commit Delete
            </a-button>
            <a-button style="border-radius: 10px;" type="dashed" status="danger" @click="visible=false">Cancel
            </a-button>
          </a-space>
        </a-space>
      </div>
    </div>
  </a-modal>
</template>

<script>
import userAvatar from "@/components/user/F1userAvatar.vue";
import jsCookie from "js-cookie";
import request from "@/utils/request";
import {BACKEND_URL, GITD_URL} from "@/utils/config";
import {Message} from "@arco-design/web-vue";
import router from "@/router";
import axios from "axios";

export default {
  name: "deleteButton",
  components: {
    userAvatar
  },
  data() {
    return {
      content: 'Delete this file',
      visible: false,
      message: '',
      me: localStorage.getItem('inUser'),
      isColab: false,
    }
  },
  mounted() {
    request.get(BACKEND_URL + `/core/api/repo/is_collaborator/${this.$route.params.user}/${this.$route.params.rep}/?colab=${localStorage.getItem('inUser')}`)
        .then(resp => {
          this.isColab = resp.data.data
        })
  },
  methods: {
    commitDelete() {
      if (!this.message) {
        Message.warning('Please enter commit message')
        return
      }
      var path = '';
      (this.$route.params.dir ?? ['']).forEach(el => path += '/' + el)

      request.delete(BACKEND_URL + `/git/web/deletefile/${this.$route.params.user}/${this.$route.params.rep}/${this.$route.params.branch || 'master'}`, {
        data: {
          msg: this.message,
          file: path.substring(1),
        },
        headers: {
          'Authorization': jsCookie.get('Authorization')
        }
      })
          .then(resp => {
            console.log(resp.data.data)
            if (resp.data.code != 0) {
              Message.error(resp.data.data)
              return
            }
            router.go(-1)
          })
    },
    doDownload() {
      var path = '';
      (this.$route.params.dir ?? ['']).forEach(el => path += '/' + el)
      path = path.substring(1)
      const url1 = `/gitd/file/${this.$route.params.user}/${this.$route.params.rep}/${encodeURI(path)}`
      axios({
        method: 'get',
        url: url1,
        data: {},
        responseType: 'blob'
      }).then(res => {
        let data = res.data // 这里后端对文件流做了一层封装，将data指向res.data即可
        if (!data) {
          return
        }
        let url = window.URL.createObjectURL(new Blob([data]))
        let a = document.createElement('a')
        a.style.display = 'none'
        a.href = url
        a.setAttribute('download', this.$route.params.dir[this.$route.params.dir.length - 1])
        document.body.appendChild(a)
        a.click() //执行下载
        window.URL.revokeObjectURL(a.href) //释放url
        document.body.removeChild(a) //释放标签
      }).catch((error) => {
        console.log(error)
      })
    }
  }
}
</script>

<style scoped>
.delete-button:hover {
  color: white;
  background-color: rgb(var(--red-6));
}
.dl-button:hover {
  color: white;
}
</style>
