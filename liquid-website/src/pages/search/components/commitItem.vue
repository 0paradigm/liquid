<template>
  <div style="margin-bottom:-10px;display:flex; justify-content: space-between;">
    <div>
      <a-space direction="vertical" size="small">
        <t-link hover="color">
          {{rep}}
        </t-link>
        <a-link @click="jumpToCommitDetail()" hover="color" style="margin-left:-4px;margin-top:-10px; margin-bottom:-30px; font-size:16px">
          Up date issueSideBar
        </a-link>
        <a-space>

          <div style="margin-top: -15px"><user-link size="13px"></user-link></div>
          <a-typography-paragraph style="color: var(--color-neutral-6)">
            committed {{timeStamp}}
          </a-typography-paragraph>
        </a-space>
      </a-space>
    </div>

    <div>
      <a-space size="large" style="margin-top: 5px">
        <a-button-group shape="round">
          <a-tooltip content="Copy the full SHA">
            <a-button type="secondary">
              <icon-copy />
            </a-button>
          </a-tooltip>

          <a-tooltip content="View commit details">
            <a-button>
              {{id}}
            </a-button>
          </a-tooltip>
        </a-button-group>

        <a-tooltip content="Browse the repository at this point in history">
          <a-button shape="round">
            <icon-code />
          </a-button>
        </a-tooltip>
      </a-space>
    </div>
  </div>
</template>

<script>
import userLink from "@/components/user/F1userLink.vue";

export default {
  name: "commitItem",
  components:{
    userLink
  },
  data() {
    return{
      id: 'fsafas3255',
      rep: 'TheAlgorithms/Java',
      timeStamp: '15 hours ago'
    }
  },
  methods: {
    jumpToCommitDetail(){
      // this.$router.push({name:'commitDetail', params:{branch:'main', SHA:this.id}})
    }
  }
}
</script>

<style scoped>

</style>