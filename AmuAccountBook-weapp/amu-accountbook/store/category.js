import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useCategoryStore = defineStore('category', () => {
    const categoryList = ref([])

    // 设置分类列表
    function setCategoryList(category) {
        categoryList.value = [...category]
    }

    // 新增分类到列表
    function addCategory(category) {
        categoryList.value.unshift(category)
    }

    // 删除列表里指定分类
    function deleteCategory(categoryName) {
        categoryList.value = categoryList.value.filter(cat => cat.name !== categoryName)
    }

    return {
        categoryList,
        setCategoryList,
        addCategory,
        deleteCategory
    }
}, {
    // 持久化插件-微信小程序写法
    persist: {
        key: 'categoryInfo',
        paths: ['categoryList'],
        storage: {
            setItem(key, value) { uni.setStorageSync(key, value) },
            getItem(key) { return uni.getStorageSync(key) },
            removeItem(key) { uni.removeStorageSync(key) }
        }
    }
})