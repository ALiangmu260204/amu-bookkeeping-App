<template>
	<view class="report-container">
		<!-- 顶部粘性切换栏 -->
		<view class="sticky-header">
			<view class="date-selector" @click="showMonthPicker = true">
				<text class="date-text">{{ selectedMonthText }}</text>
				<up-icon name="arrow-down-fill" size="12" color="#3c9cff"></up-icon>
			</view>
			<view class="type-tabs">
				<view v-for="(item, index) in typeTabs" :key="index" class="tab-item" :class="{ active: activeTab === index }"
					@click="activeTab = index">
					{{ item.name }}
				</view>
			</view>
		</view>

		<!-- 数据总览 -->
		<view class="summary-card">
			<view class="summary-item">
				<text class="label">本月支出</text>
				<text class="value expense">¥{{ monthStats.totalExpense.toFixed(2) }}</text>
			</view>
			<view class="divider"></view>
			<view class="summary-item">
				<text class="label">本月收入</text>
				<text class="value income">¥{{ monthStats.totalIncome.toFixed(2) }}</text>
			</view>
		</view>

		<!-- 分类占比（环形图） -->
		<view class="chart-section">
			<view class="section-header">
				<text class="title">{{ currentTypeName }}占比</text>
			</view>
			<view class="chart-box" v-if="pieChartData.series && pieChartData.series[0].data.length > 0">
				<qiun-data-charts canvasId="report_pie_wechat" type="ring" :opts="ringOpts" :chartData="pieChartData"
					:reshow="activeTab >= 0" />
			</view>
			<view v-else class="empty-chart">本月暂无数据</view>
		</view>

		<!-- 趋势分析（柱状图） -->
		<view class="chart-section">
			<view class="section-header">
				<text class="title">{{ currentTypeName }}趋势</text>
			</view>
			<view class="chart-box">
				<qiun-data-charts canvasId="report_bar_wechat" type="column" :opts="columnOpts" :chartData="trendChartData"
					:reshow="activeTab >= 0" />
			</view>
		</view>

		<!-- 详细排行列表 -->
		<view class="rank-section">
			<view class="section-header"><text class="title">{{ currentTypeName }}排行</text></view>
			<view class="rank-list">
				<view class="rank-item" v-for="(item, index) in categoryData" :key="index">
					<view class="rank-left">
						<view class="rank-index" :class="'top-' + (index + 1)">{{ index + 1 }}</view>
						<text class="rank-name">{{ item.name }}</text>
					</view>
					<view class="rank-right">
						<view class="progress-bar-bg">
							<view class="progress-bar-fill"
								:style="{ width: item.percent + '%', backgroundColor: currentThemeColor }"></view>
						</view>
						<view class="amount-info">
							<text class="amount">¥{{ item.value.toFixed(2) }}</text>
							<text class="percent">{{ item.percent }}%</text>
						</view>
					</view>
				</view>
				<view v-if="categoryData.length === 0" class="empty-tip">本月暂无{{ currentTypeName }}记录</view>
			</view>
		</view>

		<up-datetime-picker :show="showMonthPicker" v-model="monthPickerValue" mode="year-month" @confirm="onDateConfirm"
			@cancel="showMonthPicker = false"></up-datetime-picker>
	</view>
</template>

