<template>
  <div style="display: flex; justify-content: center">
    <a-space direction="vertical">
      <a-space class="header-container" size="large" style="margin-top: 20px">
        <a-space :size=0>
          <a-trigger trigger="click" :unmount-on-close="false" :popup-translate="[75, 7]">
            <a-button
                style="border-bottom-right-radius:0px; border-top-right-radius:0px;border-right-color: var(--color-neutral-3); border-bottom-left-radius: 10px; border-top-left-radius: 10px">
              Filter
              <icon-caret-down/>
            </a-button>
            <template #content>
              <a-list
                  style="width: 250px;background-color: white;border-radius: 4px; box-shadow: 0 2px 8px 0 rgba(0, 0, 0, 0.15);">
                <a-list-item style="font-size: 15px; font-weight: 530">Filter issues</a-list-item>
                <a-list-item class="filter-issues-item" v-for="item in filterIssues">{{ item.content }}</a-list-item>
              </a-list>

            </template>
          </a-trigger>
          <a-input style="width: 500px; border-bottom-right-radius: 10px; border-top-right-radius: 10px" placeholder=""
                   allow-clear>
            <template #prefix>
              <icon-search/>
            </template>

          </a-input>
        </a-space>
        <DiscountIcon/>
        <a-button-group shape="round">
          <a-button>
            <icon-tags/>
            Labels
          </a-button>
          <a-button>Milestones</a-button>
        </a-button-group>
        <a-button @click="this.$router.push({name:'pullNew'})" type="primary" style="border-radius: 10px">New pull
          request
        </a-button>
      </a-space>

      <a-list class="content-container" style="margin-top: 10px">

        <a-list-item style="background-color: var(--color-neutral-1)">
          <div style="display: flex; justify-content: space-between">
            <a-radio-group v-model="disp" type="button" style="background-color: transparent;margin-left: -20px">
              <a-radio value="Open" style="background-color: transparent; font-size: 15px;">
                <a-space :size=0>
                  <icon-share-alt size="17" style="margin-right: 10px"/>
                  open
                </a-space>
              </a-radio>
              <a-radio value="Close" style="background-color: transparent; font-size: 15px">
                <a-space :size=0>
                  <icon-check-circle size='17' style="margin-right: 10px;"/>
                  close
                </a-space>
              </a-radio>
            </a-radio-group>
            <a-space size="mini" style="margin-right: -20px">
              <a-trigger trigger="click" :unmount-on-close="false" :popup-translate="[-80, 5]">
                <a-button size="small" style="background-color: transparent">
                  Label
                  <icon-down/>
                </a-button>
                <template #content>
                  <div class="demo-basic">
                    <div
                        style=" font-size: 14px; font-weight:530; margin-top: 5px; margin-bottom: -10px; margin-left: 5px">
                      Filter by label
                    </div>
                    <a-divider></a-divider>
                    <a-space :size=8 wrap style="margin-top: -5px">
                      <a-tooltip v-for="tag in tags" :content="tag.content">
                        <a-tag :color="tag.color" style="border-radius: 30px; cursor: pointer">
                          {{ tag.content }}
                        </a-tag>
                      </a-tooltip>
                    </a-space>
                  </div>
                </template>
              </a-trigger>

              <a-trigger trigger="click" :unmount-on-close="false" :popup-translate="[-80, 5]">
                <a-button size="small" style="background-color: transparent">
                  Sort
                  <icon-down/>
                </a-button>

                <template #content>
                  <div class="demo-basic">
                    <div
                        style=" font-size: 14px; font-weight:530; margin-top: 5px; margin-bottom: -10px; margin-left: 5px">
                      Sort by
                    </div>
                    <a-divider></a-divider>
                    <a-radio-group default-value="A" style="margin-left: 20px; margin-top: -10px" direction="vertical">
                      <a-radio v-for="s in sort" :value="s.value">{{ s.content }}</a-radio>
                    </a-radio-group>
                  </div>
                </template>
              </a-trigger>
            </a-space>
          </div>
        </a-list-item>
        <a-list-item class="pull-item-container" v-for="item in (disp==='Open'?opens:closes)" style="background-color: white">
          <pull-item :id="item.id" :is-closed="item.isClosed" :title="item.title"
                     :open-by="item.openBy" :open-at="item.openAt" :cmt-cnt="item.cmtCnt"/>
        </a-list-item>
      </a-list>

      <div class="page" style="display: flex; justify-content: center; margin-top: 10px">
        <a-pagination :total="(disp==='Open'?opens:closes).length" show-total show-jumper/>
      </div>
    </a-space>
  </div>
</template>

<script>
import pullItem from "@/components/pull/F1pullItem";
import request from "@/utils/request";
import {BACKEND_URL} from "@/utils/config";

export default {
  name: "index",
  components: {
    pullItem
  },
  data() {
    return {
      tags: [
        {content: 'SQL', color: 'blue'},
        {content: 'New feature', color: 'blue'},
        {content: 'Java', color: 'red'},
        {content: 'Java script', color: 'gold'},
        {content: 'ooad', color: 'orange'},
      ],
      sort: [
        {value: 'A', content: 'Newest'},
        {value: 'B', content: 'Oldest'},
        {value: 'C', content: 'Most commented'},
        {value: 'D', content: 'Least commented'},
        {value: 'E', content: 'Recently commented'},
        {value: 'F', content: 'Best match'},
      ],
      filterIssues: [
        {content: 'Open issues and pull request'},
        {content: 'Your issues'},
        {content: 'Your pull requests'},
        {content: 'Everything assigned to you'},
        {content: 'Everything mentioned to you'}
      ],
      opens: [],
      closes: [],
      disp: 'Open'  // 'Close
    }
  },
  watch: {
    $route() {
      if (this.$route.name !== 'notFound') {
        this.init()
      }
    },
  },
  mounted() {
    this.init();
  },
  methods:{
    init(){
      request.get(BACKEND_URL + `/core/api/pr/list/${this.$route.params.user}/${this.$route.params.rep}`)
          .then(resp => {
            console.log(resp.data.data)
            this.opens = resp.data.data.opens
            this.closes = resp.data.data.closes
          })
    }
  }
}
</script>

<style scoped>
.pull-item-container:hover {
  background-color: var(--color-neutral-1);
}

.demo-basic {
  padding: 10px;
  width: 200px;
  background-color: var(--color-bg-popup);
  border-radius: 4px;
  box-shadow: 0 2px 8px 0 rgba(0, 0, 0, 0.15);
}

.filter-issues-item {

  font-size: 12px;
  color: var(--color-neutral-7);
}

.filter-issues-item:hover {
  background-color: var(--color-neutral-1);
  cursor: pointer;
}

</style>