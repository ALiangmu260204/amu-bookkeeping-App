<template>
  <page-container title="用户列表">
    <template #extra>
      <el-button type="primary" @click="tapRefresh">刷新</el-button>
      <el-button type="danger" disabled>删除所有用户</el-button>
    </template>

    <el-table v-loading="loading" :data="customerList" style="width: 100%">
      <el-table-column
        label="序号"
        type="index"
        :index="(index) => (pageNum - 1) * pageSize + index + 1"
        width="100"
      />
      <el-table-column label="用户ID" prop="userId" />
      <el-table-column label="微信标识" prop="openid" :show-overflow-tooltip="true" />
      <el-table-column
        label="创建时间"
        prop="createAT"
        :formatter="(row) => formDateString(row.createAT)"
      />
      <el-table-column label="操作">
        <template #default="{ $index, row }">
          <el-text type="info" v-if="row.userId === 0">系统账号不支持任何操作</el-text>
          <el-button
            v-if="row.userId !== 0"
            type="primary"
            :icon="Edit"
            @click="tapEditBtn($index, row)"
          />
          <el-button
            v-if="row.userId !== 0"
            type="danger"
            :icon="Delete"
            @click="tapDeleteBtn($index, row)"
          />
        </template>
      </el-table-column>

      <template #empty>
        <el-empty description="没有数据..." />
      </template>
    </el-table>

    <el-pagination
      v-model:current-page="pageNum"
      v-model:page-size="pageSize"
      size="default"
      background
      :total="total"
      :page-sizes="[10, 20, 30, 40, 50]"
      layout="total, sizes, prev, pager, next, jumper"
      @change="handlePageChange"
      style="margin-top: 20px; justify-content: center;"
    />

    <EditDialog ref="dialog" @success="onSuccess" />
  </page-container>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import dayjs from 'dayjs'
import { Edit, Delete } from '@element-plus/icons-vue'
import { customerListService } from '@/api/manage'
import EditDialog from './components/EditDialog.vue'

const loading = ref(false)
const customerList = ref([])
const dialog = ref()

const pageNum = ref(1)
const pageSize = ref(20)
const total = ref(0)

onMounted(() => getCustomerList(pageNum.value, pageSize.value))

async function getCustomerList(p, s) {
  loading.value = true
  const res = await customerListService(p, s)
  customerList.value = res.data.data.userList
  total.value = res.data.data.total
  loading.value = false
}

function formDateString(date) {
  return date ? dayjs(date).format('YYYY-MM-DD HH:mm:ss') : '-'
}

function tapRefresh() {
  pageNum.value = 1
  pageSize.value = 20
  getCustomerList(pageNum.value, pageSize.value)
}

function tapEditBtn(index, row) {
  dialog.value.open(row, '修改用户')
}

function tapDeleteBtn(index, row) {
  dialog.value.open(row, '删除用户')
}

const handlePageChange = (p, s) => {
  getCustomerList(p, s)
}

function onSuccess() {
  getCustomerList(pageNum.value, pageSize.value)
}
</script>

<style scoped lang="scss"></style>
