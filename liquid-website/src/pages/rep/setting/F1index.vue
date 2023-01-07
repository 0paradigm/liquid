<template>
  <div class="page">
    <div style="display: flex; justify-content: center; width: 1200px;">
      <a-menu
          v-model="value"
          :style="{ paddingTop:'10px', width: '230px', height: '500px',boxShadow: '0 0 1px rgba(0, 0, 0, 0.3)', borderRadius:'10px'}"
          :selected-keys="value"
          breakpoint="xl"
          auto-open
          @menu-item-click="onChange"
          @collapse="onCollapse"
      >
        <a-menu-item key="0">
          <template #icon>
            <icon-settings/>
          </template>
          General
        </a-menu-item>
        <a-sub-menu key="1">
          <template #icon>
            <icon-send/>
          </template>
          <template #title>Access</template>
          <a-menu-item key="1_0">
            <template #icon>
              <icon-user-group/>
            </template>
            Collaborators
          </a-menu-item>
        </a-sub-menu>
        <a-sub-menu key="2">
          <template #icon>
            <icon-code/>
          </template>
          <template #title>Code</template>
          <a-menu-item key="2_0">
            <template #icon>
              <icon-branch/>
            </template>
            Branches
          </a-menu-item>
          <a-menu-item key="2_1">
            <template #icon>
              <icon-tag/>
            </template>
            Tags
          </a-menu-item>
        </a-sub-menu>

      </a-menu>
      <general-part style="width: 100%;" v-if="this.value[0]==='0'" :repo="this.$route.params.rep"
                    :owner="this.$route.params.user"/>
      <collaborator-part style="width: 100%" v-if="this.value[0]==='1_0'" :repo="this.$route.params.rep"
                         :owner="this.$route.params.user"/>
    </div>
  </div>
</template>

<script>
import generalPart from "@/pages/rep/setting/components/F1generalPart";
import collaboratorPart from "@/pages/rep/setting/components/F1collaboratorPart";

export default {
  name: "index",
  components: {
    generalPart,
    collaboratorPart
  },
  mounted() {
    const isMine = localStorage.getItem('inUser') == this.$route.params.user
    if (!isMine) {
      this.$router.push({name: 'code', params: {user: this.$route.params.user, rep: this.$route.params.rep}})
    }
  },
  data() {
    return {
      value: ['0'],
    }
  },
  methods: {
    onEnter() {
      console.log('trigger enter');
    },
    onChange(val) {
      this.value = [val]
      console.log(val);
    },
  },
}
</script>


<style scoped>

.page {
  display: flex;
  height: 100%;
  justify-content: center;
  margin-left: 20px;
  margin-right: 20px;
  margin-top: 30px;

}

.leftPanel {
  width: 23%;
  height: 100%;
  margin-top: 10px;
  margin-right: 5px;
  margin-left: 10px;
}

.rightPanel {
  display: flex;
  justify-content: center;
  flex-direction: column;
  width: 60%;
  height: 100%;
  /*background-color: #f0f2f5;*/
  margin-top: 10px;
  margin-right: 5px;
  margin-left: 20px;
}

.general {
  margin-top: 9px;
  margin-left: 10px;
}

.access_header {
  margin-left: 10px;
  margin-top: 20px;
  font-size: 9px;
  font-family: "Arial Rounded MT Bold";
  color: var(--color-text-2);
}

.collaborators {
  margin-top: 12px;
  margin-left: 18px;
}

.divider {
  margin: 8px 0;
}

.general_header {
  margin-top: 10px;
  font-size: 23px;
  font-family: "Arial Unicode MS";
}

.feature_header {
  margin-top: 30px;
  margin-left: 2px;
  font-size: 23px;
  font-family: "Arial Unicode MS";
}

.repo_name_header {
  margin-top: 15px;
  font-size: 14px;
  font-family: "Arial Rounded MT Bold";
}

.input {
  margin-top: 10px;
}

.rename_button {
  margin-top: 10px;
  margin-left: 9px;
}

.radio {
  font-size: 14px;
  font-family: "Arial Rounded MT Bold";
}

.list_item {
  display: flex;
  flex-direction: column;
}

.repo_name_box {
  display: flex;
  flex-direction: row;
}

.description {
  font-size: 2px;
  margin-left: 27px;
}

.card {
  margin-top: 10px;
  margin-left: 30px;
}

.instr1 {
  font-size: 14px;
  font-family: "Arial Rounded MT Bold";
  margin-bottom: 5px;
}
</style>