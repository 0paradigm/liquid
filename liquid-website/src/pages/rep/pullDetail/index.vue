<template>
  <div style="display: flex; justify-content: center">
    <a-space direction="vertical">
      <div class="header-container">
        <div style="display: flex; justify-content: space-between; margin-bottom: -15px">
          <a-typography-title :heading="2">
            #{{ id }} / {{ title }}
          </a-typography-title>
        </div>
        <div>
          <a-space>
            <a-tag size=large color="purple" style="border-radius: 20px;" v-if="isMerged">
              <template #icon>
                <icon-subscribed/>
              </template>
              Merged
            </a-tag>
            <a-tag size=large color="green" style="border-radius: 20px;" v-else-if="isOpening">
              <template #icon>
                <icon-reply/>
              </template>
              Open
            </a-tag>
            <a-tag size=large color="red" style="border-radius: 20px;" v-else>
              <template #icon>
                <icon-close/>
              </template>
              Closed
            </a-tag>
            <a-typography-paragraph copyable style="margin-top: 20px; margin-left: 0px">
              <user-link :user="openBy"/>
              wants to merge {{ diffCommits.length }} {{ diffCommits.length > 1 ? 'commits' : 'commit' }} into
              <a-button
                  style="border-radius: 10px; background-color: rgb(var(--arcoblue-1)); color: rgb(var(--arcoblue-5))"
                  size="mini">{{ toRepoBranch }}
              </a-button>
              from
              <a-button
                  style="border-radius: 10px; background-color: rgb(var(--arcoblue-1)); color: rgb(var(--arcoblue-5))"
                  size="mini">{{ fromRepoBranch }}
              </a-button>
            </a-typography-paragraph>
          </a-space>
        </div>
      </div>
      <div class="select-container">
        <a-tabs style="width: 1200px" default-active-key="1" :destroy-on-hide="true" lazy-load>
          <a-tab-pane class="pane" key="1">
            <template #title>
              <a-space>
                <div style="font-size: 14px">
                  <icon-message style="font-size: 17px"/>
                  Conversation
                </div>
                <a-badge
                    :count="events.length"
                    :dotStyle="{ background: '#E5E6EB', color: '#86909C' }"
                />
              </a-space>
            </template>
            <div class="content-container">
              <div class="left-side">

                <conversation-list :conversations="events"/>
                <a-divider/>
                <div style="display: flex; justify-content: left">
                  <user-avatar :user="me"/>
                  <a-card style="border-radius:10px;margin-left: 10px">
                    <mavon-editor
                        style="width: 720px; height: 300px; background-color: white !important; border: none !important;"
                        language="en" placeholder="Leave a comment"
                        :subfield=false :autofocus=false v-model="cmtVal"></mavon-editor>
                    <div style="display: flex;justify-content: right;margin-top: 20px">
                      <a-button type="primary" status="success" shape="round" @click="revertOpenState"
                                style="margin-right: 15px" v-if="(currentUserIsEditable || me===openBy) && !isMerged">
                        {{ isOpening ? 'Close' : 'Reopen' }}
                      </a-button>
                      <a-button type="primary" shape="round" style="margin-right: 10px" status="success"
                                v-if="currentUserIsEditable && !isMerged && isOpening" @click="doMerge">Merge
                      </a-button>
                      <a-button type="primary" shape="round" @click="doComment" :disabled="!cmtVal">Comment</a-button>
                    </div>
                  </a-card>
                </div>
              </div>
              <div class="right-side">
                <issue-side-bar :participants="participants"/>
              </div>
            </div>
          </a-tab-pane>

          <a-tab-pane class="pane" key="2">
            <template #title>
              <a-space>
                <div style="font-size: 14px">
                  <icon-up-circle style="font-size: 17px"/>
                  Commits
                </div>
                <a-badge
                    :count="diffCommits.length"
                    :dotStyle="{ background: '#E5E6EB', color: '#86909C' }"
                />
              </a-space>
            </template>
            <commit-list style="width: 900px" :commits="diffCommits" :disable-rollback-and-view="true"/>
          </a-tab-pane>

          <a-tab-pane class="pane" key="3">
            <template #title>
              <a-space>
                <div style="font-size: 14px">
                  <icon-drive-file style="font-size: 17px"/>
                  File changed
                </div>
                <a-badge
                    :count="diffFiles.length"
                    :dotStyle="{ background: '#E5E6EB', color: '#86909C' }"
                />
              </a-space>
            </template>
            <file-change-item :old-value="oldValue" :new-value="newValue" :default-choose="defaultChoose"
                              :origin-tree-data="diffFilesTree" :changes="diffFiles" style="width: 1100px"/>
          </a-tab-pane>
        </a-tabs>
      </div>


    </a-space>

  </div>
</template>

