<template>
  <div class="contributor">
    {{ $t('Contributors') }}
    <a-badge
        :count="contributors.length"
        :dotStyle="{ background: '#E5E6EB', color: '#86909C' }"
    />
  </div>
  <a-space size="large" class="avatar">
    <div v-for="item in contributors">
      <div style="margin-right: -12px">
        <user-avatar :user="item" :size="35"/>
      </div>
    </div>
  </a-space>
</template>

<script>
import userAvatar from "@/components/user/F1userAvatar.vue";

export default {
  name: "contributors",
  components: {
    userAvatar
  },
  props: {
    contributors: {default: []}
  }
}
</script>

<style scoped>
.avatar {
  margin-top: 5px;
}

.contributor {
  font-size: 18px;
  font-family: "Arial Rounded MT Bold";
  padding-bottom: 10px;
}
</style>
