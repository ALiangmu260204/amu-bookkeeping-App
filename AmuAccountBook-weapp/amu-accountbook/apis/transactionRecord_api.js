import { get, post, put, del } from '@/utils/request'

export const transactionRecordApi = {
    // 新增交易记录
    addRecord: (data) => post('/user/transaction-record/add', data),

    // 修改交易记录
    updateRecord: (data) => put('/user/transaction-record/update', data),

    // 删除交易记录
    deleteRecord: (id) => del(`/user/transaction-record/one/${id}`),

    // 加载更多交易记录
    loadMoreRecords: (page, pageSize) => get(`/user/transaction-record/list`, { page: page, pageSize: pageSize }),

    // 清理所有交易记录
    clearAllRecord: (id) => del(`/user/transaction-record/all/${id}`),

    // 获取指定年月份交易记录
    getMonthRecord: (year, month) => get('/user/transaction-record/stats/month', {year, month}),

    // 获取指定年份支出和收入金额统计
    getMonthlyStats: (year) => get(`/user/transaction-record/stats/${year}`)
}