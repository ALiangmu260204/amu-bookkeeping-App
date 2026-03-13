import request from '@/utils/request'

export const userAuthService = (() => {
  return request.get('/api/captcha')
})

export const userLoginService = ({ username, password, captcha, uuid, rememberMe }) => {
  return request.post('/api/login', { username, password, captcha, uuid, rememberMe })
}

export const userGetInfoService = (() => {
  return request.get('/api/admininfo')
})
