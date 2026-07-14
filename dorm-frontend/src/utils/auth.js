import Cookies from 'js-cookie'

const TokenKey = 'dorm_token'
const UserInfoKey = 'dorm_user_info'

export function getToken() {
  return Cookies.get(TokenKey)
}

export function setToken(token) {
  return Cookies.set(TokenKey, token, { expires: 7 })
}

export function removeToken() {
  return Cookies.remove(TokenKey)
}

export function getUserInfo() {
  const info = Cookies.get(UserInfoKey)
  if (info) {
    try {
      return JSON.parse(info)
    } catch (e) {
      return null
    }
  }
  return null
}

export function setUserInfo(userInfo) {
  return Cookies.set(UserInfoKey, JSON.stringify(userInfo), { expires: 7 })
}

export function removeUserInfo() {
  return Cookies.remove(UserInfoKey)
}
