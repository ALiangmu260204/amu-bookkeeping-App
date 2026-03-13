<template>
  <el-card class="page-container">
    <template #header>
      <div class="header">
        <span>{{ title }}</span>
        <div>
          <slot name="extra"></slot>
        </div>
      </div>
    </template>
    <slot></slot>
  </el-card>
</template>

<script setup>
  defineProps({
    title: { require: true, type: String }
  })
</script>

<style scoped lang="scss">
  .page-container {
    min-height: 100%;
    box-sizing: border-box;

    .header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
  }
</style>
