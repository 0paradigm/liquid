<template>
  <a-space direction="vertical" style="width: 100%">
    <div style="display: flex; justify-content: space-between">
      <a-typography-title :heading="4">
        {{ dateFrom }} - {{ dateTo }}
      </a-typography-title>

      <a-select v-model="chooseSort" style="margin-top:24px;width: 180px; height:33px;border-radius: 10px">

        <a-option v-for="item in sort" :value="item.value" :label="item.content"></a-option>
      </a-select>
    </div>
    <a-divider style="margin-top: 0px"></a-divider>

    <a-card style="border-radius: 10px"
            :header-style="{backgroundColor:'var(--color-neutral-1)', borderTopLeftRadius:'10px', borderTopRightRadius:'10px'}">
      <template #title>
        Overview
      </template>

      <a-space style="width: 100%" direction="vertical">
        <a-row>
          <a-col :flex="6" style="margin-right: 10px">
            <a-progress
                :percent="mergedPullCnt/(openPullCnt+mergedPullCnt)"
                trackColor="rgb(var(--green-7))"
                style="margin-bottom: 10px;"
                size="large"
                :show-text="false"
            >
            </a-progress>
            <a-typography-text>
              <a-typography-text style="font-weight: bold">{{ openPullCnt + mergedPullCnt }}</a-typography-text>
              Active pull requests
            </a-typography-text>
          </a-col>
          <a-col :flex="6" style="margin-left: 10px">
            <a-progress
                :percent="closedIssueCnt/(closedIssueCnt+newIssueCnt)"
                trackColor="rgb(var(--green-7))"
                style="margin-bottom: 10px;"
                size="large"
                :show-text="false"
            >
            </a-progress>
            <a-typography-text>
              <a-typography-text style="font-weight: bold">{{ newIssueCnt + closedIssueCnt }}</a-typography-text>
              Active issues
            </a-typography-text>
          </a-col>
        </a-row>
        <a-divider style="margin-top: 10px"></a-divider>
        <a-row>
          <a-col :flex="1">
            <a-space align="center" direction="vertical" size="small" style="width: 100%">
              <a-typography-text style="font-size:15px;font-weight: bold">
                <icon-minus-circle size="17" style="color:rgb(var(--arcoblue-7))"/>
                {{ mergedPullCnt }}
              </a-typography-text>

              <t-link hover="color" style="margin-top: -20px">
                Closed pull requests
              </t-link>
            </a-space>
          </a-col>
          <a-col :flex="1">
            <a-space align="center" direction="vertical" size="small" style="width: 100%">
              <a-typography-text style="font-size:15px;font-weight: bold">
                <icon-check-circle size="17" style="color:rgb(var(--green-7))"/>
                {{ openPullCnt }}
              </a-typography-text>

              <t-link hover="color" style="margin-top: -20px">
                Open pull request
              </t-link>
            </a-space>
          </a-col>

          <a-divider direction="vertical"/>
          <a-col :flex="1">
            <a-space align="center" direction="vertical" size="small" style="width: 100%">
              <a-typography-text style="font-size:15px;font-weight: bold">
                <icon-check-circle size="17" style="color:rgb(var(--arcoblue-7))"/>
                {{ newIssueCnt }}
              </a-typography-text>

              <t-link hover="color" style="margin-top: -20px">
                Opening issue
              </t-link>
            </a-space>
          </a-col>
          <a-col :flex="1">
            <a-space align="center" direction="vertical" size="small" style="width: 100%">
              <a-typography-text style="font-size:15px;font-weight: bold">
                <icon-minus-circle size="17" style="color:rgb(var(--green-7))"/>
                {{ closedIssueCnt }}
              </a-typography-text>

              <t-link hover="color" style="margin-top: -20px">
                Closed issue
              </t-link>
            </a-space>
          </a-col>
        </a-row>
      </a-space>
    </a-card>

    <a-row style="margin-top: 10px" :gutter="24">
      <a-col :flex="1" style="margin-left: 5px">
        <a-typography-paragraph style="font-size:15px; font-weight:lighter; max-width: 600px">
          Focusing on the project's development progress, we found
          <a-typography-text style="font-weight: normal">{{ this.uniqToMaster }}
            {{ this.uniqToMaster > 1 ? 'authors' : 'author' }}
          </a-typography-text>
          have pushed
          <a-typography-text style="font-weight: normal">{{ this.masterCommits }}
            {{ this.masterCommits > 1 ? 'commits' : 'commit' }}
          </a-typography-text>
          to master.

        </a-typography-paragraph>
      </a-col>
      <a-col :flex="1">
        <bar-chart style="width: 100%; height: 200px" :data="commitFreq"/>
      </a-col>
    </a-row>

    <a-divider :margin="15" orientation="center">
      <div style="display: flex; flex-direction: row">
        <icon-branch></icon-branch>
        <div class="num">{{ mergedPullCnt }}</div>
        <div class="text">Pull request merged by</div>
        <div class="num">{{ mergedPullCnt > 0 ? 1 : 0 }}</div>
        <div class="text">person</div>
      </div>
    </a-divider>
  </a-space>
