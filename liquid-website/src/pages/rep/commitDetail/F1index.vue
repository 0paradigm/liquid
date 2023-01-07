<template>
  <div style="display: flex; justify-content: center">
    <a-space direction="vertical" style="margin-top: 20px">
      <a-card style="margin-top:10px;margin-bottom:10px; border-radius: 10px;"
              :header-style="{height:'58px', backgroundColor:'var(--color-neutral-1)', borderTopLeftRadius:'10px', borderTopRightRadius:'10px'}">
        <template #title>
          <a-typography-title style="margin-top:5px;margin-bottom: 3px; font-size: 16px" :heading="5">{{ title }}</a-typography-title>
        </template>
        <div style="display: flex; justify-content: space-between">
          <a-space>
            <user-avatar :size="23" :user="user"/>
            <user-link :user="user"/>
            committed at {{ dayjs.unix(timeStamp).format('YYYY-MM-DD HH:mm') }}
          </a-space>

          <a-space>
            <a-typography-text style="color: var(--color-neutral-6)">
              SHA: {{ id }}
            </a-typography-text>
          </a-space>

        </div>


      </a-card>
      <file-change-item :old-value="oldValue" :new-value="newValue" :default-choose="defaultChoose"
                        :origin-tree-data="originTreeData" :changes="changes" style="width: 1000px"/>
    </a-space>
  </div>

</template>

<script>
import fileChangeItem from "@/components/file/F1fileChangeItem";
import userAvatar from "@/components/user/F1userAvatar.vue";
import userLink from "@/components/user/F1userLink.vue";
import {BACKEND_URL} from "@/utils/config";
import request from "@/utils/request";
import dayjs from "dayjs";

export default {
  name: "index",
  components: {
    fileChangeItem,
    userAvatar,
    userLink
  },
  mounted() {
    request.get(BACKEND_URL + `/core/api/repo/verify_accessable/${this.$route.params.user}/${this.$route.params.rep}`)
    request.get(BACKEND_URL + `/git/web/diff/${this.$route.params.user}/${this.$route.params.rep}?branch=${this.branch}&sha=${this.$route.params.SHA}`)
        .then(resp => {
          this.originTreeData = resp.data.data;
        })
    request.get(BACKEND_URL + `/git/web/diffV2/${this.$route.params.user}/${this.$route.params.rep}?branch=${this.branch}&sha=${this.$route.params.SHA}`)
        .then(resp => {
          this.changes = resp.data.data;
          this.defaultChoose = this.changes[0].file;
          this.oldValue = this.changes[0].old;
          this.newValue = this.changes[0].new;
        })
    let reqUrl = BACKEND_URL + `/git/web/latest/${this.$route.params.user}/${this.$route.params.rep}/${this.$route.params.SHA}`;
    request.get(reqUrl)
        .then(resp => {
          this.user = resp.data.latest.user ?? ''
          this.timeStamp = resp.data.latest.timestamp ?? 1672531200
          this.title = resp.data.latest.message ?? ''
        })
  },
  data() {
    return {
      dayjs,
      id: this.$route.params.SHA,
      originTreeData: [],
      changes: [],
      defaultChoose: '',
      oldValue: '',
      newValue: '',

      branch: this.$route.params.branch || 'master',
      title: '',
      user: '',
      timeStamp: 25532442323,
    }

  }
}
</script>

<style scoped>

</style>