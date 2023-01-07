<template>
  <a-space direction="vertical" style="width: 950px; margin-top: 20px">
    <a-card  style="border-radius: 10px;"
             :header-style=headerStyle>
      <template #title>
        View
      </template>
    <line-chart style="height: 250px; margin-bottom: -20px"></line-chart>
    </a-card>

    <a-row :gutter="24" style="margin-top: 15px">
      <a-col span="12">
        <a-card style="border-radius: 10px;padding: 0px" :body-style="{padding:'0px'}" :header-style="headerStyle">
          <template #title>
            Referring sites
          </template>
          <a-table :pagination="false" :columns="siteColumns" :data="siteData" :bordered="false">
          </a-table>
        </a-card>




      </a-col>
      <a-col span="12">
        <a-card style="border-radius: 10px;padding: 0px" :body-style="{padding:'0px'}" :header-style="headerStyle">
          <template #title>
            Popular content
          </template>
          <a-table :hoverable='false' :pagination="false" :columns="contentColumns" :data="contentData" :bordered="false">
            <template #content="{ rowIndex }">
              <a-link>{{contentData[rowIndex].content}}</a-link>
<!--              <a-input v-model="contentData[rowIndex].content" />-->
            </template>
          </a-table>
        </a-card>
      </a-col>
    </a-row>
  </a-space>
</template>

<script>
import lineChart from "@/components/chart/F0lineChart";

export default {
  name: "trafficPart",
  components: {
    lineChart
  },
  data(){
    return{
      headerStyle:{backgroundColor:'var(--color-neutral-1)', borderTopLeftRadius:'10px', borderTopRightRadius:'10px'},
      siteColumns:[
        {title: 'Site', dataIndex: 'site'},
        {title: 'Views', dataIndex: 'view'},
        {title: 'Unique visitors', dataIndex: 'visitor'},
      ],
      contentColumns:[
        {title: 'Content', dataIndex: 'content', slotName: 'content'},
        {title: 'Views', dataIndex: 'view'},
        {title: 'Unique visitors', dataIndex: 'visitor'}
      ],
      siteData:[
        {site:'github.com', view:13, visitor:123},
        {site:'github.com', view:13, visitor:123}
      ],
      contentData: [
        {content:'MatthewLENG2002/cs309-ui', view:23, visitor:323, url:''}
      ]
    }
  }
}
</script>

<style scoped>


</style>