</template>

<script>
import barChart from "@/components/chart/F1barChart";
import userAvatar from "@/components/user/F1userAvatar.vue";
import request from "@/utils/request";
import {BACKEND_URL} from "@/utils/config";

export default {
  name: "pulsePart",
  components: {
    barChart,
    userAvatar
  },
  mounted() {
    request.get(BACKEND_URL + `/core/api/repo/verify_accessable/${this.$route.params.user}/${this.$route.params.rep}`)
    request.get(BACKEND_URL + `/core/api/issue/issuelist/${this.$route.params.user}/${this.$route.params.rep}`)
        .then(resp => {
          this.newIssueCnt = (resp.data?.data?.opens || []).length
          this.closedIssueCnt = (resp.data?.data?.closes || []).length
        })
    request.get(BACKEND_URL + `/core/api/pr/list/${this.$route.params.user}/${this.$route.params.rep}`)
        .then(resp => {
          console.log(resp.data.data)
          this.openPullCnt = (resp.data?.data?.opens ?? []).length
          this.mergedPullCnt = (resp.data?.data?.closes ?? []).length
          let uniqAuth = new Set()
          resp.data.data.opens.forEach(item => {
            uniqAuth.add(item.openBy)
          })
          this.ActiveAuthorNum = uniqAuth.size
        })
    request.get(BACKEND_URL + `/git/web/listcommits/${this.$route.params.user}/${this.$route.params.rep}/master`)
        .then(resp => {
          const commits = resp.data.data  // [{ts:number, user:str}]

          let uniqCommit2Master = new Set()
          commits.forEach(item => {
            uniqCommit2Master.add(item.user)
          })
          this.uniqToMaster = uniqCommit2Master.size
          this.masterCommits = commits.length

          this.commitFreq = [['commitCnt', 'AuthorName'],]
          let committerAndFreq = new Map()
          commits.forEach(item => {
            if (committerAndFreq.has(item.user)) {
              committerAndFreq.set(item.user, committerAndFreq.get(item.user) + 1)
            } else {
              committerAndFreq.set(item.user, 1)
            }
          })
          committerAndFreq.forEach((value, key) => {
            this.commitFreq.push([value, key])
          })
        })
  },
  data() {
    return {
      dateFrom: 'December 31, 2022',
      dateTo: 'January 7, 2023',
      chooseSort: 'C',
      sort: [{value: 'C', content: '1 week'},],
      mergedPullCnt: 0,
      openPullCnt: 0,
      closedIssueCnt: 0,
      newIssueCnt: 0,
      ActiveAuthorNum: 0,
      masterCommits: 0,
      uniqToMaster: 0,
      commitFreq: [['commitCnt', 'AuthorName'],]
    }
  }
}
</script>

<style scoped>
.num {
  font-family: "Arial Black";
  margin-left: 5px;
  font-size: 18px;
}

.text {
  color: dimgrey;
  margin-left: 5px;
  font-size: 18px;
}
</style>