<template>
  <div class="page">
    <div class="leftpanel">
      <div class="username">
        <user-avatar :size="25" :user="me" v-if="me"/>
        <user-link :user="me" v-if="me"/>
        <div v-else style="width: 200px">
          You are not logged in.
        </div>
      </div>
      <a-divider/>

      <div>
        <a-list :split="false" :bordered="false">
          <template #header>
            <div style="display: flex; flex-direction: row; justify-content: space-between;">
              <div style="font-size: 14px">{{ $t('topRepositories') }}</div>
            </div>
          </template>
          <a-list-item v-for="item in repos">
            <a-list-item-meta>
              <template #avatar>
                <div class="repo-item">
                  <user-avatar :user="item.userName" :size="20"/>
                  <a-link @click="this.$router.push({name:'code', params:{user:item.userName, rep:item.repoName}})"
                          :hoverable="false"
                          style="justify-content: flex-start; margin-left: 10px">
                    {{ item.userName }}/{{ item.repoName }}
                  </a-link>
                </div>
              </template>
            </a-list-item-meta>
            <template #actions>

            </template>
          </a-list-item>
        </a-list>
      </div>
    </div>


    <div class="centerpanel">
      <a-tabs default-active-key="1" v-model:active-key="informType" style="margin-top: 9px">
        <a-tab-pane key="1" title="Followed Issues">
          <a-list>
            <a-list-item v-for="item in inform_issue_list">
              <a-list-item-meta>
                <template #title>
                  <div style="display: flex; flex-direction: row">
                    <a-link @click="this.$router.push({name: 'user', params: {user: item.userName}})"
                            :hoverable="false"
                            style="justify-content: flex-start; margin-left: 5px">
                      {{ item.userName }}
                    </a-link>
                    <div style="margin-left: 5px"> raised a new issue in</div>
                    <a-link
                        @click="this.$router.push({name:'code', params:{user:item.repoOwnerName, rep:item.repoName}})"
                        :hoverable="false"
                        style="justify-content: flex-start; margin-left: 5px">
                      {{ item.repoOwnerName }}/{{ item.repoName }}
                    </a-link>
                    <div style="margin-left: 5px; color: dimgrey;"> {{
                        dayjs.unix(item.time / 1000).format("YYYY-MM-DD")
                      }}
                    </div>
                  </div>
                </template>
                <template #avatar>
                  <user-avatar style="size: 15px" :user="item.userName"/>
                </template>
              </a-list-item-meta>
              <a-card
                  class="card-demo"
                  @click=" this.$router.push({name: 'issueDetail', params: {user:item.repoOwnerName, rep:item.repoName, index: item.issueId}})"
                  hoverable
              >
                <f1issue-item :is-closed="item.issueIsClose" :id="item.issueId" :tags="item.issueTags"
                              :user="item.userName" :title="item.issueTitle" :chat-cnt="item.issueCmtCnt"
                              :open-at="item.time"/>
              </a-card>
            </a-list-item>
          </a-list>
        </a-tab-pane>

        <a-tab-pane key="2" title="Followed Pull Requests">
          <a-list>
            <a-list-item v-for="item in inform_pr_list">
              <a-list-item-meta>
                <template #title>
                  <div style="display: flex; flex-direction: row">
                    <a-link @click="this.$router.push({name: 'user', params: {user: item.userName}})"
                            :hoverable="false"
                            style="justify-content: flex-start; margin-left: 5px">
                      {{ item.userName }}
                    </a-link>
                    <div style="margin-left: 5px"> pushed to</div>
                    <a-link
                        @click="this.$router.push({name:'code', params:{user:item.repoOwnerName, rep:item.repoName}})"
                        :hoverable="false"
                        style="justify-content: flex-start; margin-left: 5px">
                      {{ item.repoOwnerName }}/{{ item.repoName }}
                    </a-link>
                    <div style="margin-left: 5px; color: dimgrey;"> {{
                        dayjs.unix(item.time / 1000).format("YYYY-MM-DD")
                      }}
                    </div>
                  </div>
                </template>
                <template #avatar>
                  <user-avatar style="size: 15px" :user="item.userName"/>
                </template>
              </a-list-item-meta>
              <a-card
                  class="card-demo"
                  @click=" this.$router.push({name: 'pullDetail', params: {user:item.repoOwnerName, rep:item.repoName, index: item.prId}})"
                  hoverable
              >
                <f1pull-item :id="item.prId" :is-closed="item.prIsClose" :title="item.prTitle"
                             :open-by="item.userName" :open-at="item.time" :cmt-cnt="item.prCmtCnt"/>
              </a-card>
            </a-list-item>
          </a-list>
        </a-tab-pane>
      </a-tabs>

    </div>


    <div class="rightpanel">
      <div class="timeline">
        <a-timeline>
          <a-timeline-item label="2023-1-4" lineType="dashed">
            Liquid 1.1.0 released 🎉
            <br/>
            <a-typography-text
                type="secondary"
                :style="{ fontSize: '12px', marginTop: '4px' }"
            >
              This release contains several import improvements that can make your experience better. Please check the
              changelog for more...
            </a-typography-text>
          </a-timeline-item>
          <a-timeline-item label="2023-1-3" lineType="dashed">
            PR diff is now available in a clearer way
            <br/>
            <a-typography-text
                type="secondary"
                :style="{ fontSize: '12px', marginTop: '4px' }"
            >
              New year, new features and improvements! 🎆 We're making URLs in Projects more powerful with direct links
              to project READMEs...
            </a-typography-text>
          </a-timeline-item>
          <a-timeline-item label="2023-1-3" lineType="dashed">
            Three days. Eleven votes. Still no US House Speaker
            <br/>
            <a-typography-text
                type="secondary"
                :style="{ fontSize: '12px', marginTop: '4px' }"
            >
              The Republican leader of the House of Representatives, Kevin McCarthy, has failed in his latest bid to get
              elected Speaker...
            </a-typography-text>
          </a-timeline-item>
        </a-timeline>
      </div>
      <a-divider/>
      <div class="explore">
        <a-list :split="false" :bordered="false">
          <template #header>
            {{ $t('exploreRepositories') }}
          </template>
          <a-list-item v-for="item in recentRepos">
            <a-list-item-meta>
              <template #avatar>
                <div class="repo-item">
                  <user-avatar :user="item.userName" :size="20"/>
                  <a-link @click="this.$router.push({name:'code', params:{user:item.userName, rep:item.repoName}})"
                          :hoverable="false"
                          style="justify-content: flex-start; margin-left: 10px">
                    {{ item.userName }}/{{ item.repoName }}
                  </a-link>
                </div>
              </template>
            </a-list-item-meta>
            <template #actions>

            </template>
          </a-list-item>
        </a-list>
      </div>


    </div>
  </div>


