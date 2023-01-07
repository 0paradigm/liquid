<template>
  <a-timeline >
    <div v-for="item in conversations">
      <conversation-item-little style="margin-bottom: -20px" :is-close="true" :uname="item.author" :ts="item.time"
                                v-if="item.ctx === '[[[close]]]'"/>
      <conversation-item-little style="margin-bottom: -20px" :is-close="false" :uname="item.author" :ts="item.time"
                                v-else-if="item.ctx === '[[[reopen]]]'"/>
      <conversation-item-little style="margin-bottom: -20px" :is-close="false" :is-merge="true" :uname="item.author" :ts="item.time"
                                v-else-if="item.ctx === '[[[merge]]]'"/>
      <conversation-item class="timeline-item" :ctx="item.ctx" :user="item.author" :ts="item.time"
                         :character="item.cred" v-else/>
    </div>

  </a-timeline>
</template>

<script>
import conversationItem from "@/components/conversation/F1conversationItem";
import conversationItemLittle from "@/components/conversation/F1conversationItemLittle";
import UserAvatar from "@/components/user/F1userAvatar.vue";

export default {
  name: "conversationList",
  props: {
    conversations: {default: []},
  },
  components: {
    UserAvatar,
    conversationItem,
    conversationItemLittle
  },
}
</script>

<style scoped>
.timeline-item {
  margin-top: 10px;
  margin-bottom: 20px
}

</style>