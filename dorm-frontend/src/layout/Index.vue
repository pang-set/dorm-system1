<template>
  <el-container class="layout-container">
    <el-aside :width="isCollapse ? '64px' : '220px'" class="sidebar">
      <div class="logo" :class="{ collapsed: isCollapse }">
        <i class="el-icon-s-home"></i>
        <span v-show="!isCollapse">宿舍管理系统</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :unique-opened="true"
        background-color="transparent"
        text-color="#bfcbd9"
        active-text-color="#ffffff"
        router>
        <template v-for="item in menuList">
          <el-submenu v-if="item.children && item.children.length > 1" :key="item.path" :index="item.path">
            <template slot="title">
              <i :class="item.meta && item.meta.icon"></i>
              <span slot="title">{{ item.meta && item.meta.title }}</span>
            </template>
            <el-menu-item
              v-for="child in item.children"
              :key="item.path + '/' + child.path"
              :index="item.path + '/' + child.path">
              <i :class="child.meta && child.meta.icon"></i>
              <span slot="title">{{ child.meta && child.meta.title }}</span>
            </el-menu-item>
          </el-submenu>
          <el-menu-item v-else :key="item.path" :index="getMenuItemIndex(item)">
            <i :class="item.meta && item.meta.icon"></i>
            <span slot="title">{{ item.meta && item.meta.title }}</span>
          </el-menu-item>
        </template>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="header">
        <div class="header-left">
          <i :class="isCollapse ? 'el-icon-s-unfold' : 'el-icon-s-fold'" @click="toggleCollapse"></i>
          <el-breadcrumb separator="/" class="breadcrumb">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-for="(crumb, idx) in breadcrumbs" :key="idx">
              {{ crumb }}
            </el-breadcrumb-item>
          </el-breadcrumb>
        </div>

        <div class="header-right">
          <el-tooltip content="消息通知" placement="bottom">
            <el-badge :value="noticeCount" :hidden="noticeCount === 0" class="header-icon">
              <i class="el-icon-bell" @click="onNotice"></i>
            </el-badge>
          </el-tooltip>

          <el-tooltip content="全屏切换" placement="bottom">
            <i class="el-icon-full-screen header-icon" @click="toggleFullScreen"></i>
          </el-tooltip>

          <el-dropdown trigger="click" @command="handleCommand">
            <div class="user-info">
              <div class="user-avatar">
                {{ avatarText }}
              </div>
              <span class="user-name">{{ userInfo.realName || userInfo.username }}</span>
              <i class="el-icon-arrow-down user-arrow"></i>
            </div>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="profile">
                <i class="el-icon-user"></i> 个人中心
              </el-dropdown-item>
              <el-dropdown-item command="changePassword">
                <i class="el-icon-key"></i> 修改密码
              </el-dropdown-item>
              <el-dropdown-item command="logout" divided>
                <i class="el-icon-switch-button"></i> 退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="main">
        <transition name="fade" mode="out-in">
          <router-view :key="$route.fullPath" />
        </transition>
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
export default {
  name: 'Layout',
  data() {
    return {
      isCollapse: false,
      noticeCount: 3
    }
  },
  computed: {
    userInfo() {
      return this.$store.getters.userInfo
    },
    activeMenu() {
      return this.$route.path
    },
    avatarText() {
      const name = this.userInfo.realName || this.userInfo.username || 'U'
      return name.charAt(0).toUpperCase()
    },
    breadcrumbs() {
      const matched = this.$route.matched.filter(item => item.meta && item.meta.title)
      return matched.map(item => item.meta.title)
    },
    menuList() {
      const role = this.$store.getters.role
      const routes = this.$router.options.routes
      return routes.filter(route => {
        if (route.path === '/login') return false
        if (route.path === '/' && route.children) {
          return true
        }
        if (route.meta && route.meta.roles) {
          return route.meta.roles.includes(role)
        }
        return true
      }).map(route => {
        if (route.children) {
          return {
            ...route,
            children: route.children.filter(child => {
              if (child.meta && child.meta.roles) {
                return child.meta.roles.includes(role)
              }
              return true
            })
          }
        }
        return route
      })
    }
  },
  mounted() {
    const saved = localStorage.getItem('sidebar_collapse')
    if (saved !== null) {
      this.isCollapse = saved === 'true'
    }
  },
  methods: {
    getMenuItemIndex(item) {
      if (item.redirect) {
        return item.redirect
      }
      if (item.children && item.children.length === 1) {
        const child = item.children[0]
        if (item.path === '/') {
          return '/' + child.path
        }
        return item.path + '/' + child.path
      }
      return item.path
    },
    toggleCollapse() {
      this.isCollapse = !this.isCollapse
      localStorage.setItem('sidebar_collapse', String(this.isCollapse))
    },
    toggleFullScreen() {
      const el = document.documentElement
      if (!document.fullscreenElement) {
        el.requestFullscreen && el.requestFullscreen()
      } else {
        document.exitFullscreen && document.exitFullscreen()
      }
    },
    onNotice() {
      this.$message.info('消息通知功能开发中')
    },
    handleCommand(command) {
      if (command === 'logout') {
        this.$confirm('确定要退出登录吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.$store.dispatch('logout')
          this.$router.push('/login')
        }).catch(() => {})
      } else if (command === 'profile') {
        this.$message.info('个人中心功能开发中')
      } else if (command === 'changePassword') {
        this.$message.info('修改密码功能开发中')
      }
    }
  }
}
</script>

