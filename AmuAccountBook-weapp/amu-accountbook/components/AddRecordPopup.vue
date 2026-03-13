<template>
	<NotifyRef ref="uNotifyRef" />

	<up-popup :show="modelValue" mode="bottom" round="20" @close="handleClose">
		<view class="popup-content">
			<view class="header">
				<text class="title">{{ initialData ? '修改记录' : '记一笔' }}</text>
				<up-icon name="close" color="#ccc" size="21" @click="handleClose"></up-icon>
			</view>

			<view class="tabs">
				<view v-for="t in types" :key="t.value" class="tab-item" :class="{ active: recordType === t.value }"
					@click="changeType(t.value)">{{ t.label }}</view>
			</view>

			<view class="input-section">
				<view class="amount-row">
					<text class="symbol">¥</text>
					<input type="digit" v-model="amount" class="amount-input" placeholder="0.00"
						@input="handleAmountInput" maxlength="11" step="0.01" />
				</view>
				<view class="datetime-trigger" @click="showPicker = true">
					<up-icon name="calendar" size="16" color="#3c9cff"></up-icon>
					<text class="datetime-text">{{ currentDate }}</text>
				</view>
			</view>

			<scroll-view scroll-y class="category-scroll">
				<view class="category-grid">
					<view class="cat-item" v-for="item in currentCategories" :key="item.id" @click="selectedCategory = item"
						@longpress="handleLongPress(item)">
						<view class="cat-icon-wrapper">
							<image class="cat-icon" :src="`/static/category/${item.icon}.svg`" />
						</view>
						<text class="cat-name" :class="{ active: selectedCategory?.id === item.id }">{{ item.name }}</text>
					</view>
					<view class="cat-item" @click="showAddCatModal = true">
						<view class="cat-icon-wrapper add-style">
							<up-icon name="plus" size="22" color="#999" />
						</view>
						<text class="cat-name">新增</text>
					</view>
				</view>
			</scroll-view>

			<view class="remark-section">
				<view class="remark-input-container">
					<up-textarea v-model="remark" placeholder="备注内容..." border="none" maxlength="30" 
						:count="true" style="height: 80rpx;" />
				</view>
			</view>

			<view class="footer">
				<up-button text="确认保存" type="primary" shape="circle" color="linear-gradient(45deg, #3c9cff, #2b85e4)" 
					@click="handleSave" />
			</view>
			<view class="safe-area-inset-bottom"></view>
		</view>

		<up-modal :show="showAddCatModal" title="请输入分类名称" showCancelButton @confirm="confirmAddCategory"
			@cancel="showAddCatModal = false, newCatName = ''">
			<up-input v-model="newCatName" placeholder="最多4个字符" maxlength="4" inputAlign="center" border="bottom" fontSize="20" />
		</up-modal>

		<up-datetime-picker :show="showPicker" v-model="pickerTS" mode="datetime" @confirm="onDatetimeConfirm"
			@cancel="showPicker = false" />
	</up-popup>
</template>

