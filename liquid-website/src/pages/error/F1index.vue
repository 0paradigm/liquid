<template>
  <div>
    <a-result status="404" :subtitle="$t('errorMessage')">
      <template #extra>
        <a-space>
          <a-button type="primary" @click="$router.go(-2)">{{$t('Back')}}</a-button>
        </a-space>
      </template>
    </a-result>
  </div>
</template>

<script>
export default {
  name: "index",
  created() {
    document.title = 'Error - liquid'
  },
}
</script>

<style scoped>

</style>
