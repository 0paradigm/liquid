<template>
  <a-trigger :popup-translate="translate">
    <t-link @click="jump" hover="color" :style="{fontSize:size}">{{ user }}</t-link>
    <template #content>
      <user-message :user="user"></user-message>
    </template>
  </a-trigger>
</template>

<script>
import userMessage from "@/components/user/F1userMessage";

export default {
  name: "userLink",
  components: {
    userMessage
  },
  props: {
    user: {type: String,},
    translate: {default: [90, 10]},
    size: {default: '15px'},
  },
  methods: {
    jump() {
      this.$router.push({name: 'user', params: {user: this.user}})
    }
  }
}
</script>
