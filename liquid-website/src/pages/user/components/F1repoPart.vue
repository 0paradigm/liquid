<template>
  <a-space direction="vertical" style="width: 100%;">
    <div style="display: flex;justify-content: space-between;margin-left: 10px">
      <a-input-search style="border-radius: 10px; width: 300px"></a-input-search>
      <a-space>
        <a-select v-model="chooseType" style="border-radius: 10px;">
          <a-option v-for="type in types" :value="type.value" :label="type.content"></a-option>
        </a-select>
        <a-select v-model="sortBy" style="border-radius: 10px;">
          <a-option v-for="s in sort" :value="s.value" :label="s.content"></a-option>
        </a-select>
        <a-button @click="this.$router.push({name:'new'})" type="primary" style="border-radius: 10px">
          <icon-bookmark/>
          {{ $t('create') }}
        </a-button>
      </a-space>
    </div>
    <a-divider style="margin-top: 20px;margin-bottom: 0px;"/>
    <a-list :bordered="false">
      <a-list-item v-for="item in repos">
        <div class="captain" style="display: flex; flex-direction: row; justify-content: space-between">
          <div style="display: flex; flex-direction: row">
            <a-link @click="this.$router.push({name:'code', params:{user:item.owner, rep:item.name}})" class="name">
              {{ item.name }}
            </a-link>
            <a-tag v-if="item.privateRepo" class="tag" color="blue" style="border-radius: 10px;" size="small">
              private
            </a-tag>
            <a-tag v-else class="tag" color="green" style="border-radius: 10px;" size="small">
              public
            </a-tag>
          </div>
          <div>
            <a-button shape="round" @click="() => toggleStar(item.owner, item.name)">
              <template #icon>
                <icon-star/>
              </template>
              {{ $t('star') }}
              <a-badge :dotStyle="{ background: 'rgb(var(--arcoblue-6))' }" class="badge" :count="item.starCount"></a-badge>
            </a-button>
          </div>
        </div>
        <div class="description"> {{ item.description }}</div>
        <div style="display: flex; flex-direction: row; margin-top: 6px; margin-left: 15px;">
          <a-tag size="small" style="color: royalblue; width: 67px; text-align: center; align-items: center" color=blue>
            <div>{{ item.language || 'Unknown' }}</div>
          </a-tag>
          <a-link href="link" style="color: dimgrey; font-size: 11px; margin-left: 20px;" :hoverable="false">
            <template #icon>
              <icon-branch />
            </template>
            {{item.forkCount}}
          </a-link>
          <div style="color: dimgrey; font-size: 12px; margin-left: 20px">
            Updated at today{{!!item.forkedFrom ? ' | Forked from ' + item.forkedFrom : ''}}
          </div>
        </div>
      </a-list-item>
    </a-list>

  </a-space>
</template>

<script>
import request from "@/utils/request";
import {BACKEND_URL} from "@/utils/config";
import jsCookie from "js-cookie";
import {Message} from "@arco-design/web-vue";

export default {
  name: "repoPart",
  mounted() {
    this.init();
  },
  watch: {
    $route() {
      if (this.$route.name === 'user') {
        this.init()
      }
    },
  },
  methods: {
    init() {
      request.get(BACKEND_URL + `/core/api/user/repo/${this.$route.params.user}`)
          .then(res => {
        this.repos = res.data.data
      })
    },
    toggleStar(owner, repo) {
      if (!localStorage.getItem('inUser')) {
        this.$router.push('/login')
      }
      request.get(BACKEND_URL + `/core/api/repo/toggle_star/${owner}/${repo}`, {
        headers: {
          'Authorization': jsCookie.get('Authorization')
        }
      }).then(res => {
        Message.info(res.data.data.msg)
        this.init()
      })
    },
  },
  data() {
    return {
      chooseType: '1',
      types: [
        {value: '1', content: 'All'},
        {value: '2', content: 'Public'},
        {value: '3', content: 'Forks'}
      ],

      sortBy: '1',
      sort: [
        {value: '1', content: 'Last added'},
        {value: '2', content: 'Name'},
        {value: '3', content: 'Stars'},
      ],
      repos: []
    }
  }
}
</script>

<style scoped>
.name {
  margin-left: 15px;
  font-size: 20px;
  font-family: "Arial Rounded MT Bold";
  color: rgb(var(--arcoblue-5));
}

.tag {
  margin-left: 3px;
  color: dodgerblue;
}

.description {
  margin-left: 16px;
  margin-top: 7px;
}
</style>