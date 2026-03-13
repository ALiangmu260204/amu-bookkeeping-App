import { post } from '@/utils/request'

export const userApi = {
    // 用户登录
    login: (js_code) => post('/user/login', { code: js_code })
}