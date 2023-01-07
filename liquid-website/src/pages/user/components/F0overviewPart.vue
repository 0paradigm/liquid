<template>
  <a-space :size="30" direction="vertical" style="margin-left: 10px">
    <a-space direction="vertical" >
      <a-typography-text class="text-item">
        {{$t('popularRepo')}}
      </a-typography-text>
      <a-row :gutter="24">
        <a-col span="12" v-for="i in 2">
          <a-card style="border-radius: 10px" hoverable>
            <a-space direction="vertical">
              <div style="display: flex;justify-content: space-between">
                <a-space direction="vertical" :size="0">
                  <a-link style="margin-bottom:-10px;font-size: 15px">Online judge</a-link>
                  <a-typography-text style="margin-left:5px;font-size: 10px; color: var(--color-neutral-6)">Fork from
                    <t-link style="font-size: 10px;color: var(--color-neutral-6)" hover="color">QingdaoU/OnlineJudge</t-link>
                  </a-typography-text>
                </a-space>
                <a-tag color="green" style="border-radius: 10px">
                  public
                </a-tag>
              </div>
              <a-typography-paragraph style="font-size:12px;margin-left:5px;color: var(--color-neutral-6)">
                open source online judge based on Vue, Django and Docker. | 青岛大学开源 Online Judge | QQ群 496710125 | admin@qduoj.com
              </a-typography-paragraph>

              <a-tag color="arcoblue">
                Python
              </a-tag>
            </a-space>
          </a-card>
        </a-col>

      </a-row>
    </a-space>

    <a-space direction="vertical" style="width: 100%">
      <a-typography-text class="text-item" style="width: 100%">39 {{$t('contribution')}} in the last year</a-typography-text>
      <a-card style="border-radius: 10px;" hoverable>
        <heatmap-chart style="height: 200px;"></heatmap-chart>
      </a-card>
    </a-space>

    <a-space direction="vertical">
      <a-typography-text class="text-item">
        {{$t('contributionAct')}}
      </a-typography-text>
      <a-timeline>
        <a-timeline-item v-for="i in 2">
          <a-space direction="vertical" :size="1" style="margin-bottom: 30px">
            <div style="font-size: 17px; font-weight: lighter">
              <icon-up-circle size="large"/> Created 2 commit in 2 repository
            </div>
            <a-typography-text v-for="i in 2" style="color: var(--color-neutral-6)">
              <a-link>MatthewLENG2002/cs309-ui</a-link> 12 commits
            </a-typography-text>
            <a-typography-text style="color: var(--color-neutral-6)">
              2017-03-10
            </a-typography-text>

          </a-space>
        </a-timeline-item>
      </a-timeline>
    </a-space>

  </a-space>
</template>

<script>
import heatmapChart from "@/components/chart/F0heatmapChart";

export default {
  name: "overviewPart",
  components:{
    heatmapChart
  }
}
</script>

<style scoped>
.text-item{
  font-size: 16px;
}

</style>
