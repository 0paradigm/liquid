<template>
  <div style="display: flex; justify-content: center; ">
    <a-row style="margin-bottom: 16px;">
      <a-col flex="100px">
        <a-radio-group v-model="choose" @change="handleChange"
                       style="margin-top:20px;margin-right:20px;border-radius: 5px" direction="vertical" type="button"
                       size="large">
          <a-space direction="vertical" align="center">
            <a-radio class="radio-item" v-for="type in types" :value="type.value"
                     style="border-radius: 4px !important;">
              <div style="display: flex; justify-content: space-between">
                <div>{{ type.label}}</div>
              </div>
            </a-radio>
          </a-space>
        </a-radio-group>
      </a-col>
      <a-col flex="auto">
        <pulse-part v-if="choose==='pulse'" style="width: 950px;"/>
<!--        <contributor-part v-if="choose==='contributors'"/>-->
<!--        <traffic-part v-if="choose==='traffic'"/>-->
        <commit-part v-if="choose==='commit'"/>
        <fork-part v-if="choose==='fork'"/>
        <star-part v-if="choose==='star'"/>
        <watch-part v-if="choose==='watch'"/>
      </a-col>
    </a-row>
  </div>

</template>

<script>
import commitPart from "@/pages/rep/graph/components/F1commitPart";
import contributorPart from "@/pages/rep/graph/components/F0contributorPart";
import forkPart from "@/pages/rep/graph/components/F1forkPart";
import pulsePart from "@/pages/rep/graph/components/F1pulsePart";
import trafficPart from "@/pages/rep/graph/components/F0trafficPart";
import starPart from "@/pages/rep/graph/components/F1starPart";
import watchPart from "@/pages/rep/graph/components/F1watchPart";
import request from "@/utils/request";
import {BACKEND_URL} from "@/utils/config";
import jsCookie from "js-cookie";

export default {
  name: "index",
  components: {
    commitPart,
    contributorPart,
    forkPart,
    pulsePart,
    trafficPart,
    starPart,
    watchPart
  },
  data() {
    return {
      choose: this.$route.params.type || 'pulse',
      types: [
        {value: 'pulse', label: 'Pules',},
        // {value: 'contributors', label: 'Contributors'},
        // {value: 'traffic', label: 'Traffic'},
        {value: 'commit', label: 'Commits'},
        {value: 'watch', label: 'Watches'},
        {value: 'fork', label: 'Forks'},
        {value: 'star', label: 'Stars'},
      ],
    }
  },
  watch: {
    $route() {
      if (this.$route.name === 'insight') {
        this.choose = this.$route.params.type || 'pulse'
      }
    },
  },
  methods: {
    handleChange(val) {
      this.$router.push({name: 'insight', params: {type: val}})
    }
  },
  mounted() {
    request.get(BACKEND_URL + `/core/api/repo/verify_accessable/${this.$route.params.user}/${this.$route.params.rep}`)
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