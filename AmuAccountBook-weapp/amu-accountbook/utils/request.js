import Constants from '@/common/constants'
import { useUserStore } from '@/store/user'
import { silentLogin } from '@/utils/silentLogin'
import { toast } from '@/common/useToast'

const TOKEN_NAME = 'accessToken'
const BASE_URL = Constants.BASE_URL

// 自定义封装 request
function request(options = {}) {
    const { url, method = 'GET', params = {}, data = {}} = options

    let fullUrl = `${BASE_URL}${url}`

    if (method.toUpperCase() === 'GET' && Object.keys(params).length > 0) {
        // 兼容微信小程序：手动构建查询字符串
        const queryString = Object.entries(params)
            .map(([k, v]) => `${encodeURIComponent(k)}=${encodeURIComponent(v)}`)
            .join('&')
        
        fullUrl += (fullUrl.includes('?') ? '&' : '?') + queryString
    }

    let token = useUserStore().accessToken

    return new Promise((resolve, reject) => {
        uni.request({
            url: fullUrl,
            method,
            data: ['POST', 'PUT'].includes(method.toUpperCase()) ? data : undefined,
            header: {
                [TOKEN_NAME]: token || '',
                'Content-Type': 'application/json'
            },
            success: (res) => {
                const { statusCode, data: resData } = res
                if (statusCode >= 200 && statusCode < 300 && resData.code === 20000) {
                    resolve(resData)
                    return
                } else if (statusCode === 401) {
                    silentLogin().then((res) => {
                        uni.removeStorageSync('userInfo')
                        res.notify = "身份凭证过期，请重新进入小程序"
                        resolve(res)
                    }).catch(() => {
                        uni.removeStorageSync('userInfo')
                        reject(new Error('登录已失效，请重新进入小程序'))
                    })
                    return
                }
                toast(resData.message)
                reject(new Error(resData.message || '请求失败'))
            }
        })
    })
}

// 请求方法
export const get = (url, params) => {
    return request({ url, method: 'GET', params })
}

export const post = (url, data) => {
    return request({ url, method: 'POST', data })
}

export const put = (url, data) => {
    return request({ url, method: 'PUT', data })
}

export const del = (url, params) => {
    return request({ url, method: 'DELETE', params })
}