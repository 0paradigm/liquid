<template>
  <div style="display: flex; justify-content: center">
    <div style="width: 1100px; margin-top: 30px">
      <file-header></file-header>
      <file-list ></file-list>
    </div>
  </div>
</template>

<script>
import fileHeader from "@/components/file/F1fileHeader";
import fileList from "@/components/file/F1fileList";

export default {
  name: "index",
  components:{
    fileHeader,
    fileList,
  },
}
</script>

<style scoped>
.head-side{
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
}
</style>