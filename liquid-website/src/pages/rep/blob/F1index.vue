<template>
  <div style="display: flex; justify-content: center">
    <div style="width: 1100px; margin-top: 30px">
      <file-header/>
      <file-container/>
    </div>
  </div>
</template>

<script>
import FileContainer from "@/pages/rep/blob/components/F1FileContainer";
import fileHeader from "@/components/file/F1fileHeader";

export default {
  name: "index",
  components:{
    fileHeader,
    FileContainer
  }
}
</script>


