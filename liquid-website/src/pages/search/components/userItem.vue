<template>
  <div class="container">
    <div style="display: flex; flex-direction: row">
      <user-avatar></user-avatar>
      <div class="user_info">
        <a-link>{{ username }}</a-link>
        <div>{{ user_descrip }}</div>
        <div style="font-size: 10px; color: var(--color-neutral-6)">
          {{ position }}
          <a-link style="font-size: 10px; color: var(--color-neutral-6)">{{ mail_addr }}</a-link>
        </div>
      </div>
    </div>
    <div>
      <a-button shape="round" type="outline"> follow</a-button>
    </div>
  </div>
</template>

<script>
import userLink from "@/components/user/F1userLink.vue";
import userAvatar from "@/components/user/F1userAvatar.vue";

export default {
  name: "userItem",
  components: {userAvatar, userLink},
  data() {
    return {
      username: 'huang',
      user_descrip: 'product manager, wide range of skills as developer and in business',
      position: 'GuangDong, China',
      mail_addr: 'hujiawei090807@gmail.com'
    }
  }
}
</script>

<style scoped>
.container {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
}
.user_info{
  margin-left: 10px;
}

</style>