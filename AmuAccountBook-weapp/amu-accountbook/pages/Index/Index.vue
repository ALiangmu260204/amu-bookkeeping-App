<template>
	<NotifyRef ref="uNotifyRef" />

	<view class="container">
		<!-- 顶部统计看板 -->
		<view class="stats-card">
			<view class="main-stat">
				<text class="label">今日支出</text>
				<view class="value-wrapper">
					<text class="symbol">¥</text>
					<text class="value">{{ totalTodayExpense }}</text>
				</view>
			</view>
			<view class="sub-stats">
				<view class="stat-box">
					<text class="sub-label">本月支出</text>
					<text class="sub-value expense">¥{{ monthlyTotal.expense }}</text>
				</view>
				<view class="divider"></view>
				<view class="stat-box">
					<text class="sub-label">本月收入</text>
					<text class="sub-value income">¥{{ monthlyTotal.income }}</text>
				</view>
			</view>
		</view>

		<!-- 交易明细列表 -->
		<view class="list-section">
			<view class="section-header">
				<text class="section-title">交易明细</text>
				<text class="section-subtitle">最近记录</text>
			</view>

			<view class="day-group" v-for="(items, index) in list" :key="index">
				<view class="day-header">
					<view class="day-info">
						<text class="date-text">{{ formatDate(items.date) }}</text>
						<text class="weekday">{{ getWeekday(items.date) }}</text>
					</view>
					<view class="day-summary">
						<text v-if="items.dailyExpend > 0" class="sum-item">支 {{ items.dailyExpend }}</text>
						<text v-if="items.dailyIncome > 0" class="sum-item">收 {{ items.dailyIncome }}</text>
					</view>
				</view>

				<view class="transaction-card">
					<view class="transaction-item" v-for="(item, idx) in items.transactions" :key="item.id"
						hover-class="item-hover" @click="navToDetail(index, idx)">
						<view class="item-left">
							<view class="icon-avatar" :class="item.isType === 0 ? 'bg-expense' : 'bg-income'">
								{{ (item.categoryName || '其').substring(0, 1) }}
							</view>
							<view class="item-detail">
								<text class="category-name">{{ item.categoryName }}</text>
								<text class="item-time">
									{{ new Date(item.transactionTime).getHours() }}:{{ new Date(item.transactionTime).getMinutes() }}
									<text v-if="item.remark" class="remark-dot">· {{item.remark}}</text>
								</text>
							</view>
						</view>
						<view class="item-right">
							<text class="amount" :class="item.isType === 0 ? 'expense' : 'income'">
								{{ item.isType === 0 ? '-' : '+' }}{{ item.amount }}
							</text>
						</view>
					</view>
				</view>
			</view>

			<!-- 空状态展示 -->
			<view v-if="list.length === 0" class="empty-state">
				<text>还没有记录，点下方按钮开始记账吧</text>
			</view>
		</view>

		<!-- 悬浮按钮 -->
		<view class="fab-btn" @click="openAddPopup"
			:style="{ transform: `translate3d(${fabPos.x}px, ${fabPos.y}px, 0)` }"
			@touchstart="touchStart"
			@touchmove="touchMove"
		>
			<up-icon name="plus" color="#fff" size="26"></up-icon>
		</view>

		<!-- 自定义组件 popup -->
		<AddRecordPopup v-model="showAdd" :initialData="editingRecord" @add="onSaveRecord" />
		<view class="safe-area-bottom"></view>
	</view>
</template>

