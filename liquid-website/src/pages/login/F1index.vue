<template>

  <t-loading :loading="isLoading" fullscreen/>
  <div class="container" ref="container">

    <div class="panels-container">
      <div class="panel left-panel">
        <div class="panel-content">
          <h3>{{$t('newHere')}}</h3>
          <a-button class="panel-btn" @click="toSignUp">{{$t('signup')}}</a-button>
        </div>
        <img class="panel-img" src="@/assets/liquid-icon-grad.png"  style="scale: 0.8" alt=""/>
      </div>

      <div class="panel right-panel">
        <div class="panel-content">
          <h3>{{$t('oneOfUs')}}</h3>
          <a-button class="panel-btn" @click="toSignIn">{{$t('login')}}</a-button>
        </div>
        <img class="panel-img" src="@/assets/liquid-icon-grad.png" style="scale: 0.8" alt=""/>
      </div>
    </div>

    <div class="forms-container">
      <div class="signin-signup">
        <div class="sign-in-form">
          <div class="login-form-title">{{$t('signInLiquid')}}</div>
          <div class="login-form-sub-title">{{$t('signInLiquid')}}</div>

          <a-tabs class="login-form-tab" default-active-key="1" v-model:active-key="loginMethod">
            <a-tab-pane key="1" :title="$t('passwordVerification')">
              <a-input class="login-form-input" v-model="user.login" placeholder="Username / Email">
                <template #prefix>
                  <icon-user/>
                </template>
              </a-input>
              <a-input-password class="login-form-input" v-model="user.password" placeholder="Password">
                <template #prefix>
                  <icon-lock/>
                </template>
              </a-input-password>

              <div class="login-form-password-actions">
                <a-checkbox v-model="savePassword">
                  {{$t('save')+$t('password')}}
                </a-checkbox>
                <a-link>
                  {{$t('forget')+$t('password')}}?
                </a-link>
              </div>

            </a-tab-pane>
            <a-tab-pane key="2" :title="$t('cellphoneVerification')">
              <a-input class="login-form-input" :placeholder="$t('phoneNumber')" v-model="userCpc.phone">
                <template #prefix>
                  <icon-phone/>
                </template>
              </a-input>

              <div class="login-form-box">
                <a-input class="login-form-verification" :placeholder="$t('messageCode')" v-model="userCpc.captcha">
                  <template #prefix>
                    <icon-email/>
                  </template>
                </a-input>
                <a-button class="login-form-smallbtn" type="outline" :disabled="getCodeBtnDisable" @click="getCode()">
<!--                  {{ this.codeBtnWord }}-->
                  {{$t('getVerification')}}
                </a-button>
              </div>

            </a-tab-pane>
          </a-tabs>

          <vue-recaptcha
              theme="light"
              size="normal"
              style="scale: 0.8; padding-top: 5px; align-self: center"
              :tabindex="0"
              @widgetId="recaptchaWidget = $event"
              @verify="canLogin=true"

          >
          </vue-recaptcha>

          <a-button class="login-form-btn" type="primary" :disabled="!canLogin" @click="signIn()" long>
            {{$t('login')}}
          </a-button>
<!--          <a-button class="login-form-btn" @click="toSignUp" type="text" long>-->
<!--            Create an account-->
<!--          </a-button>-->
        </div>

        <div class="sign-up-form">
          <div class="login-form-title">{{$t('signUpLiquid')}}</div>
          <div class="login-form-sub-title">{{$t('signUpLiquid')}}</div>
          <a-input class="login-form-input" v-model="newUser.name" :placeholder="$t('Username')">
            <template #prefix>
              <icon-user/>
            </template>
          </a-input>
          <a-input-password class="login-form-input" v-model="newUser.password" :placeholder="$t('Password')">
            <template #prefix>
              <icon-lock/>
            </template>
          </a-input-password>
          <a-input class="login-form-input" v-model="newUser.email" :placeholder="$t('Email')">
            <template #prefix>
              <icon-email/>
            </template>
          </a-input>
          <a-input class="login-form-input" v-model="newUser.phone" :placeholder="$t('Phone')">
            <template #prefix>
              <icon-phone/>
            </template>
          </a-input>
          <div class="login-form-password-actions">
          </div>


          <vue-recaptcha
              theme="light"
              size="normal"
              style="scale: 0.8;margin-top: -20px"
              :tabindex="0"
              @widgetId="recaptchaWidget = $event"
              @verify="canSignUp=true"
          />

          <a-button class="login-form-btn" type="primary" :disabled="!canSignUp" @click="signUp()" long>
            {{ $t('signup') }}
          </a-button>
