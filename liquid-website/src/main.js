import { createApp } from 'vue'
import App from './App.vue'
import store from './store'
import router from './router'
import ArcoVue from '@arco-design/web-vue'
import ArcoVueIcon from "@arco-design/web-vue/es/icon";
import TDesign from 'tdesign-vue-next';
import 'tdesign-vue-next/es/style/index.css';
import '@arco-design/web-vue/dist/arco.css'
import mavonEditor from 'mavon-editor'
import 'mavon-editor/dist/css/index.css'
import VueHighlightJS from 'vue-highlightjs'
import 'highlight.js/styles/default.css'
import hljsVuePlugin from '@highlightjs/vue-plugin'
import ECharts from 'vue-echarts'
import 'echarts';
import i18n from "./locale/index";
import VueRecaptcha from "vue3-recaptcha-v2";

const app = createApp(App);
app.component('ECharts', ECharts)
app.use(VueHighlightJS)
app.use(hljsVuePlugin)
app.use(App)
app.use(store)
app.use(router)
app.use(ArcoVue)
app.use(ArcoVueIcon)
app.use(TDesign)
app.use(mavonEditor)
app.use(i18n)
app.use(VueRecaptcha, {
    siteKey: "6LdEQ6EjAAAAAJ2okKuSDhRI3pvMpHQB9Wg-Rc2o",
    alterDomain: true,
})

app.mount('#app')
