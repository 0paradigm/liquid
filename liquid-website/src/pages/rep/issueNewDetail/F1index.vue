<template>
  <div style="display: flex; justify-content: center">
    <div class="container">
      <div class="left-side">
        <a-typography-title :heading="3">
          Issue: {{ $route.query.title }}
        </a-typography-title>
        <a-divider/>
        <div style="display: flex; justify-content: right">
          <user-avatar :user="postUser"/>
          <bug-template/>
        </div>

      </div>
      <div class="right-side">
        <issue-side-bar :participants="[postUser]"/>
      </div>
    </div>
  </div>
</template>

<script>
import userAvatar from "@/components/user/F1userAvatar.vue";
import bugTemplate from "@/pages/rep/issueNewDetail/components/F1bugTemplate";
import issueSideBar from "@/components/issue/F1issueSideBar";
import request from "@/utils/request";
import {BACKEND_URL} from "@/utils/config";

export default {
  name: "index",
  components: {
    userAvatar,
    bugTemplate,
    issueSideBar
  },
  data() {
    return {
      postUser: localStorage.getItem('inUser')
    }
  },
  mounted() {
    request.get(BACKEND_URL + `/core/api/repo/verify_accessable/${this.$route.params.user}/${this.$route.params.rep}`)
  }
}
</script>

<style scoped>
.container {
  display: grid;
  grid-template-columns: 6fr 2fr;
  margin-top: -15px;
}

.left-side {
  grid-column: 1/ span 1;
  padding: 10px 40px;
}

.right-side {
  width: 296px;
  grid-column: 2/ span 1;
  padding-top: 10px;
  margin-top: 30px;
}
</style>