</template>

<script>
import userLink from "@/components/user/F1userLink.vue";
import userAvatar from "@/components/user/F1userAvatar.vue";
import dayjs from 'dayjs';
import F1issueItem from "@/components/issue/F1issueItem";
import F1pullItem from "@/components/pull/F1pullItem";
import request from "@/utils/request";
import jsCookie from "js-cookie";
import {BACKEND_URL} from "@/utils/config";

export default {
  name: "index",
  components: {userAvatar, userLink, F1issueItem, F1pullItem},
  created() {
    document.title = 'Home - liquid'
  },
  mounted() {
    this.recentRepos = JSON.parse(localStorage.getItem('recents') || '[]')

    request.get(BACKEND_URL + `/core/api/user//allsiterepo`, {
      headers: {
        'Authorization': jsCookie.get('Authorization')
      }
    }).then(resp => this.repos = resp.data.data)
    request.get(BACKEND_URL + `/core/api/issue/watchingrepos`, {
      headers: {
        'Authorization': jsCookie.get('Authorization')
      }
    }).then(resp => this.inform_issue_list = resp.data.data)
    request.get(BACKEND_URL + `/core/api/pr/listwatching`, {
      headers: {
        'Authorization': jsCookie.get('Authorization')
      }
    }).then(resp => this.inform_pr_list = resp.data.data)
  },
  data() {
    return {
      informType: '1',
      dayjs,
      me: localStorage.getItem('inUser'),
      repos: [],
      inform_issue_list: [],
      inform_pr_list: [],
      recentRepos: []
    }
  }
}
</script>

<style scoped>
.username {
  margin-top: 20px;
  margin-left: 20px;
  margin-bottom: 20px;
}

.page {
  display: flex;
  flex-direction: row;
  height: 100%;
  align-content: space-around;

}

.leftpanel {
  width: 20%;
  height: 100%;
  margin-top: 10px;
  margin-right: 5px;
  margin-left: 5px;
}

.username {
  display: grid;
  grid-template-columns: 15% 80%;
}

.repo-item {
  display: flex;
  grid-template-columns: 1fr 1fr;
}

.centerpanel {
  width: 60%;
  height: 100%;
  /*background-color: #fff;*/
  margin-top: 10px;
  margin-right: 5px;
  margin-left: 5px;
}

.card-demo {
  /*width: 360px;*/
  margin-left: 50px;
  transition-property: all;
}

.card-demo:hover {
  transform: translateY(-5px);
}

.rightpanel {
  display: flex;
  justify-content: center;
  flex-direction: column;
  width: 30%;
  height: 100%;
  margin-top: 10px;
  margin-right: 5px;
  margin-left: 5px;
}
</style>