<script setup>
	import { ref, computed, watch } from 'vue'
	import { useRecordStore } from '@/store/transactionRecord'
	import { transactionRecordApi } from '@/apis/transactionRecord_api'
	import { onShow } from '@dcloudio/uni-app'

	const rawList = ref([])
	const statsList = ref({})
	const activeTab = ref(0)
	const typeTabs = [
		{ name: '支出', value: 'expense' }, 
		{ name: '收入', value: 'income' },
	]
	const showMonthPicker = ref(false)
	const monthPickerValue = ref(Date.now())
	const currentMonth = new Date()
	const selectedMonthText = ref(`${currentMonth.getFullYear()}年${(currentMonth.getMonth() + 1).toString().padStart(2, '0')}月`)

	// 页面显示时
	onShow(async () => {
		loadData()
		statsList.value = await loadMonthlyStats()
	})

	// 载入指定年月份数据
	const loadData = async () => {
		const res = await transactionRecordApi.getMonthRecord(selectedMonthText.value.substring(0, 4), 
															parseInt(selectedMonthText.value.substring(5, 7), 10))
		await useRecordStore().appendRecordList(res.data)
		
		const saved = useRecordStore().recordList || [];
		const flatList = []
		saved.forEach(group => {
			const date = new Date(group.transactionTime)
			const dateStr = `${date.getFullYear()}年${(date.getMonth() + 1).toString().padStart(2, '0')}月`
			if (dateStr === selectedMonthText.value) {
				flatList.push(group)
			}
		})
		rawList.value = flatList
	}

	// 载入指定年份里所有月份消费金额统计
	const loadMonthlyStats = async () => {
		const res = await transactionRecordApi.getMonthlyStats(selectedMonthText.value.substring(0, 4))
		return res.data
	}

	// tab变换
	const currentTypeName = computed(() => typeTabs[activeTab.value].name)
	const currentTypeKey = computed(() => typeTabs[activeTab.value].value)
	const currentThemeColor = computed(() => activeTab.value === 0 ? '#3c9cff' : '#2ecc71')

	// 统计当月支出和收入总额
	const monthStats = computed(() => {
		let exp = 0, inc = 0
		rawList.value.forEach(i => {
			i.isType === 0 ? exp += i.amount : inc += i.amount
		})
		return { totalExpense: exp, totalIncome: inc }
	})

	// 统计当月支出或收入分类总额
	const categoryData = computed(() => {
		const groups = {};
		const typeFilter = currentTypeKey.value === 'expense' ? 0 : 1
		const filtered = rawList.value.filter(i => i.isType === typeFilter)
		const total = filtered.reduce((s, i) => s + i.amount, 0)

		filtered.forEach(i => {
			const categoryName = i.categoryName && i.categoryName.trim() !== '' ? i.categoryName : '未知分类'
			groups[categoryName] = (groups[categoryName] || 0) + i.amount 
		})
		
		return Object.keys(groups).map(k => ({
			name: k,
			value: groups[k],
			percent: total > 0 ? ((groups[k] / total) * 100).toFixed(1) : 0
		})).sort((a, b) => b.value - a.value)
	})

	// 圆环图数据配置
	const pieChartData = computed(() => ({		
		series: [{
			data: categoryData.value.map(i => ({
				name: i.name,
				value: i.value
			}))
		}]
	}))

	// 圆环图配置
	const ringOpts = computed(() => ({
        rotate: false,
        rotateLock: false,
        padding: [5,5,5,5],
        dataLabel: true,
        enableScroll: false,
		legend: { show: false },
		title: {
			name: currentTypeName.value,
			fontSize: 24,
			color: "#666666"
		},
		subtitle: {
			name: "合计",
			fontSize: 22,
			color: "#7cb5ec"
		},
		extra: {
			ring: {
				ringWidth: 35,
				activeOpacity: 0.55,
            	activeRadius: 15,
				offsetAngle: 0,
				labelWidth: 10,
				border: true,
				borderWidth: 2,
				borderColor: "#FFFFFF",
				linearType: "custom"
			}
		}
	}))

	// 柱状图数据配置
	const trendChartData = computed(() => {
		const monthData = activeTab.value === 0 ? statsList.value.monthExpense : statsList.value.monthIncome

		return {
			categories: ['01', '02', '03', '04', '05', '06', '07', '08', '09', '10', '11', '12'],
			series: [{
				name: currentTypeName.value,
				data: monthData ? monthData.map(i => Number(i)) : [],
				color: currentThemeColor.value
			}]
    	}
	})

	// 柱状图配置
	const columnOpts = {
		xAxis: {
			disableGrid: true
		},
		yAxis: {
			data: [{ min: 0 }]
		},
		extra: {
			column: {
				type: "group",
				width: 15,
			}
		}
	}

	// 更改年月份
	const onDateConfirm = (e) => {
		const d = new Date(e.value);
		selectedMonthText.value = `${d.getFullYear()}年${String(d.getMonth() + 1).padStart(2, '0')}月`
		showMonthPicker.value = false
	}

	// 监听年月份变动，执行相关时间
	watch(selectedMonthText, async () => {
		loadData()
		statsList.value = await loadMonthlyStats()
	})
