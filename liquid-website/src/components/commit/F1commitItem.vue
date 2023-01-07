<template>
  <div style="display:flex; justify-content: space-between; height: 50px">
    <div>
      <a-space direction="vertical" size="small">
        <t-link @click="jumpToCommitDetail()" hover="color" style="font-size:16px">
          {{ label }}
        </t-link>
        <a-space align="start" style="margin-top: -10px">
          <user-avatar :size="15" :user="user"/>
          <user-link size="13px" :user="user"/>
          <a-typography-paragraph style="color: var(--color-neutral-6)">
            committed at {{ datetime() }}
          </a-typography-paragraph>
        </a-space>
      </a-space>
    </div>

    <div>
      <a-space size="small" style="margin-top: 5px">
        <a-button-group shape="round">
          <a-tooltip content="View commit details">
            <a-button @click="jumpToCommitDetail()">
              {{ id.substring(0, 8) }}
            </a-button>
          </a-tooltip>

          <a-tooltip content="Copy the full SHA">
            <a-button type="secondary">
              <icon-copy/>
            </a-button>
          </a-tooltip>

          <a-popconfirm :content="`Sure to rollback to ${id.substring(0,8)}?`" ok-text="sure" cancel-text="cancel"
                        v-on:ok="rollback" v-if="!disableRollbackAndView">
            <a-tooltip content="Rollback">
              <a-button type="secondary" status="danger">
                <icon-undo/>
              </a-button>
            </a-tooltip>
          </a-popconfirm>
        </a-button-group>

        <a-tooltip content="Browse the repository at this point in history" v-if="!disableRollbackAndView">
          <a-button @click="$router.push('/'+this.$route.params.user+'/'+this.$route.params.rep+'/'+this.id)"
                    shape="round">
            <icon-code/>
          </a-button>
        </a-tooltip>
      </a-space>
    </div>
  </div>
</template>

<script>
import userLink from "@/components/user/F1userLink.vue";
import userAvatar from "@/components/user/F1userAvatar.vue";
import dayjs from "dayjs";
import request from "@/utils/request";
import {BACKEND_URL} from "@/utils/config";

export default {
  name: "commitItem",
  components: {
    userLink,
    userAvatar
  },
  props: {
    id: {type: String, default: ''},
    label: {type: String},
    user: {type: String},
    time: {default: 0},
    disableRollbackAndView: {default: false}
  },
  methods: {
    jumpToCommitDetail() {
      this.$router.push({name: 'commitDetail', params: {branch: this.$route.params.branch, SHA: this.id}})
    },
    datetime() {
      return dayjs.unix(this.time).format('HH:mm')
    },
    rollback() {
      request.get(BACKEND_URL + `/git/web/revert/${this.$route.params.user}/${this.$route.params.rep}?branch=${this.$route.params.branch}&sha=${this.id}`)
          .then(resp => {
            console.log(resp.data)
            this.sleep(500).then(() => {
              this.$router.push({name: 'code'})
            })
          })
    },
    sleep(time) {
      return new Promise((resolve) => setTimeout(resolve, time));
    }
  }
}
</script>

<style scoped>

</style>