<template>
  <div style="display: flex; justify-content: center">
    <div style="width: 1000px">
      <a-typography-title :heading="4">
        Comparing changes
      </a-typography-title>
      <a-typography-text style="color: var(--color-neutral-6)">
        Choose two branches to see what’s changed or to start a new pull request.
      </a-typography-text>
      <a-divider/>
      <a-card style="border-radius: 10px; background-color: var(--color-neutral-1)">
        <a-space>
          <icon-sync/>
          <a-select v-model="chooseRep" style="border-radius: 10px">
            <template #label="{data}">
              <a-typography-text style="margin-right:5px;font-weight: bold; color: var(--color-neutral-6)">base
                repository:
              </a-typography-text>
              {{ data.label }}
            </template>
            <a-option v-for="item in baseReps" :label="item.content" :value="item.value"></a-option>
          </a-select>
          <a-select v-model="chooseBranch" style="border-radius: 10px" v-on:change="updateDiff">
            <template #label="{data}">
              <a-typography-text style="margin-right:5px;font-weight: bold; color: var(--color-neutral-6)">base:
              </a-typography-text>
              {{ data.label }}
            </template>
            <a-option v-for="item in baseBranches" :label="item.content" :value="item.value"></a-option>
          </a-select>
          <icon-arrow-left/>
          <a-select v-model="chooseCmpRep" v-on:change="updateChooseBr" style="border-radius: 10px">
            <template #label="{data}">
              <a-typography-text style="margin-right:5px;font-weight: bold; color: var(--color-neutral-6)">head
                repository:
              </a-typography-text>
              {{ data.label }}
            </template>
            <a-option v-for="item in cmpReps" :label="item.content" :value="item.value"></a-option>
          </a-select>
          <a-select v-if="chooseCmpRep!==0" v-model="chooseCmpBranch" v-on:change="updateDiff"
                    style="border-radius: 10px">
            <template #label="{data}">
              <a-typography-text style="margin-right:5px;font-weight: bold; color: var(--color-neutral-6)">compare:
              </a-typography-text>
              {{ data.label }}
            </template>
            <a-option v-for="item in cmpBranches" :label="item.content" :value="item.value"></a-option>
          </a-select>
        </a-space>
      </a-card>
      <a-card style="margin-top:20px;border-radius: 10px">
        <div style="display: flex; justify-content: space-around">
          <div>
            <icon-up-circle/>
            {{ this.diffCommits.length }} {{ this.diffCommits.length < 2 ? 'commit' : 'commits' }}
          </div>
          <div>
            <icon-file/>
            {{ this.diffFiles.length }} {{ this.diffFiles.length < 2 ? 'file changed' : 'files changed' }}
          </div>
          <div>
            <icon-user-group/>
            {{ this.diffCommits.length > 0 ? 1 : 0 }} contributor
          </div>
        </div>
      </a-card>

      <div style="display: flex; justify-content: center">
        <commit-list style="margin-top: 20px; width: 800px" :commits="diffCommits" :disable-rollback-and-view="true"/>
      </div>
      <file-change-item :old-value="oldValue" :new-value="newValue" :default-choose="defaultChoose"
                        :origin-tree-data="diffFilesTree" :changes="diffFiles"/>

      <div class="comment-container">
        <user-avatar :user="this.me"/>
        <a-space direction="vertical" style="margin-left: 20px;">
          <a-card style="width: 930px; border-radius: 10px" title="Open a pull request">
            <a-input placeholder="Title" allow-clear style="margin-bottom: 10px" v-model="title"/>
            <mavon-editor
                style="width: 890px; height: 250px; background-color: white !important; border: none !important;"
                language="en" placeholder="Leave a comment"
                :subfield=false :autofocus=false v-model="cmtVal"></mavon-editor>
          </a-card>
          <div style="display: flex; justify-content: right">
            <a-space>
              <a-button :disabled="!title||chooseBranch===0||chooseCmpRep===0||chooseCmpBranch===0"
                        style="border-radius: 10px;" type="primary"
                        @click="createPullRequest">Create a pull request
              </a-button>
            </a-space>
          </div>
        </a-space>
      </div>
    </div>
  </div>
</template>

<script>
import commitList from "@/components/commit/F1commitList";
import fileChangeItem from "@/components/file/F1fileChangeItem";
import userAvatar from "@/components/user/F1userAvatar.vue";
import request from "@/utils/request";
import {BACKEND_URL} from "@/utils/config";
import jsCookie from "js-cookie";