</script>

<style lang="scss" scoped>
	.report-container {
		padding: 30rpx;
		background-color: #f8f9fb;
		min-height: 100vh;
	}

	.sticky-header {
		position: sticky;
		top: 0;
		z-index: 99;
		background: #f8f9fb;
		padding-bottom: 20rpx;

		.date-selector {
			display: flex;
			align-items: center;
			justify-content: center;
			padding: 20rpx 0;

			.date-text {
				font-size: 32rpx;
				font-weight: bold;
				margin-right: 8rpx;
			}
		}

		.type-tabs {
			display: flex;
			background: #eaebed;
			border-radius: 100rpx;
			padding: 8rpx;

			.tab-item {
				flex: 1;
				text-align: center;
				padding: 16rpx 0;
				font-size: 28rpx;
				color: #888;
				border-radius: 100rpx;

				&.active {
					background: #fff;
					color: #3c9cff;
					font-weight: bold;
				}
			}
		}
	}

	.summary-card {
		background: #fff;
		border-radius: 32rpx;
		padding: 40rpx;
		display: flex;
		margin-bottom: 30rpx;

		.summary-item {
			flex: 1;
			display: flex;
			flex-direction: column;
			align-items: center;

			.label {
				font-size: 24rpx;
				color: #999;
			}

			.value {
				font-size: 38rpx;
				font-weight: 800;
			}

			.expense {
				color: #333;
			}

			.income {
				color: #2ecc71;
			}
		}

		.divider {
			width: 1rpx;
			height: 50rpx;
			background: #f0f0f0;
			align-self: center;
		}
	}

	.chart-section {
		background: #fff;
		border-radius: 32rpx;
		padding: 30rpx;
		margin-bottom: 30rpx;

		.section-header {
			margin-bottom: 20rpx;

			.title {
				font-size: 30rpx;
				font-weight: bold;
			}
		}

		.chart-box {
			width: 100%;
			height: 480rpx;
		}

		.empty-chart {
			height: 480rpx;
			display: flex;
			align-items: center;
			justify-content: center;
			color: #ccc;
			font-size: 26rpx;
		}
	}

	.rank-section {
		background: #fff;
		border-radius: 32rpx;
		padding: 30rpx;

		.title {
			font-size: 30rpx;
			font-weight: bold;
			margin-bottom: 30rpx;
		}

		.rank-list {
			.rank-item {
				display: flex;
				align-items: center;
				padding: 26rpx 0;
				border-bottom: 1rpx solid #fcfcfc;

				.rank-left {
					display: flex;
					align-items: center;
					width: 180rpx;

					.rank-index {
						width: 38rpx;
						height: 38rpx;
						border-radius: 10rpx;
						background: #f5f6f8;
						font-size: 22rpx;
						display: flex;
						align-items: center;
						justify-content: center;
						color: #aaa;
						margin-right: 20rpx;

						&.top-1 {
							background: #fff0f0;
							color: #ff5252;
						}

						&.top-2 {
							background: #fff7e6;
							color: #ff9900;
						}

						&.top-3 {
							background: #f0f7ff;
							color: #3c9cff;
						}
					}

					.rank-name {
						font-size: 28rpx;
					}
				}

				.rank-right {
					flex: 1;
					display: flex;
					flex-direction: column;

					.progress-bar-bg {
						height: 10rpx;
						background: #f5f5f5;
						border-radius: 10rpx;
						margin-bottom: 12rpx;
						overflow: hidden;
					}

					.progress-bar-fill {
						height: 100%;
						border-radius: 10rpx;
						transition: 0.8s;
					}

					.amount-info {
						display: flex;
						justify-content: space-between;

						.amount {
							font-size: 26rpx;
							font-weight: 700;
						}

						.percent {
							font-size: 22rpx;
							color: #bbb;
						}
					}
				}
			}
		}
	}

	.empty-tip {
		padding: 60rpx 0;
		text-align: center;
		color: #ccc;
		font-size: 26rpx;
	}
</style>