<template>
  <a-space direction="vertical" style="margin-top: 50px">
    <commit-heat-map style="height: 200px; width: 850px; margin-left: 50px; font-size: 10px" :time-value="commitHM"/>
    <week-commit-chart style="height: 350px; width: 950px" :data1="commitFreq"/>

  </a-space>

</template>

<script>
import lineChart from "@/components/chart/F0lineChart";
import weekCommitChart from "@/components/chart/F1weekCommitChart";
import WeekCommitChart from "@/components/chart/F1weekCommitChart";
import commitHeatMap from "@/components/chart/F1commitHeatMap";
import request from "@/utils/request";
import {BACKEND_URL} from "@/utils/config";

export default {
  name: "commitPart",
  components: {
    WeekCommitChart,
    lineChart,
    weekCommitChart,
    commitHeatMap
  },
  data() {
    return {
      commitFreq: [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,],
      commitHM: []
    }
  },
  mounted() {
    request.get(BACKEND_URL + `/core/api/repo/verify_accessable/${this.$route.params.user}/${this.$route.params.rep}`)
    request.get(BACKEND_URL + `/git/web/listcommits/${this.$route.params.user}/${this.$route.params.rep}/master`)
        .then(resp => {
          const commits = resp.data.data  // [{ts:number, user:str}]

          let uniqCommit2Master = new Set()
          commits.forEach(item => {
            uniqCommit2Master.add(item.user)
          })
          this.uniqToMaster = uniqCommit2Master.size
          this.masterCommits = commits.length
          this.commitFreq = []
          let days = {
            '1/1/2023 AM': 0,
            '1/1/2023 PM': 0,
            '1/2/2023 AM': 0,
            '1/2/2023 PM': 0,
            '1/3/2023 AM': 0,
            '1/3/2023 PM': 0,
            '1/4/2023 AM': 0,
            '1/4/2023 PM': 0,
            '1/5/2023 AM': 0,
            '1/5/2023 PM': 0,
            '1/6/2023 AM': 0,
            '1/6/2023 PM': 0,
            '1/7/2023 AM': 0,
            '1/7/2023 PM': 0,
          }
          commits.forEach(item => {
            let date = new Date(item.ts * 1000)
            let day = date.toLocaleDateString()
            let hour = date.getHours()
            let key = day + ' ' + (hour < 12 ? 'AM' : 'PM')
            days[key] = days[key] + 1
          })
          this.commitFreq.push(days['1/1/2023 AM'])
          this.commitFreq.push(days['1/1/2023 PM'])
          this.commitFreq.push(days['1/2/2023 AM'])
          this.commitFreq.push(days['1/2/2023 PM'])
          this.commitFreq.push(days['1/3/2023 AM'])
          this.commitFreq.push(days['1/3/2023 PM'])
          this.commitFreq.push(days['1/4/2023 AM'])
          this.commitFreq.push(days['1/4/2023 PM'])
          this.commitFreq.push(days['1/5/2023 AM'])
          this.commitFreq.push(days['1/5/2023 PM'])
          this.commitFreq.push(days['1/6/2023 AM'])
          this.commitFreq.push(days['1/6/2023 PM'])
          this.commitFreq.push(days['1/7/2023 AM'])
          this.commitFreq.push(days['1/7/2023 PM'])

          this.commitHM = []
          this.commitHM.push({date: "2023-01-01", count: days['1/1/2023 AM'] + days['1/1/2023 PM']})
          this.commitHM.push({date: "2023-01-02", count: days['1/2/2023 AM'] + days['1/2/2023 PM']})
          this.commitHM.push({date: "2023-01-03", count: days['1/3/2023 AM'] + days['1/3/2023 PM']})
          this.commitHM.push({date: "2023-01-04", count: days['1/4/2023 AM'] + days['1/4/2023 PM']})
          this.commitHM.push({date: "2023-01-05", count: days['1/5/2023 AM'] + days['1/5/2023 PM']})
          this.commitHM.push({date: "2023-01-06", count: days['1/6/2023 AM'] + days['1/6/2023 PM']})
          this.commitHM.push({date: "2023-01-07", count: days['1/7/2023 AM'] + days['1/7/2023 PM']})
        })
  }
}
</script>
