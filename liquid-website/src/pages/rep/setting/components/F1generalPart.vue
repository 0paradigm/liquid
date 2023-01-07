<template>
  <div class="rightPanel">
    <div class="general_header">General</div>
    <a-divider class="divider"/>
    <div class="repo_name_header">Repository name</div>
    <div class="repo_name_box">
      <t-input :style="{ width: '350px' }" class="input" v-model="repo_name"/>
      <t-button class="rename_button" theme="default" variant="outline" style="border-radius: 8px;" @click="rename">
        Rename
      </t-button>
    </div>

    <div class="feature_header" style="margin-top: 50px">Danger Zone</div>
    <a-list style="margin-top: 15px; margin-bottom: 100px">
      <a-list-item>
        <div style="display:flex; flex-direction:row; justify-content: space-between">
          <div>
            <div style="font-family: 'Arial Rounded MT Bold'">Change repository visibility</div>
            <div>This repository is currently {{ isPrivate ? 'private' : 'public' }}.</div>
          </div>
          <div>
            <a-popconfirm :content="`Sure to change to ${!isPrivate ? 'private' : 'public'}?`" ok-text="sure"
                          cancel-text="cancel"
                          v-on:ok="changeVis">
              <a-button class="red-button" type="outline" shape="round"
                        style="margin-top: 5px; color: crimson; border-color: brown;">
                Change visibility
              </a-button>
            </a-popconfirm>
          </div>
        </div>
      </a-list-item>
      <a-list-item>
        <div style="display:flex; flex-direction:row; justify-content: space-between">
          <div>
            <div style="font-family: 'Arial Rounded MT Bold'">Delete this repository</div>
            <div>Once you delete a repository, there is no going back. Please be certain.</div>
          </div>
          <div>
            <a-popconfirm :content="`Sure to delete this repository?`" ok-text="sure"
                          cancel-text="cancel"
                          v-on:ok="deleteRepo">
              <a-button class="red-button" type="outline" shape="round"
                        style="margin-top: 5px; color: crimson; border-color: brown">Delete
                this repository
              </a-button>
            </a-popconfirm>
          </div>
        </div>
      </a-list-item>
    </a-list>
  </div>
</template>

<script>
import request from "@/utils/request";
import {BACKEND_URL} from "@/utils/config";
import {Message} from "@arco-design/web-vue";
import {mapMutations} from 'vuex'

export default {
  name: "generalPart",
  props: {
    repo: {type: String},
    owner: {type: String},
  },
  data() {
    return {
      original_name: '',
      repo_name: '',
      isPrivate: true,
    }
  },
  mounted() {
    this.original_name = this.repo
    this.repo_name = this.repo
    request.get(BACKEND_URL + `/core/api/repo/isprivate/${this.owner}/${this.repo}`)
        .then(resp => this.isPrivate = resp.data.data)
  },
  methods: {
    ...mapMutations('rep', ['setPrivateRepo']),
    rename() {
      if (this.repo_name == this.original_name) {
        return
      }
      if (!/[0-9A-Za-z]/.test(this.repo_name)) {
        Message.error('Invalid name')
        return
      }
      request.post(BACKEND_URL + `/core/api/repo/rename?owner=${this.owner}&oldName=${this.original_name}&newName=${this.repo_name}`)
          .then(resp => {
            console.log(resp.data)
            if (resp.data.code != 0) {
              Message.error('A repository with that name already exists')
              return
            }
            this.$router.push({name: 'code', params: {user: this.owner, rep: this.repo_name}})
          })
    },
    changeVis() {
      request.post(BACKEND_URL + `/core/api/repo/setprivate/${this.owner}/${this.repo}?privated=${!this.isPrivate}`)
          .then(resp => {
            console.log(resp.data)
            request.get(BACKEND_URL + `/core/api/repo/isprivate/${this.owner}/${this.repo}`)
                .then(resp => {this.isPrivate = resp.data.data; this.setPrivateRepo(this.isPrivate)})

          })
    },
    deleteRepo() {
      request.delete(BACKEND_URL + `/core/api/repo/delete/${this.owner}/${this.repo}`)
          .then(resp => {
            console.log(resp.data)
            this.$router.push({name: 'user', params: {user: localStorage.getItem('inUser')}})
          })
    },
  }
}
</script>

<style scoped>
.general_header {
  margin-top: 10px;
  font-size: 23px;
  font-family: "Arial Unicode MS";
}

.feature_header {
  margin-top: 30px;
  margin-left: 2px;
  font-size: 23px;
  font-family: "Arial Unicode MS";
}

.repo_name_header {
  margin-top: 15px;
  font-size: 14px;
  font-family: "Arial Rounded MT Bold";
}

.input {
  margin-top: 10px;
}

.rename_button {
  margin-top: 10px;
  margin-left: 9px;
}

.radio {
  font-size: 14px;
  font-family: "Arial Rounded MT Bold";
}

.list_item {
  display: flex;
  flex-direction: column;
}

.repo_name_box {
  display: flex;
  flex-direction: row;
}

.description {
  font-size: 2px;
  margin-left: 27px;
}

.card {
  margin-top: 10px;
  margin-left: 30px;
}

.instr1 {
  font-size: 14px;
  font-family: "Arial Rounded MT Bold";
  margin-bottom: 5px;
}

.red-button:hover {
  background-color: bisque;
}
</style>