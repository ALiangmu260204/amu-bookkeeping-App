import { post } from '@/utils/request'

export const categoryApi = {
    // 新增分类
    addCategory: (data) => post('/user/category/add', data),

    // 删除分类
    deleteCategory: (data) => post('/user/category/delete', data)
}