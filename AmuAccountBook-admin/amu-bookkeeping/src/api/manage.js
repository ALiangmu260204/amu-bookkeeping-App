import request from '@/utils/request'

export const customerListService = (page, size) =>
  request.get(`/api/customerlist?page=${page}&size=${size}`)

export const customerUpdateInfoService = ({ userId, openid }) =>
  request.post('/api/update/customerinfo', { userId, openid })

export const customerDeleteService = ({ userId }) =>
  request.delete(`/api/delete/customerinfo/${userId}`)

export const categoryListService = ({ page, size, type, system }) =>
  request.get(`/api/categorylist?page=${page}&size=${size}&type=${type}&system=${system}`)

export const addCategoryService = (data) =>
  request.post('/api/add/category', data)

export const updateCategoryService = (data) =>
  request.post('/api/update/category', data)

export const deleteCategoryService = ({ userId, name, isType }) =>
  request.delete(`/api/delete/category?userId=${userId}&name=${name}&isType=${isType}`)
