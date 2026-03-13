<template>
	<view class="detail-container">
		<view class="header-card">
			<view class="cat-icon">{{ data.categoryName.substring(0, 1) }}</view>
			<text class="cat-name">{{ data.categoryName }}</text>
			<text class="amount" :class="data.isType">
				{{ data.isType === 0 ? '-' : '+' }}{{ data.amount.toFixed(2) }}
			</text>
		</view>

		<view class="info-list">
			<view class="info-item">
				<text class="label">记录类型</text>
				<text class="val">{{ data.isType === 0 ? '支出' : '收入' }}</text>
			</view>
			<view class="info-item">
				<text class="label">记录时间</text>
				<text class="val">{{ formatTime(data.transactionTime) }}</text>
			</view>
			<view class="info-item">
				<text class="label">备注信息</text>
				<text class="val val-remark">{{ data.remark || '' }}</text>
			</view>
		</view>

		<view class="footer-btns">
			<view class="btn edit" @click="handleEdit">修改记录</view>
			<view class="btn delete" @click="handleDelete">删除记录</view>
		</view>
	</view>
</template>

<script setup>
	import { ref } from 'vue'
	import { onLoad } from '@dcloudio/uni-app'

	const data = ref({
		id: 0,
		categoryName: '',
		amount: 0,
		isType: 0,
		transactionTime: Date.now(),
		remark: ''
	})

	// 时间格式
	function formatTime(timestamp) {
		const date = new Date(timestamp)
		const y = date.getFullYear()
		const m = String(date.getMonth() + 1).padStart(2, '0')
		const d = String(date.getDate()).padStart(2, '0')
		const h = String(date.getHours()).padStart(2, '0')
		const min = String(date.getMinutes()).padStart(2, '0')
		const s = String(date.getSeconds()).padStart(2, '0')
		return `${y}-${m}-${d} ${h}:${min}:${s}`
	}

	// 挂载时读取数据
	onLoad((options) => {
		if (options.data) {
			data.value = JSON.parse(decodeURIComponent(options.data))
		}
	})

	// 处理删除记录操作
	const handleDelete = () => {
		uni.showModal({
			title: '提示',
			content: '确定要删除这条记录吗？',
			confirmColor: '#ff5252',
			success: (res) => {
				if (res.confirm) {
					uni.$emit('DELETE_RECORD', data.value.id)
					uni.navigateBack()
				}
			}
		})
	}

	// 通知首页打开更新弹窗
	const handleEdit = () => {
		uni.$emit('UPDATE_RECORD', data.value)
		uni.navigateBack()
	}
</script>

<style lang="scss" scoped>
	.detail-container {
		padding: 40rpx;

		.header-card {
			display: flex;
			flex-direction: column;
			align-items: center;
			padding: 60rpx 0;

			.cat-icon {
				width: 120rpx;
				height: 120rpx;
				background: #3c9cff;
				color: #fff;
				border-radius: 40rpx;
				display: flex;
				align-items: center;
				justify-content: center;
				font-size: 50rpx;
				margin-bottom: 20rpx;
			}

			.cat-name {
				font-size: 32rpx;
				color: #333;
				margin-bottom: 20rpx;
			}

			.amount {
				font-size: 60rpx;
				font-weight: bold;

				&.expense {
					color: #333;
				}

				&.income {
					color: #2ecc71;
				}
			}
		}

		.info-list {
			background: #fff;
			border-radius: 24rpx;
			padding: 20rpx 30rpx;

			.info-item {
				display: flex;
				justify-content: space-between;
				padding: 30rpx 0;
				border-bottom: 1rpx solid #f8f8f8;

				.label {
					color: #999;
					font-size: 28rpx;
					min-width: 80rpx;
				}

				.val {
					color: #333;
					font-size: 28rpx;
				}

				.val-remark {
					text-align: right;
					width: 450rpx;
					font-weight: 500;
				}
			}
		}

		.footer-btns {
			position: fixed;
			bottom: 80rpx;
			left: 40rpx;
			right: 40rpx;
			display: flex;
			gap: 30rpx;

			.btn {
				flex: 1;
				height: 90rpx;
				border-radius: 50rpx;
				display: flex;
				align-items: center;
				justify-content: center;
				font-size: 30rpx;

				&.edit {
					background: #f0f7ff;
					color: #3c9cff;
				}

				&.delete {
					background: #fff1f1;
					color: #ff5252;
				}
			}
		}
	}
</style>