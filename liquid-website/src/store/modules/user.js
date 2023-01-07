import api from "@/api";
import {Message} from '@arco-design/web-vue';
import router from "@/router";
import {setToken, getToken, clearToken} from '@/utils/auth'
import request from "@/utils/request";
import {BACKEND_URL} from "@/utils/config";
import i18n from "@/locale";

export default {
    namespaced: true,
    state: {
        user: {
            login: ''
        },
        isLoading: false,
        token: getToken()
    },

    getters: {
        isLogin(state) {
            return !!state.token
        },
        isLoading(state) {
            return state.isLoading
        },
        getUser(state) {
            return state.user
        }
    },

    mutations: {
        setName(state, name) {
            state.name = name
        },

        setToken(state, token) {
            state.token = token
            setToken(token)
        },

        clearToken(state) {
            state.token = null
            clearToken()
        },

        setIsLoading(state, isLoading) {
            state.isLoading = isLoading
        },

        setUser(state, user) {
            state.user = user
        }
    },

    actions: {
        register({commit, dispatch}, [login, mail, password, phone]) {
            if (!/[A-Za-z]{3,16}/.test(login)) {
                if (i18n.global.locale === 'zh-CN') {
                    Message.error("用户名需3到16位字母")
                } else {
                    Message.error("Invalid username")
                }
                return
            }
            if (!/.{8,}/.test(password)) {
                if (i18n.global.locale === 'zh-CN') {
                    Message.error('密码长度至少8位')
                } else {
                    Message.error('Password too weak, must be at least 8 char')
                }
                return;
            }
            if (!/.+@.+\..+/.test(mail)) {
                if (i18n.global.locale === 'zh-CN') {
                    Message.error("邮箱格式非法")
                } else {
                    Message.error("Invalid email address")
                }
                return;
            }
            if (!/[0-9]{11}/.test(phone || '00000000000')) {
                if (i18n.global.locale === 'zh-CN') {
                    Message.error("手机号格式非法")
                } else {
                    Message.error("Invalid phone number")
                }
                return;
            }
            commit('setIsLoading', true)
            api.auth.login.register(login, mail, password, phone)
                .then(res => {
                    if (res.data.code !== 0) {
                        Message.error(res.data.msg)
                        return
                    }
                    dispatch('login', {login: login, password: password, type: 'password'})
                })
                .catch(res => Message.error(res.response.data.msg))
                .finally(() => commit('setIsLoading', false))
        },
        login({commit}, user) {
            if (!user.login) {
                if (i18n.global.locale === 'zh-CN') {
                    Message.error('用户名或者邮箱未填')
                } else {
                    Message.error('Username or email must be provided')
                }
                return
            }
            if (!user.password) {
                if (i18n.global.locale === 'zh-CN') {
                    Message.error('密码未填')
                } else {
                    Message.error('Password must be provided')
                }
                return;
            }
            commit('setIsLoading', true)
            api.login(user)
                .then(res => {
                    try {
                        commit('setUser', user)
                        commit('setToken', res.data.data.token)
                        request.get(BACKEND_URL + '/core/api/user/getName', {
                            params: {
                                login: user.login
                            }
                        }).then(resp => {
                            commit('setName', resp.data.data)
                            localStorage.setItem('inUser', resp.data.data)
                            if (i18n.global.locale === 'zh-CN') {
                                Message.success('欢迎')
                            } else {
                                Message.success('Welcome')
                            }
                            router.go(-1);
                        })
                    } catch (e) {
                        Message.error(res.data.msg)
                    }
                })
                .catch(res => Message.error(res.response.data.msg))
                .finally(() => commit('setIsLoading', false))
        },

        login222({commit}, user) {
            if (!user.phone) {
                if (i18n.global.locale === 'zh-CN') {
                    Message.error('手机号未填')
                } else {
                    Message.error('Phone must be provided')
                }

                return
            }
            if (!user.captcha) {
                if (i18n.global.locale === 'zh-CN') {
                    Message.error('验证码未填')
                } else {
                    Message.error('Captcha must be provided')
                }
                return;
            }
            commit('setIsLoading', true)
            request.post(BACKEND_URL + '/auth/sms', user)
                .then(res => {
                    try {
                        commit('setToken', res.data.data.token)
                        request.get(BACKEND_URL + '/core/api/user/getName', {
                            params: {
                                login: user.phone
                            }
                        }).then(resp => {
                            commit('setName', resp.data.data)
                            localStorage.setItem('inUser', resp.data.data)
                            router.go(-1);
                        })
                    } catch (e) {
                        Message.error(res.data.msg)
                    }
                })
                .catch(res => Message.error(res.response.data.msg))
                .finally(() => commit('setIsLoading', false))
        },
        logout({commit}) {
            localStorage.removeItem('inUser')
            commit('clearToken')
            if (i18n.global.locale === 'zh-CN') {
                Message.success('登出成功')
            } else {
                Message.success('Successfully logged out')
            }
            if (window.location.pathname === '/') {
                router.push('/')
                location.reload()
            } else {
                router.push('/')
            }
        },

        getUserInfo({commit, state}) {
            api.core.user.getUserInfo().then(res => {
                state.user = res.data.data
            })
        }
    }
};