<script>
import userLink from "@/components/user/F1userLink.vue";
import conversationList from "@/components/conversation/F1conversationList";
import commitList from "@/components/commit/F1commitList";
import fileChangeItem from "@/components/file/F1fileChangeItem";
import issueSideBar from "@/components/issue/F1issueSideBar";
import userAvatar from "@/components/user/F1userAvatar.vue";
import dayjs from "dayjs";
import request from "@/utils/request";
import {BACKEND_URL} from "@/utils/config";
import jsCookie from "js-cookie";

export default {
  components: {
    conversationList,
    commitList,
    fileChangeItem,
    userLink,
    issueSideBar,
    userAvatar
  },
  name: "index",
  data() {
    return {
      id: this.$route.params.index,
      title: '',
      isMerged: false,
      isOpening: true,
      openBy: '',
      events: [],
      participants: [],

      fromRepoBranch: 'chris:sync',  // todo
      toRepoBranch: 'liquid:master',  // todo

      diffCommits: [],
      diffFiles: [],
      diffFilesTree: [],
      defaultChoose: '',
      oldValue: '',
      newValue: '',

      chooseBranch: '',  // base branch
      chooseCmpRep: '',  // head repo name
      chooseCmpBranch: '',  // head branch

      dayjs,
      me: localStorage.getItem('inUser'),
      currentUserIsEditable: false,

      cmtVal: '',
    }
  },
  async mounted() {
    await this.init()
    request.get(BACKEND_URL + `/git/web/getPRCommit?base_owner=${this.$route.params.user}&base_repo=${this.$route.params.rep}&base_branch=${this.chooseBranch}&head_owner=${this.openBy}&head_repo=${this.chooseCmpRep}&head_branch=${this.chooseCmpBranch}`)
        .then(resp => {
          this.diffCommits = resp.data.data ?? []
        })

    request.get(BACKEND_URL + `/git/web/getPRDiff?base_owner=${this.$route.params.user}&base_repo=${this.$route.params.rep}&base_branch=${this.chooseBranch}&head_owner=${this.openBy}&head_repo=${this.chooseCmpRep}&head_branch=${this.chooseCmpBranch}&recursive=false`)
        .then(resp => {
          this.diffFilesTree = resp.data?.data?.recur ?? [];
          this.diffFiles = resp?.data?.data?.noRecur || [];
          if (this.diffFiles.length === 0) {
            return
          }
          this.defaultChoose = this.diffFiles[0].file;
          this.oldValue = this.diffFiles[0].old;
          this.newValue = this.diffFiles[0].new;
        })
  },
  methods: {
    async init() {
      request.get(BACKEND_URL + `/core/api/repo/is_collaborator/${this.$route.params.user}/${this.$route.params.rep}/?colab=${localStorage.getItem('inUser')}`)
          .then(resp => {
            this.currentUserIsEditable = resp.data.data
          })
      await request.get(BACKEND_URL + `/core/api/pr/details/${this.$route.params.user}/${this.$route.params.rep}/${this.id}`)
          .then(resp => {
            this.title = resp.data.data.title
            this.isOpening = resp.data.data.isOpening
            this.isMerged = resp.data.data.isMerged
            this.openBy = resp.data.data.openBy
            this.events = resp.data.data.events
            this.participants = resp.data.data.participants
            this.fromRepoBranch = resp.data.data.fromRepoBranch
            this.toRepoBranch = resp.data.data.toRepoBranch
            this.chooseBranch = resp.data.data.chooseBranch
            this.chooseCmpRep = resp.data.data.chooseCmpRep
            this.chooseCmpBranch = resp.data.data.chooseCmpBranch

            // unpacket would be fucking better, but this repo is already a piece of shit
          })
    },

    revertOpenState() {
      request.get(BACKEND_URL + `/core/api/pr/setClosed/${this.$route.params.user}/${this.$route.params.rep}/${this.id}?close=${this.isOpening}`, {
        headers: {
          'Authorization': jsCookie.get('Authorization')
        }
      }).then(resp => {
        this.init()
      })
    },

    doComment() {
      request.post(BACKEND_URL + `/core/api/pr/new_comment/${this.$route.params.user}/${this.$route.params.rep}/${this.id}`, {
        ctx: this.cmtVal
      }, {
        headers: {
          'Authorization': jsCookie.get('Authorization')
        }
      }).then(resp => {
        this.cmtVal = ''
        this.init()
      })
    },

    doMerge() {
      request.get(BACKEND_URL + `/core/api/pr/merge/${this.$route.params.user}/${this.$route.params.rep}/${this.id}`, {
        headers: {
          'Authorization': jsCookie.get('Authorization')
        }
      }).then(resp => {
        this.init()
      })
    }
  }
}
</script>

<style scoped>
.pane {
  display: flex;
  justify-content: center;
}

.content-container {
  display: grid;
  grid-template-columns: 6fr 2fr;
  padding-top: 20px;
}

.left-side {
  grid-column: 1/ span 1;
  padding: 10px 40px;
}

.right-side {
  width: 296px;
  grid-column: 2/ span 1;
  padding-top: 10px;
}

</style>