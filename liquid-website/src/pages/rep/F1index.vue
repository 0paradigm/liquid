<template>
  <div class="container">
    <div class="header">
      <div class="top-side">
        <a-space style="margin-left:30px;margin-top: 20px">

          <div style="display: flex; flex-direction: column">
            <div style="display: flex; flex-direction: row">
              <icon-bookmark style="font-size: 30px; stroke-width: 2; margin-right: 10px"/>
              <div>
                <user-link size="20px" :user="$route.params.user"></user-link>
                <a-divider direction="vertical"/>
                <a-link style="font-size: 20px">{{ $route.params.rep }}</a-link>
                <a-tag v-if="!getPrivateRepo" color="green"
                       style="margin-left:10px; margin-bottom:20px;border-radius: 10px">
                  {{ $t('public') }}
                </a-tag>
                <a-tag v-else color="blue" style="margin-left:10px; margin-bottom:20px;border-radius: 10px">
                  {{ $t('private') }}
                </a-tag>
              </div>
            </div>
            <div style="margin-left: 2px">
              {{ !!getForkedFrom ? 'forked from' : ''}}
              <a-link
                  @click="this.$router.push({name:'code', params:{user:getForkedUser, rep:getForkedRepo}})"
                  :hoverable="false"
                  style="justify-content: flex-start;">
                {{ getForkedFrom }}
              </a-link>
            </div>
          </div>
        </a-space>


        <a-space size="medium" style="margin-right: 60px; margin-top: 20px">

          <a-button shape="round" @click="toggleWatch">
            <template #icon>
              <icon-eye/>
            </template>
            {{ $t('watch') }}
            <a-badge :dotStyle="{ background: 'rgb(var(--arcoblue-6))' }" class="badge"
                     :count="getWatchCount"></a-badge>
          </a-button>

          <a-button shape="round" @click="doFork" :disabled="getPrivateRepo">
            <template #icon>
              <icon-share-alt/>
            </template>
            {{ $t('folk') }}
            <a-badge :dotStyle="{ background: 'rgb(var(--arcoblue-6))' }" class="badge" :count="getForkCount"></a-badge>
          </a-button>

          <a-button shape="round" @click="toggleStar">
            <template #icon>
              <icon-star/>
            </template>
            {{ $t('star') }}
            <a-badge :dotStyle="{ background: 'rgb(var(--arcoblue-6))' }" class="badge" :count="getStarCount"></a-badge>
          </a-button>
        </a-space>
      </div>

      <div class="bottom-side">
        <a-tabs :hide-content="true" :active-key="menuValue" @change="jump" size="large"
                style="font-size: 30px; margin-right: 30px">
          <a-tab-pane key="code">
            <template #title>
              <icon-code/>
              {{ $t('code') }}
            </template>
          </a-tab-pane>
          <a-tab-pane key="issue">
            <template #title>
              <icon-minus-circle/>
              {{ $t('issue') }}
            </template>
          </a-tab-pane>
          <a-tab-pane key="pull">
            <template #title>
              <icon-share-alt/>
              {{ $t('pull') }}
            </template>
          </a-tab-pane>
          <a-tab-pane key="insight">
            <template #title>
              <icon-bar-chart/>
              {{ $t('insight') }}
            </template>
          </a-tab-pane>
          <a-tab-pane key="setting" v-if="isMine">
            <template #title>
              <icon-settings/>
              {{ $t('setting') }}
            </template>
          </a-tab-pane>
        </a-tabs>

      </div>
    </div>

    <router-view/>
    <t-animation ani="base" style="position: fixed; left: 250px; top: 165px;z-index: -1"/>
  </div>

</template>

<script>
import userLink from "@/components/user/F1userLink.vue";
import tAnimation from "@/components/animation/F1tAnimation.vue";
import {BACKEND_URL} from "@/utils/config";
import request from "@/utils/request";
import {mapGetters, mapMutations} from "vuex"
import {Message} from "@arco-design/web-vue";
import jsCookie from "js-cookie";

export default {
  name: "index",
  components: {
    userLink,
    tAnimation,
  },
  created() {
    document.title = this.$route.params.user + '/' + this.$route.params.rep + ' - liquid'
  },
  data() {
    return {
      isMine: false,
      privateRepo: true,
      watchCount: 0,
      folkCount: 0,
      starCount: 0,
    }
  },
  computed: {
    ...mapGetters('rep', ['getPrivateRepo', 'getStarCount', 'getWatchCount', 'getForkCount', "getForkedFrom", "getForkedUser", "getForkedRepo"]),
    menuValue() {
      return this.$router.currentRoute.value.name
    },
  },
  watch: {
    $route() {
      if (this.$route.name !== 'notFound') {
        this.init()
      }
    },
  },
  mounted() {
    this.init()
  },

  methods: {
    ...mapMutations('rep', ['setPrivateRepo', 'setStarCount', 'setForkCount', 'setWatchCount', 'setDescription', 'setForkedFrom']),
    jump(value) {
      this.menuValue = value
      if (value === 'code') {
        console.log('code')
        this.$router.push({name: 'code'})
      } else if (value === 'issue') {
        console.log('issue')
        this.$router.push({name: 'issue'})
      } else if (value === 'pull') {
        this.$router.push({name: 'pull'})
      } else if (value === 'insight') {
        this.$router.push({name: 'insight'})
      } else if (value === 'setting') {
        this.$router.push({name: 'setting'})
      }
    },
    init() {
      this.isMine = localStorage.getItem('inUser') === this.$route.params.user
      request.get(BACKEND_URL + '/core/api/repo/meta/' + this.$route.params.user + '/' + this.$route.params.rep).then(res => {
        let data = res.data.data
        this.setPrivateRepo(data.privateRepo)
        this.setWatchCount(data.watchCount)
        this.setForkCount(data.forkCount)
        this.setStarCount(data.starCount)
        this.setDescription(data.description)
        this.setForkedFrom(data.forkedFrom)
      })
    },
    toggleStar() {
      if (!localStorage.getItem('inUser')) {
        this.$router.push('/login')
      }
      request.get(BACKEND_URL + `/core/api/repo/toggle_star/${this.$route.params.user}/${this.$route.params.rep}`, {
        headers: {
          'Authorization': jsCookie.get('Authorization')
        }
      }).then(res => {
        Message.info(res.data.data.msg)
        this.setStarCount(res.data.data.cnt)
      })
    },
    toggleWatch() {
      if (!localStorage.getItem('inUser')) {
        this.$router.push('/login')
      }
      request.get(BACKEND_URL + `/core/api/repo/toggle_watch/${this.$route.params.user}/${this.$route.params.rep}`, {
        headers: {
          'Authorization': jsCookie.get('Authorization')
        }
      })
          .then(res => {
            Message.info(res.data.data.msg)
            this.setWatchCount(res.data.data.cnt)
          })
    },
    doFork() {
      if (this.getPrivateRepo) {
        Message.error('You cannot create a fork of a private repository')
        return
      }
      this.$router.push({name: 'fork'})
    }
  }

}
</script>

<style scoped>
.tab {
  font-size: 30px;
}

.header {
  width: 100%;
  background-color: white;
  z-index: 1;
  /*position: fixed;*/
}

.container {
  width: 100%;
}

.top-side {
  width: 100%;
  display: flex;
  justify-content: space-between;
  /*margin-top: 20px;*/
}

.bottom-side {
  margin-top: 5px;
  margin-left: 30px;
}

.badge {
  margin-left: 7px;
  vertical-align: middle;
}

</style>