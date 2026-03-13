import axios from 'axios'
import { useUserStore } from '@/stores'
import { ElMessage } from 'element-plus'
import router from '@/router'

const baseURL = ''

const instance = axios.create({
  baseURL,
  timeout: 5000
})

instance.interceptors.request.use(
  (config) => {
    const useStore = useUserStore()
    if (useStore.token) {
      config.headers.accessToken = useStore.token
    }

    return config
  },
  (err) => {
    Promise.reject(err)
  }
)

instance.interceptors.response.use(
  (res) => {
    if (res.data.code === 20000) return res

    if (res.data.code === 40100) {
      const useStore = useUserStore()
      useStore.logout()
      router.push('/login')
    }

    ElMessage.error(res.data.message || '服务异常')
    return Promise.reject(res.data)
  },
  (err) => {
    if (err.response?.status === 401) {
      useStore.logout()
      router.push('/login')
    }

    ElMessage.error(err.response.data.message || '服务异常')
    return Promise.reject(err)
  }
)

export default instance
export { baseURL }