<!--          <a-button class="login-form-btn" @click="toSignIn" type="text" long>-->
<!--            {{ $t('login') }}-->
<!--          </a-button>-->
        </div>
      </div>
    </div>
  </div>
</template>


<script setup>
import {ref} from "vue";

let canLogin = ref(false);
let canSignUp = ref(false);
</script>

<script>
import {mapActions, mapGetters} from 'vuex'
import api from "@/api";
import {Message} from "@arco-design/web-vue";
import router from "@/router";


import {VueRecaptcha, useRecaptcha} from "vue3-recaptcha-v2";
import {BACKEND_URL} from "@/utils/config";
import request from "@/utils/request";


export default {
  name: "index",
  computed: {
    ...mapGetters('user', ['isLoading']),
    getCodeBtnDisable: {
      get() {
        if (this.waitTime == 61) {
          return !/[0-9]{11}/.test(this.userCpc.phone)
        }
        return true
      },
      set() {
      }
    }
  },
  components: {VueRecaptcha},
  setup: () => {
    const {resetRecaptcha} = useRecaptcha();
    const recaptchaWidget = ref(null);

    const callbackVerify = (response) => {
      console.log(response);
    };
    const callbackFail = () => {
      console.log("fail");
    };
    const actionReset = () => {
      resetRecaptcha(recaptchaWidget.value);
    };

    return {
      recaptchaWidget,
      callbackVerify,
      callbackFail,
      actionReset,
    };
  },
  created() {
    document.title = 'Login-liquid'
  },
  data() {
    return {
      codeBtnWord: 'Get Verification',
      waitTime: 61,
      flag: true,
      loginMethod: '1',
      user: {
        login: '',
        password: '',
        type: 'password'
      },
      userCpc: {
        phone: '',
        captcha: '',
        type: 'phone-captcha',
      },
      savePassword: false,
      newUser: {
        name: '',
        password: '',
        email: '',
        phone: '',
      },
      radius: '10px'
    }
  },
  mounted() {
    if (localStorage.getItem('user')) {
      this.user.login = localStorage.getItem('user')
      if (localStorage.getItem('password')) {
        this.user.password = localStorage.getItem('password')
        this.savePassword = true
      }
    }
  },
  methods: {
    ...mapActions('user', ['login', 'login222', 'register']),
    getCode() {
      if (!/[0-9]{11}/.test(this.userCpc.phone)) {
        if(this.$i18n.locale==='zh-CN'){
          Message.error('手机号格式不合法')
        }else {
          Message.error('Invalid phone number')
        }

        return
      }
      request.post(BACKEND_URL + '/auth/captcha', {}, {
        params: {
          phone: this.userCpc.phone
        }
      }).then(resp => {
        if (resp.data.code !== 0) {
          Message.error(resp.data.msg)
        }
      }).catch(err => Message.error(err))
      let that = this
      that.waitTime--
      that.getCodeBtnDisable = true
      this.codeBtnWord = `Get again after ${this.waitTime}s `
      let timer = setInterval(function () {
        if (that.waitTime > 1) {
          that.waitTime--
          that.codeBtnWord = `Get again after ${that.waitTime}s `
        } else {
          clearInterval(timer)
          that.codeBtnWord = 'Get Verification'
          that.getCodeBtnDisable = false
          that.waitTime = 61
        }
      }, 1000)
    },
    toSignUp() {
      this.$refs.container.classList.add('sign-up-mode')
    },
    toSignIn() {
      this.$refs.container.classList.remove('sign-up-mode')
    },
    signIn() {
      if (this.loginMethod == '1') {
        if (this.savePassword) {
          localStorage.setItem('user', this.user.login)
          localStorage.setItem('password', this.user.password)
        } else {
          // localStorage.removeItem('user')
          localStorage.setItem('user', this.user.login)
          localStorage.removeItem('password')
        }
        this.login(this.user)
      } else {
        this.login222(this.userCpc)
      }
    },


    signUp() {
      this.register([this.newUser.name, this.newUser.email, this.newUser.password, this.newUser.phone])
    }
  }
}
</script>


