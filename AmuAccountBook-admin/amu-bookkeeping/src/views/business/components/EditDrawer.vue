<template>
  <el-drawer :title="title" v-model="visibleDrawer">
    <el-form ref="formRef" :model="formModel" :rules="rules">
      <el-form-item label="分类名称" prop="name">
        <el-input v-model="formModel.name" placeholder="请输入分类名称" />
      </el-form-item>
      <el-form-item label="分类类别" prop="isType">
        <el-select v-model="formModel.isType">
          <el-option label="支出" :value="0" />
          <el-option label="收入" :value="1" />
        </el-select>
      </el-form-item>
      <el-form-item label="图标上传(目前不开放)">
        <el-upload
          disabled
          :show-file-list="false"
          :auto-upload="false"
          :on-change="onSelectFile"
          class="icon-uploader"
        >
          <img v-if="imgUrl" :src="imgUrl" class="icon" />
          <el-icon v-else class="icon-uploader-icon"><Plus /></el-icon>
        </el-upload>
      </el-form-item>
      <el-form-item>
        <el-button @click="tapCancelBtn">取消</el-button>
        <el-button type="primary" @click="tapConfirmBtn">确认</el-button>
      </el-form-item>
    </el-form>
  </el-drawer>
</template>

<script setup>
  import { ref } from 'vue'
  import { Plus } from '@element-plus/icons-vue'
  import { ElMessage } from 'element-plus'
  import { addCategoryService, updateCategoryService } from '@/api/manage'

  const props = defineProps({ title: { require: true, type: String } })
  const visibleDrawer = ref(false)
  const buttonType = ref()

  const formRef = ref()
  const defaultForm = { userId: 0, name: '', isType: 0 }
  const formModel = ref({ ...defaultForm })
  const rules = {
    name: [
      { required: true, message: '请输入分类名称', trigger: 'blur' },
      { pattern: /^\S/, min: 1, max: 7, message: '长度在 1 到 7个非空字符', trigger: 'blur' }
    ],
    // icon: [
    //   { required: true, message: '请上传图标', trigger: 'change' },
    //   {
    //     validator: (rule, value, callback) => {
    //       const allowedTypes = ['image/jpeg', 'image/png', 'image/svg']
    //       if (!allowedTypes.includes(value.type)) {
    //         callback(new Error('仅支持 JPG、PNG、SVG 格式'))
    //         return
    //       }

    //       if (value.size > (1024 * 64)) {
    //         callback(new Error('图片大小不能超过 64kb'))
    //         return
    //       }

    //       callback()
    //     },
    //     trigger: 'change'
    //   }
    // ]
  }
  const imgUrl = ref('')
  const oldCategoryName = ref('')

  const emit = defineEmits(['success'])

  const open = (row) => {
    visibleDrawer.value = true

    if (row) {
      formModel.value = { ...row }
      oldCategoryName.value = row.name
      buttonType.value = '修改'
    } else {
      formModel.value = { ...defaultForm }
      buttonType.value = '添加'
    }
  }

  const onSelectFile = (uploadFile) => { imgUrl.value = URL.createObjectURL(uploadFile.raw) }

  function tapCancelBtn() { visibleDrawer.value = false }

  async function tapConfirmBtn() {
    await formRef.value.validate()

    // const fd = new FormData()
    // for (let key in formModel.value) {
    //   fd.append(key, formModel.value[key])
    // }

    let res
    if (buttonType.value === '添加') {
      res = await addCategoryService(formModel.value)
    } else {
      formModel.value.newName = formModel.value.name
      formModel.value.name = oldCategoryName.value
      const { userId, name, newName, isType } = formModel.value
      res = await updateCategoryService({ userId, name, newName, isType })
    }

    if (res.data.code === 20000) {
      emit('success')
      ElMessage.success(`${buttonType.value}成功`)
    }
    visibleDrawer.value = false
  }

  defineExpose({ open })
</script>

<style scoped lang="scss">
  .icon-uploader {
    :deep(.el-upload) {
      border: 1px dashed var(--el-border-color);
      border-radius: 6px;
      cursor: pointer;
      position: relative;
      overflow: hidden;
      transition: var(--el-transition-duration-fast);
      width: 178px;
      height: 178px;
      display: flex;
      align-items: center;
      justify-content: center;
    }

    :deep(.el-upload:hover) {
      border-color: var(--el-color-primary);
    }

    .icon-uploader-icon {
      font-size: 28px;
      color: #8c939d;
      width: 178px;
      height: 178px;
    }

    .icon {
      width: 178px;
      height: 178px;
      display: block;
      object-fit: cover;
    }
  }
</style>
