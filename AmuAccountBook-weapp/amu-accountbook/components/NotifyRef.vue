<template>
    <up-notify :ref="(value) => notifyInstance = value" />
</template>

<script setup>
    import { ref } from 'vue'

    const notifyInstance = ref(null)

    // 定义组件自身接口实现对应效果
    defineExpose({
        primary : (message) => notifyInstance.value?.primary(message),
        success: (message) => notifyInstance.value?.success(message),
        error: (message) => notifyInstance.value?.error(message),
        warning: (message) => notifyInstance.value?.warning(message)
    })
</script>