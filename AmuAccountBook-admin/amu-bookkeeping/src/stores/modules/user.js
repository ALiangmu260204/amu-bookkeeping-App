import { defineStore } from 'pinia'
import { ref } from 'vue'
import { userGetInfoService } from '@/api/user'
import { useRouter } from 'vue-router'

export const useUserStore = defineStore('userStore', () => {
  const token = ref('')
  const info = ref({ name: '', username: '', avatar: '' })

  const setUserToken = (newToken) => {
    token.value = newToken
  }

  const setUserInfo = async () => {
    const router = useRouter()
    try {
      const res = await userGetInfoService()
      info.value = res.data.data
    } catch (err) {
      console.log(err)
      logout()
      router.push('/login')
    }
  }

  const logout = () => {
    token.value = ''
    info.value = { name: '', username: '', avatar: '' }
    localStorage.removeItem('userStore')
  }

  return {
    token,
    setUserToken,
    info,
    setUserInfo,
    logout
  }
}, {
  persist: true
})