<style lang="less" scoped>
.container {
  position: relative;
  width: 100%;
  min-height: 100vh;
  font-size: 14px;
  font-family: sans-serif;
  background-color: white;
  overflow: hidden;
}

.container::before {
  content: '';
  position: absolute;
  top: -10%;
  right: 48%;
  transform: translateY(-50%);
  transition: 1.8s ease-in-out;
  width: 2000px;
  height: 2000px;
  background-image: linear-gradient(-45deg, rgba(36, 154, 255, 1) 0%, rgba(111, 66, 251, 1) 80%);
  border-radius: 50%;
  z-index: 3;
}

.panels-container {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: grid;
  grid-template-columns: repeat(2, 1fr);
}

.panel {
  //display: flex;
  //flex-direction: column;
  //justify-content: space-around;
  //align-items: center;
  text-align: center;
  z-index: 9;

  .panel-content{
    color: white;
    transition: transform 0.9s ease-in-out;
    transition-delay: 0.6s;

    h3 {
      font-size: 22px;
      font-weight: 600;
      line-height: 1;
    }

    p {
      padding: 10px 0;
    }

    .panel-btn {
      width: 130px;
      height: 41px;
      outline: none;
      border: 2px solid white;
      border-radius: 49px;
      background: none;
      color: white;
      font-weight: 600;
      text-transform: uppercase;
      transition: 0.5s;
    }

    .panel-btn:hover{
      background-color: white;
      color: rgba(36, 154, 255, 1);
    }
  }

  .panel-img{
    width: 50%;
    transition: transform 1.1s ease-in-out;
    transition-delay: 0.6s;
  }

}

.left-panel {
  pointer-events: all;
  padding: 42px 17% 28px 12%;
}

.right-panel {
  pointer-events: none;
  padding: 42px 12% 28px 17%;

  .panel-content,
  .panel-img {
    transform: translateX(800px);
  }
}

.container.sign-up-mode::before {
  right: 52%;
  transform: translate(100%, -50%);
}

.container.sign-up-mode {
  .left-panel {
    pointer-events: none;

    .panel-content,
    .panel-img {
      transform: translateX(-800px);
    }
  }
;

  .right-panel {
    pointer-events: all;

    .panel-content,
    .panel-img {
      transform: translateX(0px);
    }
  }

  .signin-signup {
    left: 30%;
  }

  .sign-up-form {
    z-index: 2;
    opacity: 1;
  }

  .sign-in-form {
    z-index: 1;
    opacity: 0;
  }

}

.forms-container {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
}

.signin-signup {
  position: absolute;
  top: 50%;
  left: 70%;
  transform: translate(-50%, -50%);
  transition: 1s 0.7s ease-in-out;
  width: 50%;
  display: grid;
  grid-template-columns: 1fr;
}

.sign-up-form,
.sign-in-form {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  grid-column: 1 / span 1;
  grid-row: 1 / span 1;
  transition: all 0.2s 0.7s;
}

.sign-up-form {
  z-index: 1;
  opacity: 0;
}

.sign-in-form {
  z-index: 2;
}


