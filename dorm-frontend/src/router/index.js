import Vue from 'vue'
import VueRouter from 'vue-router'
import Layout from '@/layout/Index.vue'

Vue.use(VueRouter)

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/Index.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    meta: { title: '首页', icon: 'el-icon-s-home' },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/Index.vue'),
        meta: { title: '首页', icon: 'el-icon-s-home' }
      }
    ]
  },
  {
    path: '/baseInfo',
    component: Layout,
    redirect: '/baseInfo/building',
    meta: { title: '基础信息', icon: 'el-icon-office-building', roles: ['ADMIN'] },
    children: [
      {
        path: 'building',
        name: 'Building',
        component: () => import('@/views/building/Index.vue'),
        meta: { title: '楼栋管理', icon: 'el-icon-house' }
      },
      {
        path: 'room',
        name: 'Room',
        component: () => import('@/views/room/Index.vue'),
        meta: { title: '宿舍管理', icon: 'el-icon-s-grid' }
      },
      {
        path: 'bed',
        name: 'Bed',
        component: () => import('@/views/bed/Index.vue'),
        meta: { title: '床位管理', icon: 'el-icon-s-operation' }
      }
    ]
  },
  {
    path: '/student',
    component: Layout,
    redirect: '/student/list',
    meta: { title: '学生管理', icon: 'el-icon-user', roles: ['ADMIN', 'HOUSEMASTER'] },
    children: [
      {
        path: 'list',
        name: 'StudentList',
        component: () => import('@/views/student/Index.vue'),
        meta: { title: '学生信息', icon: 'el-icon-user-solid' }
      },
      {
        path: 'changeRoom',
        name: 'ChangeRoom',
        component: () => import('@/views/student/ChangeRoom.vue'),
        meta: { title: '调宿申请', icon: 'el-icon-s-promotion' }
      }
    ]
  },
  {
    path: '/check',
    component: Layout,
    redirect: '/check/record',
    meta: { title: '宿舍检查', icon: 'el-icon-edit-outline', roles: ['ADMIN', 'HOUSEMASTER'] },
    children: [
      {
        path: 'record',
        name: 'CheckRecord',
        component: () => import('@/views/check/Index.vue'),
        meta: { title: '卫生检查', icon: 'el-icon-edit' }
      },
      {
        path: 'violation',
        name: 'Violation',
        component: () => import('@/views/check/Violation.vue'),
        meta: { title: '违纪记录', icon: 'el-icon-warning' }
      }
    ]
  },
  {
    path: '/repair',
    component: Layout,
    redirect: '/repair/list',
    meta: { title: '报修管理', icon: 'el-icon-s-tools' },
    children: [
      {
        path: 'list',
        name: 'RepairList',
        component: () => import('@/views/repair/Index.vue'),
        meta: { title: '报修记录', icon: 'el-icon-s-tools' }
      }
    ]
  },
  {
    path: '/late',
    component: Layout,
    redirect: '/late/list',
    meta: { title: '晚归管理', icon: 'el-icon-time', roles: ['ADMIN', 'HOUSEMASTER'] },
    children: [
      {
        path: 'list',
        name: 'LateList',
        component: () => import('@/views/late/Index.vue'),
        meta: { title: '晚归登记', icon: 'el-icon-time' }
      },
      {
        path: 'leave',
        name: 'LeaveList',
        component: () => import('@/views/late/Leave.vue'),
        meta: { title: '请假报备', icon: 'el-icon-document' }
      }
    ]
  },
  {
    path: '/statistics',
    component: Layout,
    redirect: '/statistics/index',
    meta: { title: '数据统计', icon: 'el-icon-s-data', roles: ['ADMIN'] },
    children: [
      {
        path: 'index',
        name: 'Statistics',
        component: () => import('@/views/statistics/Index.vue'),
        meta: { title: '统计报表', icon: 'el-icon-s-data' }
      }
    ]
  },
  {
    path: '/system',
    component: Layout,
    redirect: '/system/user',
    meta: { title: '系统管理', icon: 'el-icon-setting', roles: ['ADMIN'] },
    children: [
      {
        path: 'user',
        name: 'User',
        component: () => import('@/views/system/User.vue'),
        meta: { title: '用户管理', icon: 'el-icon-s-custom' }
      }
    ]
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

export default router
