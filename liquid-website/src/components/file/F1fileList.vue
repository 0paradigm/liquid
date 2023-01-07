<template>

  <div class="content-side">
    <a-spin :loading="isLoading" style="width: 100%" dot>
      <a-list hoverable style="background-color: white">
        <template #header style="background-color: var(--color-neutral-1)">
          <div style="display: flex; justify-content: space-between">
            <a-space>
              <user-avatar :size="28" :user="this.latestUser" style="padding-right: 1px"/>
              <user-link :user="this.latestUser"/>
              <t-link hover="color" style="font-size: 12px; color: var(--color-neutral-6)"
                      @click="this.$router.push({name: 'commitDetail', params: {branch: this.$route.params.branch || 'master', SHA: this.latestSha}})"
              >
                <a-typography-text style="font-size: 14px; color: var(--color-neutral-7); padding-right: 5px">
                  {{ latestMsg }}
                </a-typography-text>
              </t-link>
            </a-space>
            <a-space>
              <t-link hover="color" style="font-size: 12px; color: var(--color-neutral-6)"
                      @click="this.$router.push({name: 'commitDetail', params: {branch: this.$route.params.branch || 'master', SHA: this.latestSha}})"
              >
                {{ this.latestSha.substring(0, 8) }}
              </t-link>
              <a-typography-text style="font-size: 12px; color: var(--color-neutral-6); padding-right: 5px">
                {{ dayjs.unix(this.latestTime).format('YY-MM-DD') }}
              </a-typography-text>
              <t-link @click="jumpToCommit" hover="color">
                <icon-schedule/>
                <div v-if="cmtCnt > 0">
                  &nbsp;{{ this.cmtCnt }} commits
                </div>
                <div v-else>
                  &nbsp;History
                </div>
              </t-link>
            </a-space>
          </div>
        </template>

        <a-list-item v-for="item in getFileList">
          <a-row>
            <a-col :flex="17">
              <icon-file v-if="!item.isDir" size="17px"/>
              <icon-folder v-if="item.isDir" size="17px"/>
              <a @click="jumpToTreeOrBlob(item.name, item.isDir)" style="text-decoration: none">
                <t-tag max-width="300" style="background: transparent;">{{ item.name }}</t-tag>
              </a>

            </a-col>
            <a-col :flex="8">
              <a-link style="color: black"
                      @click="this.$router.push({name: 'commitDetail', params: {branch: this.$route.params.branch || 'master', SHA: item.sha}})">
                {{ item.message }}
              </a-link>
            </a-col>
            <a-col :flex="5" style="display:flex;justify-content: right; margin-right: 10px">
              <a-link style="color: black"
                      @click="this.$router.push({name: 'commitDetail', params: {branch: this.$route.params.branch || 'master', SHA: item.sha}})">
                {{ dayjs.unix(item.timestamp).format('YY-MM-DD HH:mm') }}
              </a-link>
            </a-col>
          </a-row>

        </a-list-item>
      </a-list>

      <a-card v-if="readme" title="README.md" style="margin-top: 30px; border-radius: 10px">
        <mavon-editor v-model="getReadmeValue"
                      style="width: 100%; height: 100%; background-color: white !important; border: none !important;"
                      language="en" defaultOpen="preview"
                      :toolbarsFlag="false" :editable="false" :subfield=false :autofocus=false :boxShadow="false"/>
      </a-card>
    </a-spin>
  </div>
</template>

<script>
import dayjs from 'dayjs'
import {mapActions, mapGetters} from "vuex";
import userAvatar from "@/components/user/F1userAvatar.vue";
import userLink from "@/components/user/F1userLink.vue";
import request from "@/utils/request";
import {BACKEND_URL} from "@/utils/config";

export default {
  name: "fileList",
  components: {
    userAvatar,
    userLink
  },
  data() {
    return {
      dayjs,
      owner: this.$route.params.user,
      rep: this.$route.params.rep,
      latestUser: '',
      latestSha: '',
      latestTime: 0,
      latestMsg: '',
      cmtCnt: 0,
    }
  },
  mounted() {
    this.init()
  },


  computed: {
    ...mapGetters('rep', ['getFileList', 'isLoading', 'readme', 'getReadmeValue']),
  },

  watch: {
    $route() {
      if (this.$route.name === 'tree' || this.$route.name === 'code' || !!this.$route.params.branch) {
        this.init()
      }
    },
  },

  methods: {
    ...mapActions('rep', ['initFileList']),
    init() {
      this.initFileList([this.owner, this.rep, this.$route.params.branch || 'master', this.$route.params.dir])
      let reqUrl = BACKEND_URL + `/git/web/latest/${this.$route.params.user}/${this.$route.params.rep}/${this.$route.params.branch || 'master'}`;
      if (this.$route.params.dir) {
        let path = ''
        this.$route.params.dir.forEach(r => path += r + '/')
        reqUrl += `?relPath=` + encodeURI(path)
      }
      request.get(reqUrl)
          .then(resp => {
            this.latestSha = resp.data.latest.sha ?? ''
            this.latestUser = resp.data.latest.user ?? ''
            this.latestTime = resp.data.latest.timestamp ?? 1672531200
            this.latestMsg = resp.data.latest.message ?? ''
            this.cmtCnt = resp.data.cnt ?? 0
            if (this.$route.params.dir) {
              this.cmtCnt = -1
            }
          })
    },
    jumpToCommit() {
      this.$router.push({name: 'commit', params: {branch: this.$route.params.branch || 'master'}})
    },
    jumpToTreeOrBlob(name, isDir) {
      const dir = [];
      if (!!this.$route.params.dir) {
        this.$route.params.dir.forEach(d => dir.push(d))
      }
      dir.push(name)
      if (isDir) {
        this.$router.push({
          name: 'tree',
          params: {
            branch: this.$route.params.branch || 'master',
            dir: dir
          }
        })
      } else {
        this.$router.push({
          name: 'blob',
          params: {
            branch: this.$route.params.branch || 'master',
            dir: dir
          }
        })
      }
    },
  }
}
</script>
