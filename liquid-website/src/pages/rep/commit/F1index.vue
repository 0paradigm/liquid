<template>
  <div style="display:flex; justify-content:center; padding-top: 20px">
    <a-space direction="vertical" size="large">
      <a-button style="border-radius: 10px; margin-left: -5px; margin-top: 10px">
        <icon-branch/>
        {{ (this.$route.params.branch || 'master' ).substring(0, 8)}}
      </a-button>
      <commit-list style="width: 900px" :commits="commits"/>
    </a-space>
  </div>
</template>

<script>
import commitList from "@/components/commit/F1commitList";
import {BACKEND_URL} from "@/utils/config";
import request from "@/utils/request";

export default {
  name: "index",
  components: {
    commitList
  },
  data() {
    return {
      commits: []
    }
  },
  mounted() {
    request.get(BACKEND_URL + `/core/api/repo/verify_accessable/${this.$route.params.user}/${this.$route.params.rep}`)
    request.get(BACKEND_URL + `/git/web/listcommits/${this.$route.params.user}/${this.$route.params.rep}/${this.$route.params.branch || 'master'}`)
        .then(resp => {
          this.commits = resp.data.data
        })
  }
}
</script>