<script setup>
	import { ref, computed, onMounted, onUnmounted } from 'vue'
	import AddRecordPopup from '@/components/AddRecordPopup.vue'
	import NotifyRef from '@/components/NotifyRef.vue'
	import { useRecordStore } from '@/store/transactionRecord'
	import { useUserStore } from '@/store/user'
	import { transactionRecordApi } from '@/apis/transactionRecord_api'
	import { onReachBottom } from '@dcloudio/uni-app'

	const showAdd = ref(false)
	const uNotifyRef = ref(null)
	const editingRecord = ref(null)
	const page = ref(2)
	const pageSize = 25

	// 挂载事件监听
	onMounted(() => {
		uni.$on('DELETE_RECORD', (id) => removeById(id))
		uni.$on('UPDATE_RECORD', (data) => { editingRecord.value = data, showAdd.value = true})
	})

	// 载入数据
	const list = computed(() => useRecordStore().aggregatedRecords || [])

	// 今日支出计算
	const totalTodayExpense = computed(() => {
		const today = new Date()
		const todayStr = `${today.getFullYear()}-${(today.getMonth() + 1).toString().padStart(2, '0')}-${(today.getDate()).toString().padStart(2, '0')}`
		if (list.value.length > 0 && todayStr === list.value[0].date) {
			return list.value[0]?.dailyExpend || '0.00'
		} else {
			return '0.00'
		}
	})

	// 本月支出/收入计算
	const monthlyTotal = computed(() => {
		let expense = 0
		let income = 0
		const today = new Date()
		const monthStr = `${today.getFullYear()}-${(today.getMonth() + 1).toString().padStart(2, '0')}`
		list.value.filter(item => {
			const itemDate = new Date(item.date)
			const itemMonthStr = `${itemDate.getFullYear()}-${(itemDate.getMonth() + 1).toString().padStart(2, '0')}`
			return itemMonthStr === monthStr
		}).forEach(item => {
			expense += Number(item.dailyExpend) || 0
			income += Number(item.dailyIncome) || 0
		})
		return { expense: expense.toFixed(2), income: income.toFixed(2) }
	})

	// 打开添加/编辑弹窗
	const openAddPopup = () => {
		editingRecord.value = null
		showAdd.value = true
	}

	// 保存记录（新增或编辑）
	const onSaveRecord = (formData) => {
		useRecordStore().setRecordList(formData)
	}

	// 删除记录
	const removeById = async (id) => {
		const res = await transactionRecordApi.deleteRecord(id)
		if (res.code === 20000) {
			useRecordStore().deleteRecordById(id)
			const total = useUserStore().recordTotal - 1
			useUserStore().setRecordTotal(total)
			uNotifyRef.value?.success('删除成功')
		} else {
			uNotifyRef.value?.error('删除失败')
		}
	}

	// 跳转到详情页
	const navToDetail = (index, idx) => {
		const record = list.value[index].transactions[idx];
		uni.navigateTo({
			url: `/pages/Detail/Detail?data=${encodeURIComponent(JSON.stringify({ ...record }))}`
		});
	};

	// 日期格式化
	const formatDate = (s) => {
		// 解决时区问题
		const d = new Date(s + 'Z')
		const today = new Date();
		if (d.toDateString() === today.toDateString()) return '今天';
		if (d.toDateString() === new Date(today.getTime() - 86400000).toDateString()) return '昨天'
		return `${d.getMonth() + 1}月${d.getDate()}日`
	}
	const getWeekday = (s) => ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六'][new Date(s).getDay()]

	// 触底加载更多
	onReachBottom(() => {
		if (useRecordStore().recordList.length === useUserStore().recordTotal) {
			uNotifyRef.value?.primary('没有更多了')
		} else {
			transactionRecordApi.loadMoreRecords(page.value, pageSize).then(res => {
				if (res.code === 20000 && res.data.data.length > 0) {
					useRecordStore().appendRecordList(res.data.data)
					uNotifyRef.value?.success('加载更多成功')
					page.value += 1
				} else {
					uNotifyRef.value?.primary('加载更多失败')
				}
			})
		}
	})

	// 悬浮按钮拖动
	const fabPos = ref({ x: 0, y: 0 })
	let startPos = { x: 0, y: 0 }
	let fabStartPos = { x: 0, y: 0 }

	const touchStart = (e) => {
		startPos = { x: e.touches[0].clientX, y: e.touches[0].clientY }
		fabStartPos = { ...fabPos.value }
	}

	const touchMove = (e) => {
		const dx = e.touches[0].clientX - startPos.x
		const dy = e.touches[0].clientY - startPos.y
		fabPos.value = {
			x: fabStartPos.x + dx,
			y: fabStartPos.y + dy
		}
	}

	// 卸载事件监听
	onUnmounted(() => {
		uni.$off(['DELETE_RECORD', 'UPDATE_RECORD'])
	})
</script>

