import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useRecordStore = defineStore('record', () => {
    const recordList = ref([])

    // 设置记录列表
    function setRecordList(record) {
        if (Array.isArray(record)) {
            recordList.value = [...record]
        } else if (record && typeof record === 'object' && record.id) {
            const index = recordList.value.findIndex(item => item.id === record.id)
            if (index !== -1) {
                recordList.value[index] = { ...recordList.value[index], ...record }
            } else {
                recordList.value.unshift(record)
            }
        }
    }

    // 追加更多记录（防重复）
    function appendRecordList(newRecords) {
        if (!Array.isArray(newRecords)) return
        
        newRecords.forEach(newRecord => {
            const index = recordList.value.findIndex(item => item.id === newRecord.id)
            if (index !== -1) {
                recordList.value[index] = { ...recordList.value[index], ...newRecord }
            } else {
                recordList.value.push(newRecord)
            }
        })
    }

    // 记录聚合计算
    const aggregatedRecords = computed(() => {
        const groupedByDate = {}
        recordList.value.forEach((item) => {
            
            const dateObj = new Date(item.transactionTime + 'Z')
            if (isNaN(dateObj.getTime())) return
            
            const dateKey = dateObj.toISOString().split('T')[0]

            if (!groupedByDate[dateKey]) {
                groupedByDate[dateKey] = []
            }
            groupedByDate[dateKey].push(item)
        })

        const result = Object.entries(groupedByDate).map(([date, transactions]) => {
            let dailyExpend = 0
            let dailyIncome = 0
  
            transactions.forEach((transaction) => {
                const amount = parseFloat(transaction.amount) || 0
                if (transaction.isType === 0) {
                    dailyExpend += amount
                } else if (transaction.isType === 1) {
                    dailyIncome += amount
                }
            })

            return {
                date,
                dailyExpend: dailyExpend.toFixed(2),
                dailyIncome: dailyIncome.toFixed(2),
                transactions
            }
        })

        return result.sort((a, b) => new Date(b.date) - new Date(a.date))
    })

    // 删除指定记录
    const deleteRecordById = (id) => {
        recordList.value = recordList.value.filter(record => record.id !== id)
    }

    // 删除所有记录
    const clearRecord = () => {
        recordList.value = []
        uni.removeStorageSync('transactionRecordInfo')
    }

    return {
        recordList,
        setRecordList,
        appendRecordList,
        aggregatedRecords,
        deleteRecordById,
        clearRecord
    }
}, {
    // 持久化插件-微信小程序写法
    persist: {
        key: 'transactionRecordInfo',
        paths: ['recordList'],
        storage: {
            setItem(key, value) { uni.setStorageSync(key, value) },
            getItem(key) { return uni.getStorageSync(key) },
            removeItem(key) { uni.removeStorageSync(key) }
        }
    }
})