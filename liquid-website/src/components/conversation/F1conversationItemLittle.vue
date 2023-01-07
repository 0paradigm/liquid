<template>
  <a-timeline-item>
    <template #dot>
      <icon-subscribed
          :style="{
              fontSize: '20px',
              padding: '2px',
              boxSizing: 'border-box',
              borderRadius: '50%',
              backgroundColor: 'var(--color-primary-light-1)',
            }"
          v-if="isMerge"
      />
      <IconCheck
          :style="{
              fontSize: '20px',
              padding: '2px',
              boxSizing: 'border-box',
              borderRadius: '50%',
              backgroundColor: 'var(--color-primary-light-1)',
            }"
          v-else-if="isClose"
      />
      <icon-minus-circle
          v-else
          :style="{
              fontSize: '20px',
              padding: '2px',
              boxSizing: 'border-box',
              borderRadius: '50%',
              color: 'rgb(var(--green-4))',
              backgroundColor: 'rgb(var(--green-2))',
            }"
      />
    </template>
    <a-space align="start">
      <user-avatar :size="25" :user="uname"/>
      <user-link :user="uname"/>
      <a-typography-paragraph style="color: var(--color-neutral-6)">
        {{ isMerge ? 'merged this pull request' : isClose ? 'closed this as completed' : 'reopened this issue' }} at
        {{ dayjs.unix(ts / 1000).format('YYYY-MM-DD HH:mm') }}
      </a-typography-paragraph>
    </a-space>
  </a-timeline-item>
</template>

<script>
import userAvatar from "@/components/user/F1userAvatar.vue";
import userLink from "@/components/user/F1userLink.vue";
import dayjs from 'dayjs'

export default {
  name: "conversationItemLittle",
  components: {
    userAvatar,
    userLink
  },
  props: {
    uname: {default: ''},
    ts: {default: 0},
    isClose: {default: true},
    isMerge: {default: false},
  },
  data() {
    return {
      dayjs,
    }
  }
}
</script>
