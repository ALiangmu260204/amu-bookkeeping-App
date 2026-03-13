import { userApi } from '@/apis/user_api'
import { useUserStore } from '@/store/user'
import { useCategoryStore } from '@/store/category'
import { useRecordStore } from '@/store/transactionRecord'

// 自定义静默登录
export function silentLogin() {
    uni.removeStorageSync('userInfo')
    uni.removeStorageSync('transactionRecordInfo')
    uni.removeStorageSync('categoryInfo')

    return new Promise((resolve, reject) => {
        // 执行微信登录流程
        uni.login({
            provider: 'weixin',
            success: async (loginRes) => {
                try {
                    const { data } = await userApi.login(loginRes.code)
                    const { userId, accessToken, recordTotal, categoryList, transactionRecordList } = data
                    useUserStore().setUserInfo(String(userId), accessToken)
                    useUserStore().setRecordTotal(recordTotal)
                    useCategoryStore().setCategoryList(categoryList)
                    useRecordStore().setRecordList(transactionRecordList)
                    resolve(data)
                } catch (err) {
                    reject(err)
                }
            },
            fail: () => reject(new Error('登录失败，请检查网络或重新进入小程序'))
        })
    })
}