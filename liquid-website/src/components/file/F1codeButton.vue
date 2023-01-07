<template>
  <a-trigger trigger="click" :unmount-on-close="false" :popup-translate="[-110, 10]">
    <a-button :type=type style="border-radius: 10px">
      <template #icon>
        <icon-code/>
      </template>
      {{$t('Code')}}
      <icon-down/>
    </a-button>

    <template #content>
      <div class="demo-basic">
        <a-space direction="vertical" style="width: 100%">
          <a-tabs default-active-key="1">
            <a-tab-pane key="1" title="Https">
              <a-typography-paragraph :ellipsis="{
                rows: 1,
                showTooltip: true,
              }" copyable style="margin-bottom: 0px; margin-top: 0px; margin-left: 10px;">
                {{ GITD_URL() + `/${$route.params.user}/${$route.params.rep}` }}
              </a-typography-paragraph>
            </a-tab-pane>

            <a-tab-pane key="2" title="SSH">
              <a-typography-paragraph :ellipsis="{
        rows: 1,
        showTooltip: true,
      }" copyable style="margin-bottom: 0px; margin-top: 0px; margin-left: 10px;">
                {{ GITD_URL2() + `:${$route.params.user}/${$route.params.rep}.git` }}
              </a-typography-paragraph>
            </a-tab-pane>
          </a-tabs>
          <a-divider style="margin: 0px"></a-divider>
          <a-button type="text" long @click="download">
            <template #icon>
              <icon-download/>
            </template>
            {{$t('Download')}} ZIP
          </a-button>
        </a-space>
      </div>
    </template>
  </a-trigger>
</template>

<script>
import {GITD_URL, GITD_URL2, BACKEND_URL} from "@/utils/config";
import axios from "axios";

export default {
  name: "codeButton",
  methods: {
    GITD_URL2() {
      return GITD_URL2
    },
    GITD_URL() {
      return GITD_URL
    },
    download(){
      axios({
        method: 'get',
        url: `/gitd/zip/${this.$route.params.user}/${this.$route.params.rep}`,
        data: {},
        responseType: 'blob'
      }).then(res => {
        let data = res.data // 这里后端对文件流做了一层封装，将data指向res.data即可
        if (!data) {
          return
        }
        let url = window.URL.createObjectURL(new Blob([data]))
        let a = document.createElement('a')
        a.style.display = 'none'
        a.href = url
        a.setAttribute('download',this.$route.params.rep+'.zip')
        document.body.appendChild(a)
        a.click() //执行下载
        window.URL.revokeObjectURL(a.href) //释放url
        document.body.removeChild(a) //释放标签
      }).catch((error) => {
        console.log(error)
      })
    }
  },
  props: {
    type: {
      default: 'primary'
    }
  }
}
</script>

<style scoped>
.demo-basic {
  padding: 10px;
  width: 300px;
  background-color: var(--color-bg-popup);
  border-radius: 10px;
  box-shadow: 0 2px 8px 0 rgba(0, 0, 0, 0.15);
}
</style>