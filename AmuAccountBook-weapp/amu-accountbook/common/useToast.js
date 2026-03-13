export function toast(title, duration = 3000) {
    uni.showToast({
        title: String(title),
        icon: 'none',
        duration
    })
}