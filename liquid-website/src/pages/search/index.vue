<template>
  <div style="display: flex; justify-content: center;">
    <a-space direction="vertical">

      <a-input-search style="margin-top:30px; height: 40px; width: 1000px; border-radius: 20px"></a-input-search>
      <a-row style="margin-bottom: 16px;">
        <!--        <a-col flex="100px">-->
        <!--          <a-radio-group v-model="chooseType" style="margin-top:20px;margin-right:20px;border-radius: 10px" direction="vertical" type="button" size="large">-->
        <!--            <a-space direction="vertical" align="center">-->
        <!--            <a-radio class="radio-item" v-for="type in types" :value="type.value">-->
        <!--              <div style="display: flex; justify-content: space-between">-->
        <!--                <div>{{type.value}}</div>-->
        <!--                <a-badge-->
        <!--                    :count=type.cnt-->
        <!--                    :dotStyle="{ background: 'rgb(var(&#45;&#45;arcoblue-6))'}"-->
        <!--                    style="margin-top: 4px"-->
        <!--                />-->
        <!--              </div>-->

        <!--            </a-radio>-->
        <!--            </a-space>-->
        <!--          </a-radio-group>-->
        <!--        </a-col>-->
        <a-col flex="auto">

          <a-typography-title :heading="4" style="margin-left: 50px">
            {{ totalCnt }} Repository results
          </a-typography-title>

          <a-divider style="margin-top: 5px; margin-bottom: 0px"></a-divider>
          <a-space direction="vertical" style="width: 100%">
            <a-list :bordered="false" style="width: 100%">
              <a-list-item v-for="item in repoList">
                <rep-item :repo="item.repo" :user="item.user" :description="item.language" :star="item.star" :language="item.language"
                          style="margin-left: 80px"/>
              </a-list-item>
            </a-list>
            <div style="display: flex; justify-content: center">
              <a-pagination :total="this.repoList.length" show-jumper/>
            </div>
          </a-space>
        </a-col>
      </a-row>

    </a-space>


  </div>
</template>

<script>
import repItem from "@/pages/search/components/repItem";
import commitItem from "@/pages/search/components/commitItem";
import issueItem from "@/pages/search/components/issueItem";
import topicItem from "@/pages/search/components/topicItem";
import userItem from "@/pages/search/components/userItem";

export default {
  name: "index",
  created() {
    document.title = 'Search-liquid'
  },
  components: {
    repItem,
    commitItem,
    issueItem,
    topicItem,
    userItem
  },
  mounted() {
    this.repoList = [
      {repo: 'repo1', user: 'user1', description: 'lalala', star: 4, language: 'java'},
      {repo: 'repo2', user: 'user2', description: 'hahahahahah', star: 40, language: 'python'},
    ]
  },
  data() {
    return {
      repoList: []
    }
  }
}
</script>

<style scoped>
.radio-item {
  text-align: center;
  width: 200px;
  border-radius: 10px;
}
</style>