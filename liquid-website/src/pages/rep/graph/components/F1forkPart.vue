<template>
  <div style="width: 950px; padding-top: 20px">
  <a-typography-title :heading="4">
    {{forks.length}} {{ forks.length > 1 ? 'forks' : 'fork' }} of {{$route.params.user}}/{{$route.params.rep}}
  </a-typography-title>
    <a-divider/>
    <a-row :gutter="[24, 24]">
      <a-col v-for="item in forks" :span="6">
        <user-avatar :size="25" :user="item.owner"/>
        <a-link style="margin-left: 3px" @click="this.$router.push({name: 'code', params: {user: item.owner, rep: item.name}})">{{`${item.owner}/${item.name}`}}</a-link>
      </a-col>
    </a-row>
  </div>

</template>

<script>
import userAvatar from "@/components/user/F1userAvatar.vue";
import {BACKEND_URL} from "@/utils/config";
import request from "@/utils/request";

export default {
  name: "forkPart",
  components:{
    userAvatar,
  },
  data(){
    return{
      forks: []
    }
  },
  mounted() {
    request.get(BACKEND_URL + `/core/api/repo/verify_accessable/${this.$route.params.user}/${this.$route.params.rep}`)
    request.get(BACKEND_URL+`/core/api/repo/list_forks/${this.$route.params.user}/${this.$route.params.rep}`)
        .then(resp => {
          this.forks = resp.data.data
        })
  }
}
</script>

<style scoped>

</style>