@media screen and(max-width: 870px) {
  .container {
    min-height: 530px;
    height: 100vh;
  }

  .container::before {
    left: 30%;
    bottom: 68%;
    top: initial;
    right: initial;
    transform: translateX(-50%);
    transition: 2s ease-in-out;
    width: 1500px;
    height: 1500px;
  }

  .panels-container {
    grid-template-columns: 1fr;
    grid-template-rows: 1fr 2fr 1fr;
  }

  .panel {
    display: flex;
    flex-direction: row;
    justify-content: space-around;
    align-items: center;
    grid-column: 1 / span 1;
    padding: 35px 8%;

    .panel-content {
      padding-right: 15%;
      transition: transform 0.9s ease-in-out;
      transition-delay: 0.8s;

      h3 {
        font-size: 18px;
      }

      p {
        font-size: 12px;
        padding: 8px 0;
      }

      .panel-btn {
        width: 110px;
        height: 35px;
        font-size: 12px;
      }
    }

    .panel-img {
      width: 200px;
      transition: transform 0.9s ease-in-out;
      transition-delay: 0.6s;
    }

  }

  .left-panel {
    grid-row: 1 / span 1;
  }

  .right-panel {
    grid-row: 3 / span 1;

    .panel-content,
    .panel-img {
      transform: translateY(300px);
    }
  }

  .container.sign-up-mode::before {
    right: initial;
    bottom: 32%;
    transform: translate(-50%, 100%);
  }

  .container.sign-up-mode {
    .left-panel {
      .panel-content,
      .panel-img {
        transform: translateY(-300px);
      }
    }
  ;

    .right-panel {
      .panel-content,
      .panel-img {

        transform: translateY(0px);
      }
    }

    .signin-signup {
      top: -0%;
      left: 50%;
      transform: translate(-50%, 15%);
    }
  }

  .signin-signup {
    top: 105%;
    left: 50%;
    transform: translate(-50%, -120%);
    transition: 1s 0.8s ease-in-out;
    width: 100%;
  }
}

@media screen and(max-width: 570px) {
  .container {
    padding: 20px;
  }

  .container::before {
    left: 30%;
    bottom: 72%;
  }


  .panel {
    .panel-content {
      padding: 8px 14px;
    }

    .panel-img {
      display: none;
    }

  }

  .container.sign-up-mode::before {
    left: 30%;
    bottom: 28%;
  }

  .sign-in-form,
  .sign-up-form {
    padding: 0 2px;
  }
}


.login-form {


  &-title {
    color: var(--color-text-1);
    font-weight: 500;
    width: 100%;
    max-width: 380px;
    text-align: left;
    font-size: 24px;
    line-height: 32px;
  }

  &-sub-title {
    width: 100%;
    max-width: 380px;
    text-align: left;
    color: var(--color-text-3);
    font-size: 16px;
    line-height: 24px;
    margin-bottom: 10px;
  }

  &-error-msg {
    height: 32px;
    color: rgb(var(--red-6));
    line-height: 32px;
  }

  &-password-actions {
    width: 100%;
    max-width: 380px;
    display: flex;
    justify-content: space-between;
    margin-top: 20px;
    margin-bottom: 10px;
  }

  //&-register-btn {
  //  width: 100%;
  //  max-width: 380px;
  //  color: var(--color-text-3) !important;
  //}

  &-input {
    width: 100%;
    max-width: 380px;
    border-radius: 10px;
    margin-top: 10px;
  }

  &-btn {
    width: 100%;
    max-width: 380px;
    border-radius: 10px;
    margin-top: 10px;
  }

  &-box {
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    width: 380px;
  }

  &-tab {
    width: 400px;
  }

  &-verification {
    width: 100%;
    max-width: 215px;
    border-radius: 10px;
    margin-top: 10px;
    margin-bottom: 30px;
  }

  &-smallbtn {
    width: 100%;
    max-width: 160px;
    border-radius: 10px;
    margin-top: 10px;
    margin-left: 10px;
  }
}

</style>