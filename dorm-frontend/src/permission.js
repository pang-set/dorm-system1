import router from './router'
import { getToken } from '@/utils/auth'

const whiteList = ['/login']

router.beforeEach((to, from, next) => {
  document.title = to.meta.title ? to.meta.title + ' - 学生宿舍管理系统' : '学生宿舍管理系统'

  if (getToken()) {
    if (to.path === '/login') {
      next('/')
    } else {
      next()
    }
  } else {
    if (whiteList.indexOf(to.path) !== -1) {
      next()
    } else {
      next('/login?redirect=' + to.path)
    }
  }
})
