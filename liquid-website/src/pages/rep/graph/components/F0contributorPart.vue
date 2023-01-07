<template>
  <a-space direction="vertical" style="width: 950px">
    <div style="display: flex; justify-content: space-between">
      <a-typography-title :heading="4">
        {{dateFrom}} - {{dateTo}}
      </a-typography-title>


      <a-select v-model="chooseSort" style="margin-top:24px;width: 220px; height:33px;border-radius: 10px">
        <template #label="{data}">

          <a-typography-text style="margin-right:5px;font-weight: bold; color: var(--color-neutral-6)">Contributions: </a-typography-text>{{data.label}}
        </template>
        <a-option v-for="item in sort" :value="item.value" :label="item.content"></a-option>
      </a-select>
    </div>
    <a-divider style="margin-top: 0px"></a-divider>
    <a-typography-paragraph style="margin-top: -10px; color: var(--color-neutral-6)">
      Contributions to main, excluding merge commits and bot accounts
    </a-typography-paragraph>
    <line-chart class="chart"></line-chart>

    <a-row :gutter="[24, 24]" >
      <a-col v-for="i in 3" :span="12">
        <a-card style="border-radius: 10px" :header-style="{height:'50px'}">
          <template #title>
            <a-space>
              <user-avatar :size="30"></user-avatar>
              <a-space style="margin-top:0px;margin-left: 0px" direction="vertical" size="small">
                <div style="margin-bottom: -15px">
                  <user-link size="17px">
                  </user-link>
                </div>
                <a-space >
                  <a-typography-text class="text" style="color: var(--color-neutral-6)">18 commits</a-typography-text>
                  <a-typography-text class="text" style="color: rgb(var(--green-6))">43,847 ++</a-typography-text>
                  <a-typography-text class="text" style="color: rgb(var(--red-6))">50,643 --</a-typography-text>
                </a-space>
              </a-space>
            </a-space>
          </template>
          <template #extra>
            <a-typography-text> #1</a-typography-text>
          </template>

          <line-chart style="height: 250px"></line-chart>
        </a-card>
      </a-col>
    </a-row>

  </a-space>
</template>

<script>
import lineChart from "@/components/chart/F0lineChart";
import userAvatar from "@/components/user/F1userAvatar.vue";
import userLink from "@/components/user/F1userLink.vue";

export default {
  name: "contributorPart",
  components: {
    lineChart,
    userAvatar,
    userLink
  },
  data(){
    return{
      dateFrom: 'November 27, 2022',
      dateTo: 'December 4, 2022',
      chooseSort: 'C',
      sort: [
        {value: 'A', content: 'Additions'},
        {value: 'B', content: 'Deletions'},
        {value: 'C', content: 'Commits'},
      ],
    }
  }
}
</script>

<style scoped>
.chart {
  height: 300px;
}
.text{
  font-size: 10px;
}
</style>