<script setup>
	import { ref, computed, watch } from 'vue'
	import NotifyRef from '@/components/NotifyRef.vue'
	import { useCategoryStore } from '@/store/category'
	import { useUserStore } from '@/store/user'
	import { categoryApi } from '@/apis/category_api'
	import { transactionRecordApi } from '@/apis/transactionRecord_api'
	import { onHide } from '@dcloudio/uni-app'

	const props = defineProps({
		modelValue: Boolean,
		initialData: Object
	})
	const emit = defineEmits(['update:modelValue', 'add'])

	const types = [
		{ label: '支出', value: 'expense' }, 
		{ label: '收入', value: 'income'}
	]
	const recordType = ref('expense')
	const amount = ref('')
	const showPicker = ref(false)
	const pickerTS = ref(Date.now())
	const selectedTime = ref(new Date())
	const selectedCategory = ref(null)
	const remark = ref('')
	const showAddCatModal = ref(false)
	const newCatName = ref('')
	const uNotifyRef = ref(null)
	const isUpdateMode = ref(false)

	// tab变换
	const changeType = (type) => {
		recordType.value = type
	}

	// 合法金额格式
	const handleAmountInput = (e) => {
		let value = e.detail.value;
		// 1. 只保留数字和小数点
		value = value.replace(/[^\d.]/g, '');
		// 2. 不能以小数点开头
		value = value.replace(/^\./, '');
		// 3. 合并多个小数点为一个
		const parts = value.split('.');
		if (parts.length > 2) {
			value = parts[0] + '.' + parts.slice(1).join('');
		}
		// 4. 限制小数最多两位
		if (value.includes('.')) {
			const decimalPart = value.split('.')[1];
			if (decimalPart.length > 2) {
			value = value.substring(0, value.indexOf('.') + 3); // 保留 . 后两位
			}
		}
		// 5. 处理整数部分的前导零
		if (value) {
			const [integerPart, decimalPart] = value.split('.');
			// 如果整数部分全为 0（如 "000"），保留一个 "0"
			// 否则去掉前导零（如 "00123" → "123"）
			let cleanInteger = integerPart.replace(/^0+(\d)/, '$1'); // 去掉开头的 0，但保留至少一位
			if (cleanInteger === '') {
			cleanInteger = '0'; // 比如输入 "000" → 应该变成 "0"
			}
			value = decimalPart !== undefined ? `${cleanInteger}.${decimalPart}` : cleanInteger;
		}

		amount.value = value
	}

	// 监听初始数据变化，如果有初始数据则填充到表单中
	const currentDate = computed(() => {
		const date = selectedTime.value;
		return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate())
			.padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
	})

	// 处理日期时间选择器的确认事件
	const onDatetimeConfirm = (e) => {
		selectedTime.value = new Date(e.value)
		showPicker.value = false
	}
	
	// 根据当前记录类型过滤出对应的分类列表，0表示支出，1表示收入
	const currentCategories = computed(() => {
		return useCategoryStore().categoryList.filter(cat => cat.isType === (recordType.value === 'expense' ? 0 : 1))
	})

	// 长按分类项，弹出删除确认框
	const handleLongPress = (item) => {
		if (item.isSystem) return uNotifyRef.value?.warning('系统分类无法删除')
		uni.showModal({
			title: '删除自定义分类',
			content: `确定删除“${item.name}”？`,
			confirmColor: '#ff5252',
			success: (res) => {
				if (res.confirm) {
					const data = { name: item.name, isType: item.isType }
					categoryApi.deleteCategory(data).then(res => {
						if (res.code === 20000) {
							uNotifyRef.value?.success(`${item.name} 分类删除成功`)
							useCategoryStore().deleteCategory(item.name)
						} else {
							uNotifyRef.value?.error(res.notify || `${item.name} 分类删除失败`)
						}
					}).catch(err => {
						uNotifyRef.value?.error(err.message || `${item.name} 分类删除失败，请稍后再试`)
					})
				}
			}
		})
	}

	// 新增分类
	const confirmAddCategory = async () => {
		selectedCategory.value = null
		
		if (newCatName.value.trim() === '') {
			uNotifyRef.value?.warning("分类名称不能为空")
			return
		} else {
			try {
				const data = { name: newCatName.value.trim(), isType: recordType.value === 'expense' ? 0 : 1 }
				const res = await categoryApi.addCategory(data)
				if(res.code === 20000) {
					useCategoryStore().addCategory(res.data)
					selectedCategory.value = res.data
					uNotifyRef.value?.success("新增分类成功")
				} else {
					uNotifyRef.value?.warning(res.notify || "新增分类失败")
				}
			} catch (err) {
				uNotifyRef.value?.error(err.message || "新增分类失败，请稍后再试")
			}
			newCatName.value = ''
		}

		showAddCatModal.value = false
	}

	// 添加交易记录
	const handleSave = () => {
		const val = parseFloat(amount.value)
		if (!val || val <= 0) return uNotifyRef.value?.warning("请输入合法金额")
		if (!selectedCategory.value) return uNotifyRef.value?.warning("请选择分类")

		const data = {
			amount: val,
			categoryId: selectedCategory.value?.id,
			categoryName: selectedCategory.value?.name,
			isType: recordType.value === 'expense' ? 0 : 1,
			remark: remark.value.trim(),
			transactionTime: currentDate.value
		}

		if (isUpdateMode.value) {
			data.id = props.initialData?.id
			transactionRecordApi.updateRecord(data).then(res => {
				if (res.code === 20000) {
					uNotifyRef.value?.success("修改成功")
					emit('add', data)
				} else {
					uNotifyRef.value?.error(res.notify || "修改失败")
				}
			}).catch(err => {
				uNotifyRef.value?.error(err.message || "修改失败，请稍后再试")
			})

			isUpdateMode.value = false
		} else {
			transactionRecordApi.addRecord(data).then(res => {
				if (res.code === 20000) {
					const total = useUserStore().recordTotal + 1
					useUserStore().setRecordTotal(total)
					emit('add', res.data)
					uNotifyRef.value?.success("添加成功")
				} else {
					uNotifyRef.value?.error(res.notify || "添加失败")
				}
			}).catch(err => {
				uNotifyRef.value?.error(err.message || "添加失败，请稍后再试")
			})
		}

		handleClose()
	}

	// 关闭弹窗时重置表单数据
	const handleClose = () => {
		amount.value = ''
		recordType.value = 'expense'
		selectedTime.value = new Date()
		selectedCategory.value = {}
		remark.value = ''
		emit('update:modelValue', false)
	}
	
	// 监听初始数据变化，如果有初始数据则填充到表单中
	watch(() => props.initialData, (newData) => {
		if (newData) {
			amount.value = newData.amount
			recordType.value = newData.isType === 0 ? 'expense' : 'income'
			selectedTime.value = new Date(newData.transactionTime)
			remark.value = newData.remark || ''
			const category = useCategoryStore().categoryList.find(cat => cat.id === newData.categoryId)
			selectedCategory.value = category || {}
			isUpdateMode.value = true
		}
	}, { immediate: true })

	// 页面隐藏时重置数据
	onHide(() => {
		handleClose()
	})
