<script>
	import { silentLogin } from '@/utils/silentLogin'
	import { toast } from '@/common/useToast'

	export default {
		// TODO 整个App程序待优化
		// 每次重启小程序格式化一边持久的store，避免脏数据
		onLaunch: async function() {
			try {
				toast('登录中...')
				await silentLogin()
				uni.reLaunch({
					url: '/pages/Index/Index'
				})
			} catch (error) {
				toast(error)
			}
		},
		onShow: function() {},
		onHide: function() {}
	}
</script>

<style lang="scss">
	/*每个页面公共css */
	@import "uview-plus/index.scss";
</style>
