import Vue from 'vue'
import Vuex from 'vuex'
import { getToken, setToken, removeToken, getUserInfo, setUserInfo, removeUserInfo } from '@/utils/auth'
import { login, getAuthUserInfo } from '@/api/auth'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    token: getToken(),
    userInfo: getUserInfo() || {}
  },
  mutations: {
    SET_TOKEN(state, token) {
      state.token = token
    },
    SET_USER_INFO(state, userInfo) {
      state.userInfo = userInfo
    }
  },
  actions: {
    login({ commit }, userInfo) {
      return new Promise((resolve, reject) => {
        login(userInfo).then(res => {
          if (res.code === 200) {
            commit('SET_TOKEN', res.data.token)
            commit('SET_USER_INFO', res.data)
            setToken(res.data.token)
            setUserInfo(res.data)
            resolve(res)
          } else {
            reject(res)
          }
        }).catch(error => {
          reject(error)
        })
      })
    },
    logout({ commit }) {
      commit('SET_TOKEN', '')
      commit('SET_USER_INFO', {})
      removeToken()
      removeUserInfo()
    }
  },
  getters: {
    token: state => state.token,
    userInfo: state => state.userInfo,
    role: state => state.userInfo.role || '',
    userId: state => state.userInfo.userId || null,
    realName: state => state.userInfo.realName || ''
  }
})
