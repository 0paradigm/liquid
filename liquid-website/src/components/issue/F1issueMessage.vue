<template>
  <div class="demo-basic">
    {{ title }}
  </div>
</template>

<script>
export default {
  name: "issueMessage",
  props: {
    title: '',
  }
}
</script>

<style scoped>
.demo-basic {
  padding: 10px;
  width: 200px;
  background-color: var(--color-bg-popup);
  border-radius: 4px;
  box-shadow: 0 2px 8px 0 rgba(0, 0, 0, 0.15);
}
</style>