<style scoped lang="scss">
	$c-primary: #3c9cff;
	$c-expense: #ff5252;
	$c-income: #2ecc71;
	$c-text-main: #2c3e50;
	$c-bg: #f8f9fb;

	.container {
		padding: 20rpx 30rpx;
		// background-color: $c-bg;
	}

	.stats-card {
		background: #fff;
		padding: 40rpx;
		border-radius: 32rpx;
		box-shadow: 0 10rpx 30rpx rgba(0, 0, 0, 0.04);
		margin-bottom: 40rpx;

		.main-stat {
			text-align: center;
			margin-bottom: 30rpx;

			.label {
				font-size: 24rpx;
				color: #999;
			}

			.value-wrapper {
				display: flex;
				justify-content: center;
				align-items: baseline;
				margin-top: 10rpx;

				.symbol {
					font-size: 36rpx;
					font-weight: 600;
					color: $c-text-main;
				}

				.value {
					font-size: 64rpx;
					font-weight: 800;
					color: $c-text-main;
				}
			}
		}

		.sub-stats {
			display: flex;
			padding-top: 30rpx;
			border-top: 1rpx solid #f2f2f2;

			.stat-box {
				flex: 1;
				display: flex;
				flex-direction: column;
				align-items: center;

				.sub-label {
					font-size: 22rpx;
					color: #999;
				}

				.sub-value {
					font-size: 30rpx;
					font-weight: 600;

					&.expense {
						color: $c-expense;
					}

					&.income {
						color: $c-income;
					}
				}
			}

			.divider {
				width: 1rpx;
				height: 40rpx;
				background: #eee;
			}
		}
	}

	.section-header {
		display: flex;
		justify-content: space-between;
		margin-bottom: 24rpx;
		padding: 0 10rpx;

		.section-title {
			font-size: 34rpx;
			font-weight: bold;
		}

		.section-subtitle {
			font-size: 24rpx;
			color: #999;
		}
	}

	.day-group {
		margin-bottom: 30rpx;

		.day-header {
			display: flex;
			justify-content: space-between;
			padding: 0 10rpx 16rpx;

			.date-text {
				font-size: 28rpx;
				font-weight: 600;
			}

			.weekday {
				font-size: 22rpx;
				color: #999;
				margin-left: 12rpx;
			}

			.day-summary {
				font-size: 22rpx;
				color: #999;

				.sum-item {
					margin-left: 15rpx;
				}
			}
		}

		.transaction-card {
			background: #fff;
			border-radius: 24rpx;
			overflow: hidden;

			.transaction-item {
				display: flex;
				justify-content: space-between;
				align-items: center;
				padding: 28rpx 30rpx;
				position: relative;

				&::after {
					content: '';
					position: absolute;
					bottom: 0;
					right: 30rpx;
					left: 124rpx;
					height: 1rpx;
					background: #f8f8f8;
				}

				&:last-child::after {
					display: none;
				}

				&.item-hover {
					background-color: #fcfcfc;
				}

				.item-left {
					display: flex;
					align-items: center;

					.icon-avatar {
						width: 80rpx;
						height: 80rpx;
						border-radius: 26rpx;
						display: flex;
						align-items: center;
						justify-content: center;
						font-size: 32rpx;
						font-weight: bold;
						color: #fff;
						margin-right: 24rpx;

						&.bg-expense {
							background: linear-gradient(135deg, #ff8a8a, $c-expense);
						}

						&.bg-income {
							background: linear-gradient(135deg, #87e8b0, $c-income);
						}
					}

					.item-detail {
						display: flex;
						flex-direction: column;

						.category-name {
							font-size: 30rpx;
							font-weight: 500;
						}

						.item-time {
							font-size: 22rpx;
							color: #999;
							margin-top: 6rpx;

							.remark-dot {
								display:inline-flex;
								max-width: 265rpx;
								color: #ccc;
								margin-left: 10rpx;
								white-space: nowrap;
								overflow: hidden;
								text-overflow: ellipsis;
								align-items: center;
							}
						}
					}
				}

				.item-right {
					.amount {
						font-size: 32rpx;
						font-weight: 700;

						&.expense {
							color: $c-text-main;
						}

						&.income {
							color: $c-income;
						}
					}
				}
			}
		}
	}

	.fab-btn {
		position: fixed;
		right: 40rpx;
		bottom: 60rpx;
		width: 110rpx;
		height: 110rpx;
		background: linear-gradient(135deg, $c-primary, #2b85e4);
		border-radius: 50%;
		display: flex;
		align-items: center;
		justify-content: center;
		box-shadow: 0 10rpx 20rpx rgba(60, 156, 255, 0.3);
		z-index: 99;
		will-change: transform;

		&:active {
			transform: scale(0.9);
		}
	}

	.empty-state {
		padding: 100rpx 0;
		display: flex;
		flex-direction: column;
		align-items: center;
		color: #ccc;

		text {
			font-size: 24rpx;
			margin-top: 20rpx;
		}
	}

	.safe-area-bottom {
		height: 25rpx;
	}
</style>