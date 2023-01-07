<template>
  <div style="display: flex; justify-content: space-between">
    <div>
      <icon-minus-circle v-if=!isClosed size="17" style="color:rgb(var(--green-7)) "/>
      <icon-check-circle v-if="isClosed" size="17" style="color:rgb(var(--arcoblue-4))"/>
      <a-trigger :popup-translate="[0, -60]">
        <t-link @click="jumpToIssue" style="font-size:17px;margin-left: 5px" hover="color">{{ title }}</t-link>
        <template #content>
          <issue-message :title="title"></issue-message>
        </template>
      </a-trigger>

      <a-space style="margin-top:5px; margin-left: 10px">
        <a-tag v-for="tag in tags" :color="tag.color" style="border-radius: 30px">
          {{ tag.content }}
        </a-tag>

      </a-space>
    </div>
    <div>
      <a-button style="background-color: transparent">
        <icon-message size="17"/>
        {{ chatCnt }}
      </a-button>
    </div>
  </div>
  <div style="margin-left: 22px;margin-top: -1px; color: gray; font-size: 11px">
    #{{ this.id }} opened on {{dayjs.unix(this.openAt/1000).format('YYYY-MM-DD') }} by
    <user-link :user="user" size="11px"/>
  </div>
</template>

<script>
import userLink from "@/components/user/F1userLink.vue";
import issueMessage from "@/components/issue/F1issueMessage";
import dayjs from "dayjs";

export default {
  name: "issueItem",
  components: {
    issueMessage,
    userLink,
    dayjs
  },
  data() {
    return {
      dayjs
    }
  },
  props: {
    isClosed: {type: Boolean, default: false},
    id: {type: Number, default: -1},
    tags: {default: []},
    user: {type: String, default: ''},
    title: {type: String, default: 'Issue'},
    chatCnt: {type: Number, default: 0},
    openAt: {type: Number, default: 0},
  },
  methods: {
    jumpToIssue() {
      this.$router.push({name: 'issueDetail', params: {index: this.id}})
    }
  }
}
</script>
