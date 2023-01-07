<template>
  <div style="display: flex; justify-content: center; margin-top: 20px">
    <a-row :gutter="24">
      <a-col flex="300px">
        <a-space direction="vertical">
          <a-avatar class="avatar" trigger-type="mask" :size="280" @click.stop="uploadHeadImg">
            <img
                :alt="$route.params.user"
                :src="this.avatar"
            />
            <template #trigger-icon>
              <IconEdit/>
            </template>
          </a-avatar>

          <input type="file" accept="image/*" @change="handleFile" class="hiddenInput"/>

          <div style="font-size: 20px">
            {{ $route.params.user }}
          </div>
          <div v-if="this.user.name!=='' && this.user.name!==null"
               style="font-size: 18px; color: var(--color-neutral-7)">
            {{ this.user.name }}
          </div>

          <div v-if="EditButtonIsShow === true">
            <a-list>
              <a-list-item>
                <div class="info-title">Bio</div>
                <div v-if="user_bio!=='' && user_bio!== null">{{ this.user_bio }}</div>
                <div v-else>nothing to show</div>
              </a-list-item>
              <a-list-item>
                <div class="info-title">Company</div>
                <div v-if="company!=='' && company!==null ">{{ this.company }}</div>
                <div v-else>nothing to show</div>
              </a-list-item>
              <a-list-item>
                <div class="info-title">Location</div>
                <div v-if="location!=='' && location!== null">{{ this.location }}</div>
                <div v-else>nothing to show</div>
              </a-list-item>
              <a-list-item>
                <div class="info-title">Twitter Username</div>
                <div v-if="twitter_username!=='' && twitter_username!== null">{{ this.twitter_username }}</div>
                <div v-else>nothing to show</div>
              </a-list-item>
            </a-list>
          </div>
          <a-button style="margin-top: 10px" shape="round" type="outline" long @click="clickEdit"
                    v-show="EditButtonIsShow">{{ $t('edit') }}
          </a-button>
          <div v-show="isShow">
            <div class="edit-title">{{ $t('Name') }}</div>
            <a-input class="input" placeholder="Name"
                     v-model=name
                     allow-clear/>
            <div class="edit-title">{{ $t('Bio') }}</div>
            <a-textarea class="input" placeholder="Bio"
                        v-model=user_bio
                        allow-clear/>

            <div class="edit-box">
              <icon-idcard class="edit-icon"/>
              <a-input class="small-input" placeholder="Company"
                       v-model=company
                       allow-clear/>
            </div>

            <div class="edit-box">
              <icon-home class="edit-icon"/>
              <a-input class="small-input" placeholder="Location"
                       v-model=location
                       allow-clear/>
            </div>

            <div class="edit-box">
              <icon-twitter class="edit-icon"/>
              <a-input class="small-input" placeholder="Twitter Username"
                       v-model=twitter_username
                       allow-clear/>
            </div>

            <div class="edit-box" style="margin-top: 15px">
              <a-button shape="round" type="primary" @click="clickSave">{{ $t('Save') }}</a-button>
              <a-button shape="round" style="margin-left: 8px" @click="clickCancel">{{ $t('Cancel') }}</a-button>
            </div>

          </div>
        </a-space>
      </a-col>
      <a-col flex="auto">
        <a-tabs :destroy-on-hide="true" :default-active-key="selectKey" style="width: 750px" lazy-load>
<!--          <a-tab-pane key="1">-->
<!--            <template #title>-->
<!--              <icon-book/>-->
<!--              {{ $t('overview') }}-->
<!--              <a-badge-->
<!--                  :count="0"-->
<!--                  :dotStyle="{ background: '#E5E6EB', color: '#86909C' }"-->
<!--              />-->
<!--            </template>-->
<!--            <overview-part/>-->
<!--          </a-tab-pane>-->
          <a-tab-pane key="2">
            <template #title>
              <icon-storage/>
              {{ $t('repositories') }}
            </template>
            <repo-part/>
          </a-tab-pane>
          <a-tab-pane key="3">
            <template #title>
              <icon-star/>
              {{ $t('star') }}
            </template>
            <star-part/>
          </a-tab-pane>
        </a-tabs>
      </a-col>
    </a-row>
  </div>
</template>

<script>
import overviewPart from "@/pages/user/components/F0overviewPart";
import repoPart from "@/pages/user/components/F1repoPart";
import starPart from "@/pages/user/components/F1starPart";
import request from "@/utils/request";
import api from "@/api";
import {BACKEND_URL} from "@/utils/config";
import router from "@/router";

