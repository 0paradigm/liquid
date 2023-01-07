<template>
  <a-timeline :reverse="false">
    <a-timeline-item v-if="cmts.length > 0" v-for="c in cmts">
      <a-typography-paragraph style="color: var(--color-neutral-7);font-size: 16px; margin-bottom: -5px">
        Committed on {{ c.date }}
      </a-typography-paragraph>
      <a-list style="margin-top: 10px; margin-bottom: 30px">
        <a-list-item v-for="item in c.commits" class="list-item">
          <commit-item :id="item.id" :user="item.user" :label="item.label" :time="item.ts" :disable-rollback-and-view="disableRollbackAndView"/>
        </a-list-item>
      </a-list>
    </a-timeline-item>

    <a-timeline-item style="opacity: 0.3;"> End of commit list</a-timeline-item>
  </a-timeline>
</template>

<script>
import commitItem from "@/components/commit/F1commitItem";
import dayjs from 'dayjs'

export default {
  name: "commitList",
  components: {
    commitItem
  },
  props: {
    commits: {default: []},
    disableRollbackAndView: {default: false}
  },
  computed: {
    cmts() {
      let res = []
      for (let i = 0; i < this.commits.length; i++) {
        let cmt = this.commits[i]
        let date = dayjs.unix(cmt.ts).format('YYYY/MM/DD')
        if (res.length === 0 || res[res.length - 1].date !== date) {
          res.push({
            date: date,
            commits: [cmt]
          })
        } else {
          res[res.length - 1].commits.push(cmt)
        }
      }
      console.log(res)
      return res
    }
  },
}
</script>

<style scoped>
.list-item{
  background-color: white;
}

.list-item:hover {
  background-color: var(--color-neutral-1);
}
</style>
