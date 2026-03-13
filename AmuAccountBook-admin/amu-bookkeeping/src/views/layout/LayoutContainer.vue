<template>
  <el-container class="app-wrapper">
    <el-aside :class="['app-sidebar', { 'is-collapse': isCollapse }]">
      <div class="sidebar-logo">
        <div class="logo-container">
          <img src="@/assets/logo_bg_m.png" class="logo-img" />
          <h1 v-show="!isCollapse" class="logo-title">阿木简易记账</h1>
        </div>
      </div>

      <el-scrollbar>
        <el-menu
          class="sidebar-menu"
          :default-active="$route.path"
          :collapse="isCollapse"
          :collapse-transition="true"
          router
        >
          <el-sub-menu index="/">
            <template #title>
              <el-icon><Monitor /></el-icon>
              <span>控制台</span>
            </template>
            <el-menu-item index="/data/board">数据看板</el-menu-item>
            <el-menu-item index="/data/analysis">系统分析</el-menu-item>
          </el-sub-menu>

          <el-menu-item index="/data/category">
            <el-icon><Coin /></el-icon>
            <template #title>分类管理</template>
          </el-menu-item>

          <el-menu-item index="/data/user">
            <el-icon><User /></el-icon>
            <template #title>用户管理</template>
          </el-menu-item>
        </el-menu>
      </el-scrollbar>
    </el-aside>

    <el-container class="main-container">
      <el-header class="app-header">
        <div class="header-left">
          <div class="collapse-trigger" @click="isCollapse = !isCollapse" >
            <el-icon><Expand v-show="isCollapse" /></el-icon>
            <el-icon><Fold v-show="!isCollapse" /></el-icon>
          </div>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>数据看板</el-breadcrumb-item>
          </el-breadcrumb>
        </div>

        <div class="header-right">
          <!-- <el-tooltip content="全屏" placement="bottom">
            <el-icon class="header-icon"><FullScreen /></el-icon>
          </el-tooltip> -->

          <el-dropdown trigger="hover" @command="handCommand">
            <div class="user-profile">
              <el-avatar :size="42" :src="`data:image/png;base64,${useUserStore().info.avatar}`" />
              <span class="user-name">{{ useUserStore().info.name }}</span>
              <el-icon><CaretBottom /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <!-- <el-dropdown-item command="profile">个人设置</el-dropdown-item> -->
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="app-main">
        <div class="main-content">
          <router-view v-slot="{ Component }">
            <transition name="fade-transform" mode="out-in" v-if="Component">
              <component :is="Component" />
            </transition>
            <el-empty v-else description="等待数据载入..." />
          </router-view>
        </div>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
  import { onMounted, ref } from 'vue'
  import { Monitor, User, FullScreen, CaretBottom, Expand, Fold, Coin } from '@element-plus/icons-vue'
  import { ElMessageBox } from 'element-plus'
  import { useUserStore } from '@/stores'
  import { useRouter } from 'vue-router'

  const isCollapse = ref(false)
  const router = useRouter()

  onMounted(() => {
    useUserStore().setUserInfo()
  })

  const handCommand = async (key) => {
    if (key === 'logout') {
      await ElMessageBox.confirm('是否退出登录', '温馨提示', {
        type: 'warning',
        confirmButtonText: '确认',
        cancelButtonText: '取消'
      })
      useUserStore().logout()
      router.push('/login')
    } else {
      router.push(`/${key}`)
    }
  }
</script>

<style scoped lang="scss">
  $sidebar-bg: #FFFFFF;
  $header-height: 50px;
  $tags-height: 34px;

  .app-wrapper {
    height: 100vh;
    width: 100vw;
    display: flex;

    .app-sidebar {
      background-color: $sidebar-bg;
      transition: width 0.3s cubic-bezier(0.2, 0, 0, 1);
      width: 210px;
      height: 100%;
      overflow: hidden;
      display: flex;
      flex-direction: column;
      box-shadow: 2px 0 8px rgba(0, 0, 0, 0.15);

      &.is-collapse { width: 64px; }

      .sidebar-logo {
        height: 64px;
        display: flex;
        align-items: center;
        padding: 0 16px;
        overflow: hidden;
        background: #002140;

        .logo-container {
          display: flex;
          align-items: center;
          width: 100%;

          .logo-img {
            width: 32px;
            height: 32px;
            object-fit: contain;
            flex-shrink: 0;
          }

          .logo-title {
            color: #fff;
            margin-left: 12px;
            font-size: 16px;
            font-weight: 600;
            white-space: nowrap;
            letter-spacing: 0.5px;
          }
        }
      }

      .el-menu {
        border-right: none;
        width: 100% !important;
      }
    }

    .main-container {
      background-color: #f0f2f5;
      flex: 1;
      display: flex;
      flex-direction: column;
    }

    .app-header {
      height: $header-height;
      background: #fff;
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 0 15px;
      box-shadow: 0 1px 4px rgba(0,21,41,0.08);
      z-index: 9;

      .header-left, .header-right {
        display: flex;
        align-items: center;
      }

      .collapse-trigger {
        font-size: 20px;
        cursor: pointer;
        margin-right: 15px;
        &:hover { color: #409eff; }
      }

      .header-icon {
        margin-right: 15px;
        cursor: pointer;
        font-size: 18px;
      }

      .user-profile {
        display: flex;
        align-items: center;
        cursor: pointer;
        padding: 0 8px;
        margin-right: 10px;
        .user-name {
          margin: 0 8px;
          font-size: 18px;
          color: #333;
        }
      }
    }

    .app-main {
      padding: 16px;
      background: #f0f2f5;

      .main-content {
        background: #fff;
        border-radius: 4px;
        min-height: 100%;
        padding: 20px;
      }
    }
  }

  :deep(.el-menu--collapse) {
    .el-sub-menu__title span { display: none; }
    .el-sub-menu__icon-arrow { display: none; }
  }
</style>
