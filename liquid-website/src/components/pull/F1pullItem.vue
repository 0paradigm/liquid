<template>
  <div style="display: flex; justify-content: space-between">
    <div>
      <icon-share-alt v-if=!isClosed size="17" style="color:rgb(var(--green-7)) "/>
      <icon-check-circle v-if="isClosed" size="17" style="color:rgb(var(--arcoblue-4))"/>
      <a-trigger :popup-translate="[0, -60]">
        <t-link @click="jumpToPull" style="font-size:17px;margin-left: 5px" hover="color">{{ title }}</t-link>
        <template #content>
          <issue-message :title="title"/>
        </template>
      </a-trigger>
    </div>
    <div>
      <a-button style="background-color: transparent">
        <icon-message size="17"/>
        {{cmtCnt}}
      </a-button>
    </div>
  </div>
  <div style="margin-left: 22px; color: gray; font-size: 11px">
    #{{ id }} opened on {{dayjs.unix(openAt/1000).format('YYYY-MM-DD')}}
    by <user-link :user="openBy" size="11px"/>
  </div>
</template>

<script>
import userLink from "@/components/user/F1userLink.vue";
import issueMessage from "@/components/issue/F1issueMessage";
import dayjs from 'dayjs'

export default {
  name: "pullItem",
  components: {
    userLink,
    issueMessage,
  },
  props: {
    id: {default: 0},
    isClosed: {default: false},
    title: {default: 'PR'},
    openBy: {default: 'liquid-official'},
    openAt: {default: 0},
    cmtCnt: {default: 0},
  },
  data() {
    return {
      dayjs
    }
  },
  methods: {
    jumpToPull() {
      this.$router.push({name: 'pullDetail', params: {index: this.id}})
    }
  }
}
</script>
