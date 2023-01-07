<template>
  <div class="navbar">
    <div class="left-side">
      <a-space style="align-items: center">
        <router-link to="/">
          <img
              alt="logo"
              src="@/assets/logo-full.png"
              style="height: 50px; padding-left: 10px; padding-top: 12px"
          />
        </router-link>
      </a-space>
    </div>
    <ul class="right-side">
      <li class="search-container">
        <a-input class="search-text" @keyup.enter="jumpToSearch"></a-input>
        <a-button class="search-btn" type="outline" :shape="'circle'" @click="jumpToSearch">
          <template #icon>
            <icon-search/>
          </template>
        </a-button>
      </li>
      <li>

        <a-trigger trigger="click">
          <a-tooltip :content="$t('language')">
        <a-dropdown @select="changeLanguage">
          <a-button class="nav-btn" type="outline" :shape="'circle'">
            <template #icon>
              <icon-language/>
            </template>
          </a-button>
          <template #content>
            <a-doption value="zh-CN">简体中文</a-doption>
            <a-doption value="en-US">English</a-doption>
          </template>
        </a-dropdown>
          </a-tooltip>
        </a-trigger>

      </li>
      <li>
        <!--        <a-dropdown  trigger="hover" @select="changeBackground">-->

        <a-trigger trigger="click">
          <a-tooltip :content="$t('backgroundColor')">
            <a-button class="nav-btn" type="outline" :shape="'circle'">
              <template #icon>
                <icon-skin />
              </template>
            </a-button>
          </a-tooltip>

          <template #content>
            <t-color-picker-panel v-model=getBackgroundColor :color-modes="['linear-gradient']"  @change="changeBackground" :swatch-colors="systemColors"/>

          </template>
        </a-trigger>


      </li>
      <li>
        <a-button class="nav-btn" type="outline" :shape="'circle'">
          <template #icon>
            <icon-notification/>
          </template>
        </a-button>
      </li>
      <li>
        <a-button style="border-radius: 10px" v-if="!isLogin" @click="$router.push('/login')">{{ $t('login') }}
        </a-button>
        <a-dropdown v-else trigger="hover" >
          <a-button class="nav-btn" type="outline" :shape="'circle'" @click="jumpToUser">
            <template #icon>
              <img
                  :alt="user"
                  :src="BACKEND_URL() + '/media/get?uid=' + getUser?.login ?? ''"
                  style="height: 30px; width: 30px; border-radius: 30px"
              />
            </template>
          </a-button>
          <template #content>
            <a-doption @click="jumpToUser" style="text-align: center"> {{ getUser?.login ?? '' }}</a-doption>
            <a-doption @click="logout" style="text-align: center;">{{$t('SignOut')}}</a-doption>
          </template>
        </a-dropdown>
      </li>
    </ul>
  </div>
</template>

<script>
import {mapGetters, mapActions, mapMutations} from 'vuex'
import {BACKEND_URL} from "@/utils/config";

export default {
  data() {
    return {
      systemColors: ['linear-gradient(45deg, white 0%, white 100%)',  'linear-gradient(0deg,rgb(230, 249, 255) 0%,rgb(255, 255, 255) 100%)'],
      user: '',
    }
  },
  computed: {
    ...mapGetters('user', ['isLogin', 'getUser']),
    ...mapGetters(['getBackgroundColor'])
  },
  mounted() {
    this.getUserInfo()
  },
  methods: {
    BACKEND_URL() {
      return BACKEND_URL
    },
    ...mapActions('user', ['logout', 'getUserInfo']),
    ...mapMutations(['setBackgroundColor']),
    jumpToSearch() {
      this.$router.push('/search')
    },
    jumpToUser() {
      if (this.isLogin) {
        this.$router.push({name: 'user', params: {user: this.getUser.login}})
      }
    },
    changeLanguage(val) {
      this.$i18n.locale = val   // 设置当前语言
      localStorage.setItem('liquid-locale', val)
    },
    changeBackground(val){
      this.setBackgroundColor(val)
    }
  }
}

</script>

<style scoped lang="less">

.demo-basic {
  padding: 10px;
  width: 200px;
  background-color: var(--color-bg-popup);
  border-radius: 4px;
  box-shadow: 0 2px 8px 0 rgba(0, 0, 0, 0.15);
}

.language-item {
  font-size: 14px;
  height: 50px;
}

.language-item:hover {
  background-color: var(--color-neutral-2);
  cursor: pointer;
}

.search-container {
  border-radius: 25px;
  display: flex;
  justify-content: space-between;
  background-color: transparent;
  transition: 1s;
  margin-right: 15px;
}


.search-text {
  margin-left: 10px;
  border: none;
  outline: none;
  float: left;
  padding: 0;
  width: 0;
  color: transparent;
  transition: 1s;
  background-color: transparent;
}

.search-container .search-btn {
  padding: 0;
  margin-right: -10px;
  border-color: rgb(var(--gray-2));
  color: rgb(var(--gray-8));
  background-color: white;
  font-size: 16px;
  float: right;
}


.search-container:hover {
  background-color: var(--color-neutral-2);
  transition: 1s;
}

.search-container:focus-within {
  background-color: var(--color-neutral-2);
  transition: 1s;
}

.search-container:hover .search-text {
  width: 200px;
  background-color: transparent;
  color: black;
}

.search-container:hover .search-btn {
  background-color: rgb(var(--arcoblue-5));
  border-color: transparent;
  color: white;
}


.search-container:focus-within .search-text {
  width: 200px;
  background-color: transparent;
  color: black;
}


.search-container:focus-within .search-btn {
  background-color: rgb(var(--arcoblue-5));
  border-color: transparent;
  color: white;
}


.navbar {
  display: flex;
  justify-content: space-between;
  height: 100%;
  background-color: var(--color-bg-2);
  border-bottom: 1px solid var(--color-border);
}

.left-side {
  display: flex;
  align-items: center;
  padding-left: 20px;
}

.right-side {
  display: flex;
  padding-right: 20px;
  list-style: none;

  li {
    display: flex;
    align-items: center;
    padding: 0 10px;
  }

  .nav-btn {
    border-color: rgb(var(--gray-2));
    color: rgb(var(--gray-8));
    font-size: 16px;
  }


}

</style>

