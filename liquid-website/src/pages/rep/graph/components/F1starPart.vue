<template>
  <div style="width: 950px; padding-top: 20px">
    <a-typography-title :heading="4">
      {{stars.length}} {{stars.length > 1?'peoples' : 'people'}} starred {{$route.params.user}}/{{$route.params.rep}}
    </a-typography-title>
    <a-divider/>
    <a-row :gutter="[24, 24]">
      <a-col v-for="i in stars" :span="6">
        <user-avatar :size="25" :user="i"/>
        <a-link style="margin-left: 3px" @click="this.$router.push({name: 'user', params: {user: i}})">{{i}}</a-link>
      </a-col>
    </a-row>
  </div>

</template>

<script>
import userAvatar from "@/components/user/F1userAvatar.vue";
import request from "@/utils/request";
import {BACKEND_URL} from "@/utils/config";

export default {
  name: "starPart",
  components:{
    userAvatar,
  },
  data(){
    return{
      stars: []
    }
  },
  mounted() {
    request.get(BACKEND_URL + `/core/api/repo/verify_accessable/${this.$route.params.user}/${this.$route.params.rep}`)
    request.get(BACKEND_URL+`/core/api/repo/list_starers/${this.$route.params.user}/${this.$route.params.rep}`)
        .then(resp => {
          this.stars = resp.data.data
        })
  }
}
</script>
