<template>
	<view class="my-container">
		<!-- 1. 用户信息头部 -->
		<view class="user-header">
			<view class="avatar-box">
				<image class="avatar" src="../../static/avatar.png" mode="aspectFill"></image>
			</view>
			<view class="info">
				<text class="username">记账小能手</text>
				<text class="user-id">ID: {{ useUserStore().userId }}</text>
			</view>
		</view>

		<!-- 2. 成就统计卡片 -->
		<view class="stat-card">
			<!-- <view class="stat-item">
				<text class="num">365</text>
				<text class="label">记账天数</text>
			</view> -->
			<view class="stat-item">
				<text class="num">{{ useUserStore().recordTotal }}</text>
				<text class="label">记账笔数</text>
			</view>
		</view>

		<!-- 3. 功能列表 -->
		<!-- <view class="menu-list">
			<view class="menu-item" @click="handleTodo">
				<view class="left">
					<up-icon name="setting" color="#3c9cff" size="20"></up-icon>
					<text class="menu-name">通用设置</text>
				</view>
				<up-icon name="arrow-right" color="#ccc" size="14"></up-icon>
			</view>

			<view class="menu-item" @click="handleTodo">
				<view class="left">
					<up-icon name="download" color="#2ecc71" size="20"></up-icon>
					<text class="menu-name">导出账单(Excel)</text>
				</view>
				<up-icon name="arrow-right" color="#ccc" size="14"></up-icon>
			</view>

			<view class="menu-item" @click="handleTodo">
				<view class="left">
					<up-icon name="question-circle" color="#ff9900" size="20"></up-icon>
					<text class="menu-name">帮助与反馈</text>
				</view>
				<up-icon name="arrow-right" color="#ccc" size="14"></up-icon>
			</view>
		</view> -->

		<!-- 4. 危险操作区域：清除流水 -->
		<view class="danger-zone">
			<view class="menu-item no-border" @click="showClearModal = true">
				<view class="left">
					<up-icon name="trash-fill" color="#ff5252" size="20"></up-icon>
					<text class="menu-name danger">清空所有流水</text>
				</view>
				<text class="tip">不可恢复</text>
			</view>
		</view>

		<!-- 清空确认弹窗 -->
		<up-modal :show="showClearModal" title="确定要清空吗？" content="此操作将永久删除您所有的账单流水记录（保留分类设置）。该操作不可撤销，请谨慎操作。"
			showCancelButton confirmColor="#ff5252" confirmText="确定清空" @confirm="confirmClearData"
			@cancel="showClearModal = false"></up-modal>

		<!-- 底部信息 -->
		<view class="footer-info">
			<text>简单记账 · 精致生活</text>
		</view>
	</view>
</template>

<script setup>
	import { ref } from 'vue'
	import { useUserStore } from '@/store/user'
	import { transactionRecordApi } from '@/apis/transactionRecord_api'
	import { useRecordStore } from '@/store/transactionRecord'
 
	const showClearModal = ref(false)

	// 删除所有的交易记录操作
	const confirmClearData = async () => {
		showClearModal.value = false;

		const res = await transactionRecordApi.clearAllRecord(useUserStore().userId)
		if (res.code === 20000) {
			await useRecordStore().clearRecord()
			await useUserStore().setRecordTotal(0)
			uni.showToast({
				title: '已清空所有流水',
				icon: 'success',
				duration: 2000
			})
		} else {
			uni.showToast({
				title: '清理失败',
				icon: 'none'
			})
		}
	}
</script>

<style lang="scss" scoped>
	.my-container {
		padding: 40rpx 30rpx;
		background-color: #f8f9fb;
		min-height: 100vh;
	}

	.user-header {
		display: flex;
		align-items: center;
		margin-bottom: 50rpx;
		padding: 20rpx 10rpx;

		.avatar-box {
			width: 120rpx;
			height: 120rpx;
			border-radius: 60rpx;
			overflow: hidden;
			border: 4rpx solid #fff;
			box-shadow: 0 10rpx 20rpx rgba(0, 0, 0, 0.05);
			margin-right: 30rpx;

			.avatar {
				width: 100%;
				height: 100%;
			}
		}

		.info {
			.username {
				font-size: 36rpx;
				font-weight: bold;
				color: #333;
				display: block;
				margin-bottom: 8rpx;
			}

			.user-id {
				font-size: 24rpx;
				color: #999;
			}
		}
	}

	.stat-card {
		background: #fff;
		border-radius: 32rpx;
		padding: 40rpx 0;
		display: flex;
		margin-bottom: 40rpx;
		box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.02);

		.stat-item {
			flex: 1;
			display: flex;
			flex-direction: column;
			align-items: center;
			position: relative;

			&:not(:last-child)::after {
				content: '';
				position: absolute;
				right: 0;
				top: 20%;
				height: 60%;
				width: 1rpx;
				background: #f0f0f0;
			}

			.num {
				font-size: 34rpx;
				font-weight: 800;
				color: #333;
				margin-bottom: 10rpx;
			}

			.label {
				font-size: 22rpx;
				color: #999;
			}
		}
	}

	.menu-list,
	.danger-zone {
		background: #fff;
		border-radius: 32rpx;
		padding: 10rpx 30rpx;
		margin-bottom: 30rpx;
		box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.02);

		.menu-item {
			display: flex;
			justify-content: space-between;
			align-items: center;
			padding: 34rpx 0;

			&:not(:last-child) {
				border-bottom: 1rpx solid #fcfcfc;
			}

			&.no-border {
				border-bottom: none;
			}

			.left {
				display: flex;
				align-items: center;

				.menu-name {
					font-size: 28rpx;
					color: #333;
					margin-left: 20rpx;
				}

				.danger {
					color: #ff5252;
					font-weight: bold;
				}
			}

			.tip {
				font-size: 24rpx;
				color: #ff8a8a;
			}
		}
	}

	.footer-info {
		text-align: center;
		margin-top: 95%;

		text {
			font-size: 24rpx;
			color: #d0d2d6;
		}
	}
</style>