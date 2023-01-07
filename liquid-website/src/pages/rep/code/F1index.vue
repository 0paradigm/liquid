<template>
  <div style="display: flex; justify-content: center">
    <div class="container">
      <div class="left-side">
        <file-header/>
        <file-list/>
      </div>
      <div class="right-side">
        <Right_InfoPage/>
      </div>
    </div>
  </div>
</template>

<script>
import Right_InfoPage from "@/pages/rep/code/components/F1Right_InfoPage"
import fileList from "@/components/file/F1fileList";
import fileHeader from "@/components/file/F1fileHeader";
import {mapActions, mapGetters} from "vuex";

export default {
  components: {
    Right_InfoPage,
    fileHeader,
    fileList
  },
  name: "index",
  methods: {
    ...mapActions('rep', ['getFile']),

    jumpToUpdate() {
      this.$router.push({
        name: 'upload',
        params: {
          branch: this.$route.params.branch || 'master',
          dir: this.$route.params.dir
        }
      })
    },
  }
}
</script>

<style scoped>

.container {
  display: grid;
  grid-template-columns: 6fr 2fr;
  padding-top: 20px;
}

.left-side {
  grid-column: 1/ span 1;
  padding: 10px 40px;
}

.right-side {
  width: 296px;
  grid-column: 2/ span 1;
  padding-top: 10px;
}

.head-side {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
}

</style>