export default {
  name: "index",
  components: {
    commitList,
    fileChangeItem,
    userAvatar
  },
  methods: {
    createPullRequest() {
      request.get(BACKEND_URL + `/core/api/pr/new/${this.chooseRep}?title=${this.title}&head_owner=${this.chooseCmpRep.toString().split('/')[0]}&head_repo=${this.chooseCmpRep.toString().split('/')[1]}&head_branch=${this.chooseCmpBranch}&base_branch=${this.chooseBranch}&cmt=${this.cmtVal}`)
          .then(resp => {
            this.$router.push({name: 'pullDetail', params: {index: resp.data.data}})
          })
    },
    updateChooseBr() {
      if (this.chooseCmpRep === 0) {
        return
      }
      this.chooseCmpBranch = 0
      request.get(BACKEND_URL + `/git/web/${this.chooseCmpRep}/listbranches`, {
        headers: {
          'Authorization': jsCookie.get('Authorization')
        }
      }).then(resp => {
        this.cmpBranches = [{value: 0, content: ''},]
        resp.data.data.forEach(br => this.cmpBranches.push({
          value: br,
          content: br,
        }))
      })
      this.updateDiff()
    },
    updateDiff() {
      if (this.chooseBranch === 0 || this.chooseCmpRep === 0 || this.chooseCmpBranch === 0) {
        this.diffCommits = []
        this.diffFiles = []
        this.diffFilesTree = []
        return
      }

      request.get(BACKEND_URL + `/git/web/getPRCommit?base_owner=${this.$route.params.user}&base_repo=${this.$route.params.rep}&base_branch=${this.chooseBranch}&head_owner=${this.me}&head_repo=${this.chooseCmpRep.split('/')[1]}&head_branch=${this.chooseCmpBranch}`).then(resp => {
        this.diffCommits = resp.data.data
      })

      request.get(BACKEND_URL + `/git/web/getPRDiff?base_owner=${this.$route.params.user}&base_repo=${this.$route.params.rep}&base_branch=${this.chooseBranch}&head_owner=${this.me}&head_repo=${this.chooseCmpRep.split('/')[1]}&head_branch=${this.chooseCmpBranch}&recursive=false`).then(resp => {
        this.diffFilesTree = resp.data.data.recur;
        this.diffFiles = resp.data.data.noRecur;
        this.defaultChoose = this.diffFiles[0].file;
        this.oldValue = this.diffFiles[0].old;
        this.newValue = this.diffFiles[0].new;
      })

    },
  },
  mounted() {
    request.get(BACKEND_URL + `/git/web/${this.$route.params.user}/${this.$route.params.rep}/listbranches`, {
      headers: {
        'Authorization': jsCookie.get('Authorization')
      }
    }).then(resp => {
      this.baseBranches = [{value: 0, content: ''},]
      resp.data.data.forEach(br => this.baseBranches.push({
        value: br,
        content: br,
      }))
    })

    request.get(BACKEND_URL + `/core/api/user/myfork/${this.$route.params.user}/${this.$route.params.rep}`, {
      headers: {
        'Authorization': jsCookie.get('Authorization')
      }
    }).then(resp => {
      this.cmpReps = [{value: 0, content: ''},]
      resp.data.data.forEach(rep => this.cmpReps.push({
        value: `${localStorage.getItem('inUser')}/${rep}`,
        content: `${localStorage.getItem('inUser')}/${rep}`,
      }))
    })

  },
  data() {
    return {
      title: '',
      cmtVal: '',
      chooseRep: `${this.$route.params.user}/${this.$route.params.rep}`,
      baseReps: [
        {
          value: `${this.$route.params.user}/${this.$route.params.rep}`,
          content: `${this.$route.params.user}/${this.$route.params.rep}`
        },
      ],
      chooseBranch: 0,
      baseBranches: [{value: 0, content: ''},],
      chooseCmpRep: 0,
      cmpReps: [{value: 0, content: ''},],
      chooseCmpBranch: 0,
      cmpBranches: [{value: 0, content: ''},],

      diffCommits: [],
      diffFiles: [],
      diffFilesTree: [],
      defaultChoose: '',
      oldValue: '',
      newValue: '',

      me: localStorage.getItem('inUser'),
    }
  },
}
</script>

<style scoped>
.comment-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>