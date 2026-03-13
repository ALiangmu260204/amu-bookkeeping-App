<template>
  <el-row class="login-page">
    <el-col :span="12" class="bg"></el-col>
    <el-col :span="6" :offset="3" class="form">
      <!-- 登录表单 -->
      <el-form :model="formModel" :rules="rules" ref="form" size="large">
        <el-form-item>
          <h1>登录</h1>
        </el-form-item>
        <el-form-item prop="username">
          <el-input v-model="formModel.username" :prefix-icon="User" :clearable="true" placeholder="用户名" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="formModel.password" :prefix-icon="Lock" type="password" :clearable="true" placeholder="密码" />
        </el-form-item>
        <el-form-item prop="captcha">
          <div class="wrap_captcha">
            <el-input v-model="formModel.captcha" :prefix-icon="Lock" placeholder="验证码" />
            <el-image :src="`data:image/png;base64,${captchaSrc}`" @click="getCaptcha" class="img_captcha" />
          </div>
        </el-form-item>
        <el-form-item>
          <el-checkbox v-model="formModel.rememberMe" size="default">记住我（7天）</el-checkbox>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="default" @click="tapLogin" class="btn_remember">登录</el-button>
        </el-form-item>
      </el-form>
    </el-col>
  </el-row>
</template>

<script setup>
  import { onMounted, ref } from 'vue'
  import { User, Lock } from '@element-plus/icons-vue'
  import { userAuthService, userLoginService } from '@/api/user'
  import { ElMessage } from 'element-plus'
  import { useUserStore } from '@/stores'
  import { useRouter } from 'vue-router'

  const router = useRouter()
  const form = ref()
  const formModel = ref({ username: '', password: '', captcha: '', uuid: '', rememberMe: false })
  const rules = {
    username: [
      { required: true, message: '请输入用户名', trigger: 'blur' },
      { pattern: /^\S/, message: '用户名不能为空', trigger: 'blur' }
    ],
    password: [
      { required: true, message: '请输入密码', trigger: 'blur' },
      { pattern: /^\S/, message: '密码不能为空', trigger: 'blur' }
    ],
    captcha: [
      { required: true, message: '请输入验证码', trigger: 'blur' },
      { pattern: /^\S/, message: '密码不能为空', trigger: 'blur' }
    ]
  }
  const captchaUUID = ref('')
  const captchaSrc = ref(null)

  onMounted(() => {
    if (useUserStore().token) router.push('/')
    else getCaptcha()
  })

  async function getCaptcha() {
    const res = await userAuthService()
    captchaUUID.value = res.data.data.uuid
    captchaSrc.value = res.data.data.image
  }

  async function tapLogin() {
    await form.value.validate()
    formModel.value.uuid = captchaUUID.value
    const res = await userLoginService(formModel.value)
    if (res.data.code !== 20000) {
      ElMessage.error('账号或密码错误')
    } else {
      useUserStore().setUserToken(res.data.data.token)
      ElMessage.success('登录成功')
      router.push('/')
    }
  }


</script>

<style scoped lang="scss">
  .login-page {
    height: 100vh;
    background-color: #FFFFFF;

    .bg {
      background: url('@/assets/login_bg.png') center;
      border-radius: 0 20px 20px 0;
    }

    .form {
      display: flex;
      flex-direction: column;
      justify-content: center;
    }

    .wrap_captcha {
      display: flex;

      .img_captcha {
        margin-left: 25px;
      }
    }

    .btn_remember {
      width: 100%;
    }
  }
</style>
