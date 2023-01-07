<template>
  <div style="display: flex; justify-content: center; margin-top: 30px">
    <user-avatar :translate="[100, -190]" :user="postUser"/>
    <a-list style="margin-left: 20px">
      <a-list-item  v-for="item in issueType">
        <div style="display: flex; justify-content: space-between; width: 800px">
          <div>
            <a-typography-title style="margin-top: 0px; margin-bottom: 0px" :heading="6">{{item.title}}</a-typography-title>
            <div style="color: var(--color-neutral-6)">{{item.descrip}}</div>

          </div>
          <a-button @click="this.$router.push({name:'issueNewDetail', query:{type:item.type, title:item.title}})" style="margin-top: 7px" shape="round" type="primary">Get started</a-button>
        </div>
      </a-list-item>
    </a-list>
  </div>
</template>

<script>
import userAvatar from "@/components/user/F1userAvatar.vue";
import request from "@/utils/request";
import {BACKEND_URL} from "@/utils/config";

export default {
  name: "index",
  components: {
    userAvatar
  },
  data() {
    return{
      postUser: localStorage.getItem('inUser'),
      issueType: [
        {
          title: 'Bug report',
          descrip: 'Create a report to help us improve',
          type: 'BUG',
        },
        {
          title: 'Feature Request',
          descrip: 'Suggest an idea for this project',
          type: 'REQUEST',
        },
        {
          title: 'Other',
          descrip: 'Use this for any other issues. Do NOT create blank issues',
          type: 'OTHER',
        },
      ]
    }
  },
  mounted() {
    request.get(BACKEND_URL + `/core/api/repo/verify_accessable/${this.$route.params.user}/${this.$route.params.rep}`)
  }
}
</script>

<style scoped>

</style>