<style lang="scss" scoped>
@import '@/assets/styles/variables.scss';

.layout-container {
  height: 100vh;
}


.sidebar {
  background: $sidebar-bg;
  transition: width 0.3s;
  overflow: hidden;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.1);
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: flex-start;
  padding: 0 20px;
  color: #fff;
  font-size: 16px;
  font-weight: bold;
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
  background: rgba(0, 0, 0, 0.15);
  overflow: hidden;
  white-space: nowrap;

  i {
    font-size: 24px;
    margin-right: 10px;
    color: $primary-color;
    flex-shrink: 0;
  }
}

::v-deep .el-menu {
  border-right: none;
}

// 菜单激活高亮条
::v-deep .el-menu-item {
  position: relative;
  transition: all $transition-fast;

  &:hover {
    background: rgba(255, 255, 255, 0.06) !important;
  }

  &.is-active {
    background: $primary-color !important;
    color: #fff !important;

    &::before {
      content: '';
      position: absolute;
      left: 0;
      top: 0;
      bottom: 0;
      width: 3px;
      background: #fff;
    }

    i { color: #fff !important; }
  }
}

::v-deep .el-submenu .el-menu-item.is-active {
  background: rgba(24, 144, 255, 0.2) !important;
  color: #fff !important;
  border-left: 3px solid $primary-color;

  &::before { display: none; }
}


.header {
  background: $header-bg;
  border-bottom: 1px solid $border-lighter;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
  z-index: 10;
}

.header-left {
  display: flex;
  align-items: center;
  gap: $spacing-md;

  > i {
    font-size: 20px;
    cursor: pointer;
    color: $text-regular;
    transition: color $transition-fast;

    &:hover {
      color: $primary-color;
    }
  }
}

.breadcrumb {
  font-size: $font-size-base;

  ::v-deep .el-breadcrumb__item__inner {
    color: $text-secondary;
    font-weight: normal;
  }

  ::v-deep .el-breadcrumb__item:last-child .el-breadcrumb__item__inner {
    color: $text-primary;
    font-weight: 600;
  }
}

.header-right {
  display: flex;
  align-items: center;
  gap: 18px;

  .header-icon {
    font-size: 18px;
    color: $text-regular;
    cursor: pointer;
    transition: color $transition-fast;
    padding: 6px;
    border-radius: $radius-circle;

    &:hover {
      color: $primary-color;
      background: rgba(24, 144, 255, 0.08);
    }
  }
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 4px 12px 4px 4px;
  border-radius: 20px;
  transition: background $transition-fast;

  &:hover {
    background: $bg-light;
  }

  .user-avatar {
    width: 32px;
    height: 32px;
    border-radius: 50%;
    background: $blue-gradient;
    color: #fff;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 14px;
    font-weight: 600;
    flex-shrink: 0;
    margin-right: 8px;
  }

  .user-name {
    color: $text-primary;
    font-size: $font-size-base;
    margin: 0 4px;
  }

  .user-arrow {
    font-size: 12px;
    color: $text-secondary;
  }
}


.main {
  background: $bg-page;
  padding: 0;
  overflow-y: auto;
}
</style>
