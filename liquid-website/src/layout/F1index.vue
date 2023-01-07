<template>
  <div class="wrapper">
<!--    <a-layout>-->
      <a-layout-header style="position: fixed; width: 100%; z-index: 2; flex: 0">
        <layout-header/>
      </a-layout-header>

      <a-layout-content class="layout main" style="margin-top: 60.97px; z-index: 1; flex: 1">
        <router-view/>
      </a-layout-content>

      <footer style="flex: 0; margin-top: 30px">
        <div class="foot">
          <div class="copyright">
            <img src="@/assets/liquid-icon-grad.png" class="foot-logo" style="opacity: 0.3">

            <p>
              &copy; 2022
              <a class="foot-link" href="https://github.com/0paradigm/liquid">Liquid</a>.
              All Rights Reserved.
            </p>
          </div>

          <div class="copyright">
            <a href="https://github.com/0paradigm/liquid" class="foot-link">{{ $t('footer.about') }}</a>
            <t-divider layout="vertical"/>
            <a href="mailto:liquid@nobody.com" class="foot-link">{{ $t('footer.feedback') }}</a>
            <t-divider layout="vertical"/>
            <a href="https://cataas.com/cat/gif" class="foot-link">{{ $t('footer.lucky') }}</a>
          </div>
        </div>
      </footer>


      <div class="background" :style="{backgroundImage:getBackgroundColor}"></div>

<!--    </a-layout>-->
  </div>
</template>

<script>
import LayoutHeader from './header/F1index.vue';
import tAnimation from "@/components/animation/F1tAnimation.vue";
import {mapGetters} from 'vuex'

export default {
  name: "index",
  components: {
    LayoutHeader,
    tAnimation
  },
  computed: {
    ...mapGetters(['getBackgroundColor'])
  }
}
</script>

<style scoped>
.background {
  /*background-image: linear-gradient(125deg, white, rgb(var(--arcoblue-2)));*/

  top: 0;
  left: 0;
  position: fixed;
  height: 100%;
  width: 100%;
  z-index: -1;
}

@keyframes bgAnimation {
  0% {
    background-position: 0% 50%;
  }

  50% {
    background-position: 100% 50%;
  }

  100% {
    background-position: 0% 50%;
  }
}

.foot {
  background-color: rgba(211, 211, 211, 0.20);
  align-items: center;
  display: flex;
  justify-content: space-between;
}

.foot-logo {
  height: 20px;
  align-items: center;
  padding-right: 10px;
  filter: grayscale(95%);
  transition: all 0.3s;
}


.foot-logo:hover {
  height: 20px;
  align-items: center;
  padding-right: 10px;
}

.copyright {
  display: flex;
  padding: 20px 3%;
  color: rgba(0, 0, 0, 0.40);
  font-size: 90%;
  align-items: center;
}

.foot-link {
  text-decoration: none;
  color: rgba(0, 0, 0, 0.40);
}

#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;

  position: absolute;
  top: 0;
  bottom: 0;
  left: 0;
  right: 0;
}

html, body {
  height: 100%;
  margin: 0;
}

.wrapper {
  display: flex;
  flex-direction: column;
  height: 100%;
}
</style>