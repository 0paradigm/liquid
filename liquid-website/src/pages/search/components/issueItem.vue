<template>
  <a-space align="start" style="margin-top: 10px; ">
    <icon-minus-circle style="color:rgb(var(--green-7))" size="20"/>
    <a-space direction="vertical" size="small">
      <t-link hover="color">
        {{rep}}
      </t-link>
      <a-link @click="jumpToCommitDetail()" hover="color" style="margin-left:-4px;margin-top:-5px; margin-bottom:-5px; font-size:16px">
        Up date issueSideBar
      </a-link>
      <a-space>
        <div style="margin-top: -15px"><user-link size="13px"></user-link></div>
        <a-typography-paragraph style="color: var(--color-neutral-6)">
          opened {{timeStamp}}
        </a-typography-paragraph>
      </a-space>
    </a-space>
  </a-space>
</template>

<script>
import userLink from "@/components/user/F1userLink.vue";

export default {
  name: "issueItem",
  components:{
    userLink,
  },
  data(){
    return{
      id: 'fsafas3255',
      rep: 'TheAlgorithms/Java #3',
      timeStamp: '15 hours ago'
    }
  }
}
</script>

<style scoped>

</style>