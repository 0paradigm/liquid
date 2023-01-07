import { createStore } from 'vuex'
import user from './modules/user'
import direct from "@/store/modules/direct";
import rep from "./modules/rep";

export default createStore({
  state: {
    count: 1,
    backgroundColor: localStorage.getItem('liquid-backgroundColor') || 'white',
  },
  getters: {
    getBackgroundColor(state){
      return state.backgroundColor
    }
  },
  mutations: {
    setBackgroundColor(state, color){
      state.backgroundColor = color
      localStorage.setItem('liquid-backgroundColor', color)
    }
  },
  actions: {
  },
  modules: {
    rep,
    user,
    direct,

  }
})
