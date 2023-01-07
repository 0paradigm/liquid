<template>
  <div class="head-side">
    <a-space>
      <a-popover position="bl">
        <a-button class="button" :style="{width:'100px', borderRadius:'10px'}">
          {{ current.substring(0, 8) }}
          <icon-caret-down/>
        </a-button>
        <template #content>
          <a-input class="input"
                   :placeholder="$t('createABranch')"
                   v-model="newBranch"
                   @press-enter="enterPress"
                   allow-clear
                   v-if="isColab"/>
          <a-divider class="divider" v-if="isColab"/>
          <a-tabs default-active-key="1" style="width: 250px">
            <a-tab-pane key="1">
              <template #title>
                <div style="font-size: 12px">{{ $t('Branches') }}</div>
              </template>

              <div v-for="item in data" style="font-size: 12px">
                <div v-if="item===current"
                     style="display: flex; flex-direction: row; justify-content: space-between">
                  <div class="textCurrent">
                    <icon-check class="icon"/>
                    {{ item }}
                  </div>
                </div>

                <div v-else class="textDefault"
                     style="display: flex; flex-direction: row; justify-content: space-between">
                  <div class="textDefault"
                       @click="$router.push('/'+this.$route.params.user+'/'+this.$route.params.rep+'/'+item)">
                    {{ item }}
                  </div>
                  <a-popconfirm content="Are you sure you want to delete?" ok-text="sure" cancel-text="cancel"
                                v-on:ok="()=>deleteBranch(item)" v-if="isColab">
                    <a-button size="mini" shape="round" type="text">
                      <icon-delete/>
                    </a-button>
                  </a-popconfirm>
                </div>
                <a-divider class="divider"></a-divider>
              </div>

            </a-tab-pane>
            <a-tab-pane key="2" style="font-size: 13px">
              <template #title>
                <div style="font-size: 13px">{{ $t('Tags') }}</div>
              </template>
              Nothing to show
            </a-tab-pane>
          </a-tabs>
        </template>
      </a-popover>

      <a-space v-if="this.$route.name==='code'">
        <a-link>
          <template #icon>
            <icon-branch/>
          </template>
          {{ this.data.length }}
          {{ $t('branches') }}
        </a-link>
        <a-link>
          <template #icon>
            <icon-tag/>
          </template>
          {{ this.tags.length }}
          {{ $t('tags') }}
        </a-link>
      </a-space>

      <a-space v-if="this.$route.name==='new'||this.$route.name==='tree' || this.$route.name==='blob'">
        <a-breadcrumb :routes="routes">
          <template #item-render="{route, paths}">
            <a-link @click="jumpToTree(route.dir)">
              {{ route.label }}
            </a-link>
          </template>
        </a-breadcrumb>
      </a-space>

    </a-space>
    <a-space>
      <a-dropdown trigger="hover" v-if="this.$route.name!=='blob' && isColab ">
        <a-button style="border-radius: 10px">{{ $t('AddFile') }}</a-button>
        <template #content>
          <a-doption @click="jumpToNew()">{{ $t('CreateNewFile') }}</a-doption>
          <a-doption @click="jumpToUpdate()">{{ $t('UploadFiles') }}</a-doption>
        </template>
      </a-dropdown>
      <code-button v-if="this.$router.currentRoute.value.name==='code'"></code-button>
      <delete-button v-if="this.$route.name!=='newFile'"></delete-button>
    </a-space>
  </div>
</template>

<script>
import codeButton from "@/components/file/F1codeButton.vue";
import deleteButton from "@/components/file/F1deleteButton";
import request from "@/utils/request";
import {BACKEND_URL} from "@/utils/config";
import {Message} from "@arco-design/web-vue";
import jsCookie from "js-cookie";

