# View Pager2
## 概要
- ViewPagerをレイアウトに追加
- FragmentStateAdapterからFragmentを生成
- ViewPagerにFragmentStateAdapterを適用

### ViewPagerをレイアウトに追加

```xml
<androidx.viewpager2.widget.ViewPager2
      xmlns:android="http://schemas.android.com/apk/res/android"
      android:id="@+id/pager"
      android:layout_width="match_parent"
      android:layout_height="match_parent" />
```

### FragmentStateAdapterからFragmentを生成

```kotlin
class PagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
		// ページ数を指定
    override fun getItemCount(): Int {
        return PAGE_COUNT
    }

		// Fragmentを作成
    override fun createFragment(position: Int): Fragment {
        return PageFragment()
    }
}
```

### ViewPagerにFragmentStateAdapterを適用

```kotlin
private lateinit var viewPager: ViewPager2

viewPager = binding.pager
val adapter = PagerAdapter(this, pageCount)
viewPager.adapter = adapter
```

## バックボタンでページを移動する
```kotlin
override fun onBackPressed() {
    if(viewPager.currentItem == 0){
        super.onBackPressed()
    }else {
        viewPager.currentItem = viewPager.currentItem - 1
    }
}
```