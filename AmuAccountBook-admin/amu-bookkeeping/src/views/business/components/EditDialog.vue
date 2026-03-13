<template>
  <el-dialog v-model="dialogVisible" :title="title" width="30%">
    <el-form ref="form" :model="formModel" :rules="rules">
      <el-form-item prop="userId" label="用户ID" v-if="title === '修改用户'">
        <el-input v-model="formModel.userId" placeholder="请输入分类名称" />
      </el-form-item>
      <el-form-item v-else>
        <el-text size="large">确定删除用户ID：{{ formModel.userId }} 吗？</el-text>
        <el-text type="danger" size="default">此操作无法撤回</el-text>
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="tapConfirm">确认</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { customerUpdateInfoService, customerDeleteService } from '@/api/manage'

const dialogVisible = ref(false)
const title = ref('')
const form = ref()
const formModel = ref({ userId: null, openid: '' })
const rules = {
  userId: [
    { required: true, message: '请输入用户ID', trigger: 'blur' },
    { pattern: /^\d{1,11}$/, message: '用户ID必须是11位内数字', trigger: 'blur' },
  ],
}
const emit = defineEmits(['success'])

const open = (row, t) => {
  dialogVisible.value = true
  title.value = t
  formModel.value.userId = row.userId
  formModel.value.openid = row.openid
}

async function tapConfirm() {
  let res
  if (title.value === '修改用户') {
    await form.value.validate()
    res = await customerUpdateInfoService(formModel.value)
  } else {
    res = await customerDeleteService(formModel.value)
  }
  if (res.data.code === 20000) {
    ElMessage.success(`${title.value}成功`)
    emit('success')
  } else ElMessage.error(`${title.value}失败`)

  dialogVisible.value = false
}

defineExpose({ open })
</script>

<style scoped lang="scss"></style>