export default {
  name: "fileHeader",
  components: {
    codeButton,
    deleteButton
  },
  computed: {
    current() {
      return this.$route.params.branch || 'master'
    },
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
      value: this.$route.params.branch || 'master',
      modalVisible: false,
      newBranch: '',
      isColab: false,
      data: [
        "master",
      ],
      tags: [],
    }
  },
  mounted() {
    request.get(BACKEND_URL + `/git/web/${this.$route.params.user}/${this.$route.params.rep}/listbranches`, {
      headers: {
        'Authorization': jsCookie.get('Authorization')
      }
    })
        .then(resp => {
          this.data = resp.data.data
          if (resp.data.code === 0) {
            let record = {userName: this.$route.params.user, repoName: this.$route.params.rep,}
            let recents = JSON.parse(localStorage.getItem('recents') || '[]').filter(item => {
              return item.userName !== record.userName && item.repoName !== record.repoName
            })
            console.log(recents)
            recents.unshift(record)
            localStorage.setItem('recents', JSON.stringify(recents))
          }
        })
    request.get(BACKEND_URL + `/core/api/repo/is_collaborator/${this.$route.params.user}/${this.$route.params.rep}/?colab=${localStorage.getItem('inUser')}`)
        .then(resp => {
          this.isColab = resp.data.data
        })
  },
  methods: {
    handleClick() {
      this.modalVisible = true;
    },
    handleOk() {
      this.modalVisible = false;
    },
    handleCancel() {
      this.modalVisible = false;
    },
    enterPress() {
      if (!this.newBranch.trim()) {
        return
      }
      for (const br of this.data) {
        if (br == this.newBranch) {
          Message.warning('Branch already exists')
          return;
        }
      }
      const newBr = this.newBranch
      const oldBr = this.$route.params.branch || 'master'
      request.post(BACKEND_URL + `/git/web/createbranch/${this.$route.params.user}/${this.$route.params.rep}/${oldBr}/${newBr}`)
          .then(resp => {
            console.log(resp.data)
            request.get(BACKEND_URL + `/git/web/${this.$route.params.user}/${this.$route.params.rep}/listbranches`, {
              headers: {
                'Authorization': jsCookie.get('Authorization')
              }
            })
                .then(resp => {
                  this.newBranch = ''
                  this.data = resp.data.data
                })
          })
    },
    deleteBranch(br) {
      request.delete(BACKEND_URL + `/git/web/deletebranch/${this.$route.params.user}/${this.$route.params.rep}/${br}`)
          .then(resp => {
            console.log(resp.data)
            request.get(BACKEND_URL + `/git/web/${this.$route.params.user}/${this.$route.params.rep}/listbranches`, {
              headers: {
                'Authorization': jsCookie.get('Authorization')
              }
            })
                .then(resp => {
                  this.data = resp.data.data
                })
          })
    },
    jumpToNew() {
      let branch, dir;
      if (this.$route.name === 'code') {
        branch = this.$route.params.branch || 'master';
        dir = '';
      } else {
        branch = this.$route.params.branch || 'master';
        dir = this.$route.params.dir
      }
      this.$router.push({
        name: 'newFile',
        params: {
          branch: branch,
          dir: dir
        }
      })
    },
    jumpToUpdate() {
      let branch, dir;
      if (this.$route.name === 'code') {
        branch = this.$route.params.branch || 'master';
        dir = '';
      } else {
        branch = this.$route.params.branch || 'master';
        dir = this.$route.params.dir
      }
      this.$router.push({
        name: 'upload',
        params: {
          branch: branch,
          dir: dir
        }
      })
    },
    jumpToTree(dir) {
      if (dir.toString() !== this.$route.params.dir.toString()) {
        this.$router.push({
          name: 'tree', params: {
            branch: this.$route.params.branch || 'master',
            dir: dir
          }
        })
      }
    }
  }
}
</script>

<style scoped>
.head-side {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
}

.divider {
  margin-top: 5px;
  margin-bottom: 5px;
  width: 250px;
}

.icon {
  font-size: 15px;
}

.textDefault {
  margin-left: 12px;
}

.textDefault:hover {
  cursor: pointer;
}

.textCurrent {
  margin-left: 5px;
}

.textCurrent:hover {
  cursor: pointer;
}

.input {
  width: 250px;
  height: 30px;
  border-radius: 10px;
  margin-top: 3px;
  margin-bottom: 3px
}

</style>