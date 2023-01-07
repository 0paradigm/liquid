<template>
  <a-trigger :popup-translate="translate">
    <a-avatar @click="jump" class="avatar" :size="size">
      <img
          :alt="user"
          :src="BACKEND_URL()+'/media/get?uid=' + user"
      />
    </a-avatar>
    <template #content>
      <user-message :user="user"></user-message>
    </template>
  </a-trigger>
</template>

<script>
import userMessage from "@/components/user/F1userMessage";
import {BACKEND_URL} from "@/utils/config";

export default {
  name: "userAvatar",
  components: {
    userMessage
  },
  props: {
    user: {
      type: String,
    },
    translate: {default: [100, -180]},
    size: {default: 40},
  },
  methods: {
    BACKEND_URL() {
      return BACKEND_URL
    },
    jump() {
      this.$router.push({name: 'user', params: {user: this.user}})
    }
  }
}
</script>

<style scoped>
.avatar:hover {
  cursor: pointer;
}
</style>