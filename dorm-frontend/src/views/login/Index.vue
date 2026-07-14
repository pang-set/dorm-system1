<template>
  <div class="login-container">
    <div class="login-brand">
      <div class="brand-content">
        <div class="brand-logo">
          <i class="el-icon-s-home"></i>
        </div>
        <h1 class="brand-title">学生宿舍管理系统</h1>
        <p class="brand-subtitle">Dormitory Management System</p>
        <p class="brand-footer">© 2026 学生宿舍管理系统</p>
      </div>
    </div>

    <div class="login-form-wrapper">
      <div class="form-box">
        <div class="form-header">
          <h2>欢迎登录</h2>
          <p>请输入您的账号信息</p>
        </div>

        <el-form
          ref="loginForm"
          :model="loginForm"
          :rules="loginRules"
          class="login-form"
          @keyup.enter.native="handleLogin">

          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              placeholder="请输入用户名"
              size="large"
              ref="usernameInput">
              <template slot="prefix">
                <i class="el-icon-user"></i>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              size="large"
              show-password>
              <template slot="prefix">
                <i class="el-icon-lock"></i>
              </template>
            </el-input>
          </el-form-item>

          <div class="form-options">
            <el-checkbox v-model="remember">记住我</el-checkbox>
            <a class="forgot-link" @click="onForgot">忘记密码？</a>
          </div>

          <el-button
            type="primary"
            size="large"
            :loading="loading"
            class="login-btn"
            @click="handleLogin">
            <span v-if="!loading">登 录</span>
            <span v-else>登录中...</span>
          </el-button>
        </el-form>

        <div class="role-cards">
          <p class="role-tip">演示账号</p>
          <div class="role-list">
            <div
              v-for="role in roleOptions"
              :key="role.username"
              class="role-card"
              :style="{ background: role.color }"
              @click="fillForm(role)">
              <i :class="role.icon"></i>
              <div class="role-info">
                <div class="role-name">{{ role.label }}</div>
                <div class="role-account">{{ role.username }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Login',
  data() {
    return {
      loginForm: {
        username: '',
        password: ''
      },
      loginRules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' }
        ]
      },
      loading: false,
      remember: false,
      roleOptions: [
        {
          label: '管理员',
          username: 'admin',
          password: '123456',
          icon: 'el-icon-s-custom',
          color: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'
        },
        {
          label: '宿管',
          username: 'housemaster',
          password: '123456',
          icon: 'el-icon-s-check',
          color: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)'
        },
        {
          label: '学生',
          username: 'student',
          password: '123456',
          icon: 'el-icon-user',
          color: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)'
        }
      ]
    }
  },
  mounted() {
    this.$nextTick(() => {
      this.$refs.usernameInput && this.$refs.usernameInput.focus()
    })
  },
  methods: {
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          this.loading = true
          this.$store.dispatch('login', this.loginForm).then(res => {
            this.$message.success('登录成功')
            const redirect = this.$route.query.redirect || '/'
            this.$router.push(redirect)
          }).catch(err => {
            this.$message.error(err.message || '登录失败')
          }).finally(() => {
            this.loading = false
          })
        }
      })
    },
    fillForm(role) {
      this.loginForm.username = role.username
      this.loginForm.password = role.password
      this.handleLogin()
    },
    onForgot() {
      this.$alert('请联系系统管理员重置密码', '忘记密码', {
        confirmButtonText: '我知道了',
        type: 'info'
      })
    }
  }
}
</script>

<style lang="scss" scoped>
@import '@/assets/styles/variables.scss';

.login-container {
  height: 100vh;
  display: flex;
  overflow: hidden;
  background: $bg-page;
}


.login-brand {
  flex: 1.2;
  background: $blue-gradient;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  color: #fff;

  .brand-content {
    position: relative;
    z-index: 2;
    padding: 60px;
    max-width: 520px;
  }

  .brand-logo {
    width: 80px;
    height: 80px;
    background: rgba(255, 255, 255, 0.18);
    border-radius: $radius-xl;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: $spacing-xl;
    backdrop-filter: blur(10px);
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);

    i {
      font-size: 44px;
      color: #fff;
    }
  }

  .brand-title {
    font-size: 36px;
    font-weight: 700;
    margin: 0 0 12px 0;
    letter-spacing: 1px;
  }

  .brand-subtitle {
    font-size: $font-size-md;
    opacity: 0.85;
    margin: 0 0 32px 0;
    letter-spacing: 0.5px;
  }

  .brand-footer {
    font-size: $font-size-xs;
    opacity: 0.5;
    margin: 0;
  }
}


.login-form-wrapper {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fff;
  padding: 40px;
  overflow-y: auto;
}

.form-box {
  width: 100%;
  max-width: 400px;
}

.form-header {
  margin-bottom: 36px;

  h2 {
    font-size: 28px;
    font-weight: 700;
    color: $text-primary;
    margin: 0 0 8px 0;
  }

  p {
    font-size: $font-size-base;
    color: $text-secondary;
    margin: 0;
  }
}

.login-form {
  ::v-deep .el-form-item__error {
    padding-top: 2px;
  }

  ::v-deep .el-input__inner {
    height: 46px;
    line-height: 46px;
    border-radius: $radius-base;
    background: $bg-light;
    border: 1px solid transparent;
    transition: all $transition-base;

    &:focus {
      background: #fff;
      border-color: $primary-color;
      box-shadow: 0 0 0 3px rgba(24, 144, 255, 0.1);
    }
  }

  ::v-deep .el-input__prefix {
    left: 12px;

    i {
      font-size: 18px;
      color: $text-secondary;
    }
  }

  ::v-deep .el-input .el-input__inner {
    padding-left: 40px;
  }
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: -8px 0 20px;

  .forgot-link {
    font-size: $font-size-base;
    color: $primary-color;
    cursor: pointer;
    text-decoration: none;
    transition: opacity $transition-fast;

    &:hover {
      opacity: 0.7;
    }
  }
}

.login-btn {
  width: 100%;
  height: 46px;
  font-size: $font-size-md;
  font-weight: 600;
  letter-spacing: 4px;
  background: $blue-gradient;
  border: none;
  border-radius: $radius-base;
  box-shadow: 0 4px 14px rgba(102, 126, 234, 0.4);
  transition: all $transition-base;

  &:hover {
    transform: translateY(-1px);
    box-shadow: 0 6px 20px rgba(102, 126, 234, 0.55);
  }

  &:active {
    transform: translateY(0);
  }
}


.role-cards {
  margin-top: 36px;
  padding-top: 28px;
  border-top: 1px dashed $border-lighter;
}

.role-tip {
  font-size: $font-size-sm;
  color: $text-secondary;
  margin: 0 0 $spacing-md;
  text-align: center;
}

.role-list {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}

.role-card {
  padding: 14px 12px;
  border-radius: $radius-lg;
  color: #fff;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all $transition-base;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);

  &:hover {
    transform: translateY(-3px);
    box-shadow: 0 8px 20px rgba(0, 0, 0, 0.18);
  }

  i {
    font-size: 22px;
    flex-shrink: 0;
  }

  .role-info {
    overflow: hidden;
  }

  .role-name {
    font-size: $font-size-base;
    font-weight: 600;
    line-height: 1.2;
  }

  .role-account {
    font-size: 11px;
    opacity: 0.85;
    margin-top: 2px;
  }
}

@media (max-width: 900px) {
  .login-container {
    flex-direction: column;
  }

  .login-brand {
    display: none;
  }
}
</style>