export default {
  components: {
    overviewPart,
    repoPart,
    starPart
  },
  created() {
    document.title = this.$route.params.user + ' - liquid'
  },
  mounted() {
    this.init()
  },
  computed: {
    avatar() {
      return BACKEND_URL + '/media/get?uid=' + this.$route.params.user
    }
  },
  watch: {
    $route() {
      if (this.$route.name === 'user') {
        this.init()
      }
    },
  },
  name: "index",
  data() {
    return {
      selectKey: '2',
      name: '',
      user_bio: '',
      company: '',
      location: '',
      twitter_username: '',
      phone: '',
      isShow: false,
      EditButtonIsShow: true,
      user: '',
    }
  },
  methods: {
    BACKEND_URL() {
      return BACKEND_URL
    },

    clickEdit() {
      this.isShow = !this.isShow;
      if (this.isShow === false) {
        this.EditButtonIsShow = true
      } else {
        this.EditButtonIsShow = false
      }
    },

    clickCancel() {
      this.user_bio = this.user.bio
      this.location = this.user.location
      this.twitter_username = this.user.twitterUsername
      this.company = this.user.company
      this.name = this.user.name
      this.phone = this.user.phone
      this.isShow = false;
      this.EditButtonIsShow = true
    },

    clickSave() {
      this.isShow = false;
      this.EditButtonIsShow = true;
      request.post(BACKEND_URL + '/core/api/user/update?phone=' + this.phone + "&bio=" + this.user_bio
          + "&company=" + this.company + "&location=" + this.location + "&twitter_username=" + this.twitter_username
          + "&name=" + this.name,
          {
            phone: this.phone,
            bio: this.user_bio,
            company: this.company,
            location: this.location,
            twitter_username: this.twitter_username
          }).then(res => {
        this.$message.success('修改成功')
        this.init();
      })
    },

    init() {
      api.core.user.findUserByNameOrMail(this.$route.params.user || '').then(res => {
        if (res.data.code === 1001) {
          if (this.$i18n.locale === 'zh-CN') {
            this.$message.error('此用户不存在')
          } else {
            this.$message.error('doesn\'t exist this user')
          }

          this.$router.push({
            name: 'notFound',
            params: {pathMatch: router.currentRoute.value.path.substring(1).split('/')}
          })
        } else {
          var data = res.data.data
          this.user = data
          this.user_bio = data.bio
          this.location = data.location
          this.twitter_username = data.twitterUsername
          this.company = data.company
          this.name = data.name
          this.phone = data.phone
        }
      })
    },

    // 打开图片上传
    uploadHeadImg: function () {
      if (localStorage.getItem('inUser') !== this.$route.params.user) {
        return
      }
      this.$el.querySelector('.hiddenInput').click()
    },
    // 将头像显示
    handleFile: function (e) {
      let $target = e.target
      let file = $target.files[0]
      var reader = new FileReader()
      reader.onload = (data) => {
        let res = data.target
        this.avatar = res.result
      }
      reader.readAsDataURL(file)

      let param = new FormData(); //创建form对象
      param.append('file', file);//通过append向form对象添加数据
      console.log(param.get('file')); //FormData私有类对象，访问不到，通过get判断值是否传进去
      //设置请求头
      let config = {
        headers: {'Content-Type': 'multipart/form-data'}
      };
      request.post(BACKEND_URL + '/media/upload?uid=' + this.$route.params.user, param, config)
          .then(response => {
            console.log(response.data);
          })
    },

  }
}
</script>

<style scoped>

.container {
  display: flex;
  flex-direction: row;
  margin-top: 42px;
  margin-left: 50px;
}

.avatar_button {
  margin-top: 19px;
}

.tabs {
  margin-left: 45px;
}

.name {
  margin-left: 25px;
  margin-top: 10px;
}

.hiddenInput {
  display: none;
}

.edit-title {
  margin-top: 10px;
  font-family: "Arial Rounded MT Bold";
}

.input {
  border-radius: 10px;
  margin-top: 5px;
}

.edit-icon {
  font-size: 20px;
  margin-right: 7px;
  margin-top: 7px;
}

.small-input {
  border-radius: 10px;
  margin-top: 5px;
  height: 25px;
}

.edit-box {
  display: flex;
  flex-direction: row;
  margin-top: 8px;
}

.info-title {
  font-weight: bold;
  font-size: 15px;
}

.info-text {
  font-size: 10px;
}
</style>