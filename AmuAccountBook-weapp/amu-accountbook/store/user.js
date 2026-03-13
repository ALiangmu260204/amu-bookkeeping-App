import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
    const userId = ref('')
    const accessToken = ref('')
    const recordTotal = ref(NaN)

    // 设置用户基本信息
    function setUserInfo(nweUserId, newAccessToken) {
        userId.value = nweUserId
        accessToken.value = newAccessToken
    }

    // 设置用户总记录数
    function setRecordTotal(total) {
        recordTotal.value = total
    }

    return {
        userId,
        accessToken,
        setUserInfo,
        recordTotal,
        setRecordTotal
    }

}, {
    // 持久化插件-微信小程序写法
    persist: {
        key: 'userInfo',
        paths: ['userId', 'accessToken', 'recordTotal'],
        storage: {
            setItem(key, value) { uni.setStorageSync(key, value) },
            getItem(key) { return uni.getStorageSync(key) },
            removeItem(key) { uni.removeStorageSync(key) }
        }
    }
})