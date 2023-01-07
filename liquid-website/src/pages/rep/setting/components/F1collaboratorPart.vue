<template>
  <div style="margin-left: 20px">
    <div class="general_header">Collaborators</div>
    <a-divider class="divider"/>
    <div class="title">Who has access</div>
    <div class="cards" style="margin-top: 17px; display: flex; flex-direction: row">
      <a-card hoverable :style="{ width: '340px', marginBottom: '20px', marginRight: '30px' }">
        <div class="top" style="display: flex; justify-content: space-between; flex-direction: row; margin-bottom: 7px">
          <div class="text"> {{ isPrivate ? 'PRIVATE' : 'PUBLIC' }} REPOSITORY</div>
          <div v-if="!isPrivate">
            <icon-eye/>
          </div>
          <div v-else>
            <icon-eye-invisible/>
          </div>
        </div>
        <div v-if="!isPrivate">
          This repository is public and visible to anyone.
        </div>
        <div v-else>
          Only those with access to this repository can view it.
        </div>
      </a-card>

      <a-card hoverable :style="{ width: '340px', marginBottom: '20px' }">

        <div class="top" style="display: flex; justify-content: space-between; flex-direction: row; margin-bottom: 7px">
          <div class="text">DIRECT ACCESS</div>
          <icon-user-group/>
        </div>

        <div v-if="collaborators.length===0">
          <div>0 collaborator have access to this repository. Only you can contribute to this repository.</div>
        </div>
        <div v-else>
          <div> {{ collaborators.length + 1 }} have access to this repository.</div>
          <div style="display: flex; flex-direction: row">
            <a-link :hoverable="false" v-if="collaborators.length>1"> {{ collaborators.length }}
              collaborators
            </a-link>
            <a-link :hoverable="false" v-if="collaborators.length===1"> {{ collaborators.length }}
              collaborator
            </a-link>
          </div>
        </div>
      </a-card>
    </div>

    <div style="display: flex; flex-direction: row; justify-content: space-between; margin-top: 23px">
      <div class="title">Manage access</div>
    </div>
    <a-list :style="{ width: `710px` }" :size="size" style="margin-top: 17px">

      <a-list-item style="background-color: white">
        <div style="display: flex; flex-direction: row; justify-content: space-between">
          <a-input :style="{width:'80%'}" placeholder="filter:collaborators" allow-clear>
            <template #prefix>
              <icon-search/>
            </template>
          </a-input>
          <a-button type="primary" shape="round" @click="handleClick">Add people</a-button>
        </div>
      </a-list-item>

      <a-modal :visible="this.addVisible"
               @ok="handleOK"
               @cancel="handleCancel"
               okText="Confirm"
               cancel-text="Cancel"
               unmountOnClose>
        <template #title>
          <icon-bookmark class="icon"></icon-bookmark>
          Add collaborators to {{ this.$route.params.rep }}
        </template>
        <div>
          <t-select
              v-model="this.waitingList"
              multiple
              placeholder=""
              :options="options"
              :filter="filterMethod"
          />
        </div>
      </a-modal>

      <a-list-item v-for="user in collaborators" style="background-color: white">
        <div style="display: flex; flex-direction: row; justify-content: space-between">
          <div style="display: flex; flex-direction: row;">
            <user-avatar :user="user" :size="30"/>
            <div style="margin-left: 12px; margin-top: 4px">
              <user-link :user="user"/>
            </div>
          </div>
          <a-popconfirm :content="`Sure to remove ${user} as collaborator?`" ok-text="sure"
                        cancel-text="cancel"
                        v-on:ok="()=>removeCol(user)">
            <a-button type="outline" shape="round">Remove</a-button>
          </a-popconfirm>
        </div>
      </a-list-item>
    </a-list>
  </div>


</template>

<script>
import userLink from "@/components/user/F1userLink.vue";
import userAvatar from "@/components/user/F1userAvatar.vue";
import request from "@/utils/request";
import {BACKEND_URL} from "@/utils/config";

export default {
  name: "collaboratorPart",
  components: {userAvatar, userLink},
  methods: {
    handleClick() {
      this.addVisible = true
    },
    handleCancel() {
      this.addVisible = false
    },
    filterMethod(search, option) {
      console.log('search:', search, ', option:', option);
      return option.label.indexOf(search) !== -1;
    },

    handleOK() {
      this.addVisible = false
      this.waitingList.forEach(name => {
        request.get(BACKEND_URL + `/core/api/repo/add_collaborator/${this.owner}/${this.repo}?colab=${name}`)
            .then(resp => {
              console.log(resp.data, 'add colab')
              this.collaborators.push(name)
            })
            .then(() => {
              const idx = this.waitingList.indexOf(name);
              this.waitingList.splice(idx, 1)
              const idx2 = this.options.indexOf(name);
              this.options.splice(idx2, 1)
            })
      })
    },
    removeCol(user) {
      request.get(BACKEND_URL + `/core/api/repo/remove_collaborator/${this.owner}/${this.repo}?colab=${user}`)
          .then(resp => {
            console.log(resp.data)
            const idx = this.collaborators.indexOf(user);
            this.collaborators.splice(idx, 1)
          })
    },
  },
  data() {
    return {
      options: [],
      waitingList: [],
      collaborators: [],
      isPrivate: false,

      addVisible: false,
    }
  },
  props: {
    repo: {type: String},
    owner: {type: String},
  },
  mounted() {
    request.get(BACKEND_URL + `/core/api/repo/isprivate/${this.owner}/${this.repo}`)
        .then(resp => this.isPrivate = resp.data.data)
    request.get(BACKEND_URL + `/core/api/repo/get_collaborator/${this.owner}/${this.repo}`)
        .then(resp => this.collaborators = resp.data?.data ?? [])
    request.get(BACKEND_URL + `/core/api/repo/addable_collaborator/${this.owner}/${this.repo}`)
        .then(resp => {
          this.options = []
          resp.data.data.forEach(name => this.options.push({label: name, value: name}))
        })
  }
}
</script>

<style scoped>
.general_header {
  margin-top: 30px;
  font-size: 23px;
  font-family: "Arial Unicode MS";
}

.title {
  font-size: 22px;
  font-family: "Arial Unicode MS";
}

.text {
  color: dimgrey;
  font-size: 15px;
  font-family: "Arial Narrow";
}

.icon {
  font-size: 20px;
  margin-right: 6px;
}
</style>