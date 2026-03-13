<template>
  <page-container title="分类管理">
    <template #extra>
      <el-button type="primary" @click="tapAddCate">添加分类</el-button>
    </template>

    <el-form inline style="margin-left: 10px;">
      <el-form-item label="类别：">
        <el-select v-model="params.type" style="width: 100px">
          <el-option label="全部" :value="2"></el-option>
          <el-option label="支出" value="0"></el-option>
          <el-option label="收入" value="1"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="分类来源：">
        <el-select v-model="params.system" style="width: 120px">
          <el-option label="全部" :value="2"></el-option>
          <el-option label="系统预置" value="1"></el-option>
          <el-option label="用户定义" value="0"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="tapSearch">搜索</el-button>
        <el-button @click="tapReset">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="categoryList" v-loading="loading">
      <el-table-column
        label="序号"
        type="index"
        :index="(index) => (params.page - 1) * params.size + index + 1"
        width="100"
      />
      <el-table-column label="用户ID" prop="userId" width="100"></el-table-column>
      <el-table-column label="分类名称" prop="name" #default="{ $index, row }">
        <el-link v-if="row.userId === 0" type="primary" :underline="false" width="100">{{ row.name }}</el-link>
      </el-table-column>
      <el-table-column label="图标名称" prop="icon"></el-table-column>
      <el-table-column label="支或收" prop="isType" #default="{ $index, row }">
        <el-text v-if="row.isType === 0">支出</el-text>
        <el-text v-else>收入</el-text>
      </el-table-column>
      <el-table-column label="系统预置" prop="isSystem"></el-table-column>
      <el-table-column label="操作" #default="{ $index, row }">
        <el-button
          type="primary"
          :icon="Edit"
          @click="tapEditCategory(row)"
        />
        <el-button
          type="danger"
          :icon="Delete"
          @click="tapDeleteCategory(row)"
          Slide
        />
      </el-table-column>
    </el-table>

    <el-pagination
      v-model:current-page="params.page"
      v-model:page-size="params.size"
      background
      :total="total"
      :page-sizes="[10, 20, 30, 40, 50]"
      layout="total, sizes, prev, pager, next, jumper"
      @change="handlePageChange"
      style="margin-top: 20px; justify-content: center;"
    />

    <EditDrawer
      ref="drawerEditRef"
      :title="drawerTitle"
      @success="onSuccess"
    />

    <el-dialog
      v-model="dialogVisible"
      title="Warning"
      width="500"
      center
    >
      <el-text style="display: flex; justify-content: center;" v-if="dialogContent.userId === 0">
        你确定要删除系统预置 {{ dialogContent.name }} 分类吗？<br>
      </el-text>
      <el-text style="display: flex; justify-content: center;" v-else>
        你确定要删除用户ID{{ dialogContent.userId }}定义的 {{ dialogContent.name }} 分类吗？<br>
      </el-text>
      <el-text style="display: flex; justify-content: center;" type="danger">此操作无法撤销</el-text>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="onConfirmBtn">确认</el-button>
      </template>
    </el-dialog>
  </page-container>
</template>

<script setup>
  import { onMounted, ref } from 'vue'
  import { Edit, Delete } from '@element-plus/icons-vue'
  import { ElMessage } from 'element-plus'
  import { categoryListService } from '@/api/manage'
  import EditDrawer from './components/EditDrawer.vue'
  import { deleteCategoryService } from '@/api/manage'

  const params = ref({ page: 1, size: 20, type: 2, system: 2 })
  const categoryList = ref([])
  const total = ref(null)
  const loading = ref(false)
  const drawerEditRef = ref()
  const drawerTitle = ref()
  const dialogVisible = ref(false)
  const dialogContent = ref()

  onMounted(() => getCategoryList(params.value))

  async function getCategoryList(datas) {
    loading.value = true
    const res = await categoryListService(datas)
    categoryList.value = res.data.data.categoryList
    total.value = res.data.data.total
    loading.value = false
  }

  function tapAddCate() {
    drawerTitle.value = '添加分类'
    drawerEditRef.value?.open()
  }

  function tapSearch() {
    params.value.page = 1
    getCategoryList(params.value)
  }

  function tapReset() {
    params.value.page = 1
    params.value.type = 2
    params.value.system = 2
    getCategoryList(params.value)
  }

  function tapEditCategory(row) {
    drawerTitle.value = '编辑分类'
    drawerEditRef.value?.open(row)
  }

  function tapDeleteCategory({ userId, name, isType }) {
    dialogVisible.value = true
    dialogContent.value = { userId, name, isType }
  }

  async function onConfirmBtn() {
    const res = await deleteCategoryService(dialogContent.value)
    if (res.data.code === 20000) {
      getCategoryList(params.value)
      ElMessage.success(`删除分类：${dialogContent.value.name} 成功`)
    }
    dialogVisible.value = false
  }

  const handlePageChange = (p, s) => {
    params.value.page = p
    params.value.size = s
    getCategoryList(params.value)
  }

  function onSuccess() { getCategoryList(params.value) }
</script>

<style scoped lang="scss"></style>