</script>

<style scoped lang="scss">
	.popup-content {
		padding: 30rpx 40rpx;
		background: #fff;

		.header {
			display: flex;
			justify-content: space-between;
			align-items: center;
			margin-bottom: 30rpx;

			.title {
				font-size: 34rpx;
				font-weight: bold;
			}
		}

		.tabs {
			display: flex;
			background: #f1f3f5;
			border-radius: 100rpx;
			padding: 6rpx;
			margin-bottom: 30rpx;

			.tab-item {
				flex: 1;
				text-align: center;
				padding: 16rpx 0;
				font-size: 28rpx;
				border-radius: 100rpx;

				&.active {
					background: #fff;
					color: #3c9cff;
					font-weight: bold;
				}
			}
		}

		.input-section {
			border-bottom: 2rpx solid #f8f8f8;
			margin-bottom: 30rpx;
			padding-bottom: 20rpx;

			.amount-row {
				display: flex;
				align-items: center;

				.symbol {
					font-size: 50rpx;
					font-weight: bold;
					margin-right: 20rpx;
				}

				.amount-input {
					font-size: 70rpx;
					height: 90rpx;
					font-weight: bold;
					flex: 1;
				}
			}

			.datetime-trigger {
				display: inline-flex;
				align-items: center;
				background: #f0f7ff;
				padding: 8rpx 18rpx;
				border-radius: 8rpx;
				margin-top: 10rpx;

				.datetime-text {
					font-size: 24rpx;
					color: #3c9cff;
					margin: 0 10rpx;
				}
			}
		}

		.category-scroll {
			max-height: 320rpx;
			margin-bottom: 30rpx;

			.category-grid {
				display: grid;
				grid-template-columns: repeat(4, 1fr);
				gap: 24rpx;

				.cat-item {
					display: flex;
					flex-direction: column;
					align-items: center;

					.cat-icon-wrapper {
						width: 100rpx;
						height: 100rpx;
						background: #f8f9fb;
						border-radius: 30rpx;
						display: flex;
						align-items: center;
						justify-content: center;
						font-size: 44rpx;
						border: 4rpx solid transparent;

						&.active {
							background: #eef7ff;
							border-color: #3c9cff;
						}

						&.add-style {
							border: 2rpx dashed #ccc;
							background: transparent;
						}
					}
					
					.cat-icon {
						width: 64rpx;
						height: 64rpx;
					}

					.cat-name {
						font-size: 24rpx;
						color: #888;
						margin-top: 10rpx;

						&.active {
							color: #3c9cff;
							font-weight: bold;
						}
					}
				}
			}
		}

		.remark-section {
			background: #f8f9fb;
			border-radius: 16rpx;
			padding: 12rpx 20rpx;
			margin-bottom: 40rpx;
			overflow-wrap: break-word; 

			.remark-input-container {
				position: relative;
			}
		}
	}

	.safe-area-inset-bottom {
		padding-bottom: env(safe-area-inset-bottom);
	}
</style>