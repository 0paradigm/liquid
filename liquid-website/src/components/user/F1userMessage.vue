<template>
  <div class="demo-basic">
    <div style="display: flex; justify-content: space-between">
      <a-avatar>
        <img
            :alt="this.user"
            :src="BACKEND_URL() + '/media/get?uid=' + this.user"
        />
      </a-avatar>
      <a-button shape="round">Follow</a-button>
    </div>
    <a-space direction="vertical">
      <t-link hover="color" style="margin-top: 5px">{{ user }}</t-link>
      <div style="color: gray">
        <icon-location/>
        {{ location }}
      </div>
      <a-typography-paragraph
          :ellipsis="{
        rows: 2,
        showTooltip: true,
      }"
          type="secondary"
          :style="{marginBottom:'0px',color:'gray'}">
        {{ descript }}
      </a-typography-paragraph>
    </a-space>

  </div>
</template>

<script>
import {BACKEND_URL} from "@/utils/config";
import request from "@/utils/request";

export default {
  name: "userLink",
  props: {
    user: '',
  },
  data() {
    return {
      descript: '',
      location: ''
    }
  },
  mounted() {
    request.get(BACKEND_URL + '/core/api/user/info/' + this.user)
        .then(resp => {
          this.descript = resp.data.data.bio
          this.location = resp.data.data.location
        })
  },
  methods: {
    BACKEND_URL() {
      return BACKEND_URL
    },
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