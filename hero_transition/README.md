# Hero Transition

# 単一要素で行う場合
### 遷移元の要素と遷移名をマッピングする

```kotlin
val extras = FragmentNavigatorExtras(view1 to "hero_image")

view.findNavController().navigate(
    R.id.confirmationAction,
    null, // Bundle of args
    null, // NavOptions
    extras)
```

### アニメーションを設定

```kotlin
// 遷移先
class FragmentB : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(requireContext())
             .inflateTransition(R.transition.shared_image)
    }
}
```

```xml
<!-- res/transition/shared_image.xml -->
<transitionSet>
    <changeImageTransform />
</transitionSet>
```

# 複数要素で行う場合(RecyclerView等)

### 共通要素
ヒーロー遷移には共通要素が必要

### 複数要素で動かすときの問題点

- 要素ごとに共通要素であることを結びつける必要があり、それぞれを区別する必要がある
（同じ`transitionName`をつけてしまうと動作しない）
- 遷移先のフラグメントに要素がまだ無い場合、期待通りの画面遷移にならない

## 画面遷移の設定

1. `setTransitionName` で遷移名を設定（一意な名前で要素を区別できるようにする）
2. `SharedElementCallbacks` で共通要素名と要素の対応関係を作る

## 遷移元

### 遷移時に遷移名を指定する

```kotlin
// アイテムクリック時に呼ばれるコールバック
override fun onCLick(item: MyItem, view: View) {
			// ...
      val extras = FragmentNavigatorExtras(view to "hero_image")
      findNavController().navigate(
          R.id.action_first_to_second,
          args.toBundle(),
          null,
          extras
      )
  }
```

### 遷移アニメーションの指定

戻ってくるときのアニメーションを指定する

```kotlin
// 遷移アニメーションの指定
exitTransition = TransitionInflater.from(requireContext())
    .inflateTransition(R.transition.exit_transition)
```

```xml
<!-- res/transition/exit_transition.xml -->
<?xml version="1.0" encoding="utf-8"?>
<transitionSet xmlns:android="http://schemas.android.com/apk/res/android"
    android:duration="375"
    android:interpolator="@android:interpolator/fast_out_slow_in"
    android:startDelay="25">
    <fade>
        <targets android:targetId="@id/image_view"/>
    </fade>
</transitionSet>
```

### SharedElementCallback

遷移元と遷移先の共通要素のマッピングをする

遷移元のフラグメントを離れるときと、「戻る」のナビゲーションのときに呼ばれる

```kotlin
setExitSharedElementCallback(object : SharedElementCallback() {
    override fun onMapSharedElements(
        names: MutableList<String>,
        sharedElements: MutableMap<String, View>
    ) {
        val id = names[0]
        val position = adapter.currentList.indexOfFirst { it.id.toString() == id }
        val selectedViewHolder = binding.recyclerListView.findViewHolderForAdapterPosition(position)
        if(selectedViewHolder?.itemView == null) return
        sharedElements[id] = selectedViewHolder.itemView.findViewById(R.id.image_view)
    }
})
```

```kotlin
names         : [item_1]
sharedElements: {item_1=androidx.appcompat.widget.AppCompatImageView{4857b05 VFED..C.. ...P.... 0,0-540,600 #7f0800d7 app:id/image_view}}
```

### 遷移の先送り

```kotlin
override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
): View {
    // ...


    postponeEnterTransition()

    viewModel.data.observe(viewLifecycleOwner) {
        // 全ての要素が揃ったあとにアニメーションを実行する
        (view?.parent as? ViewGroup)?.doOnPreDraw {
            startPostponedEnterTransition()
        }
    }
```

### 遷移時に強調する要素だけアニメーションしない

デフォルトの状態だと全ての要素にアニメーションが適用される。

もし、フェードアウトのアニメーションを指定していれば、遷移する要素にもそれが適用される。

なので、遷移前にその要素に対してだけアニメーションをつけないようにする

```kotlin
// view: ヒーロー遷移する要素
(exitTransition as TransitionSet).excludeTarget(view, true)
```

## 遷移先

### 遷移名を指定

FragmentNavigatorExtrasで指定した場合、遷移先の`onCreate`が遷移元の`onDestroy`よりも前に呼ばれる。

```kotlin
override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
): View {
    // ...

    ViewCompat.setTransitionName(binding.imageView, "hero_image")
}
```

### アニメーションを指定

```kotlin
override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
): View {
    // ...

    sharedElementEnterTransition = TransitionInflater.from(requireContext())
            .inflateTransition(R.transition.shared_image_transition)
}
```

```xml
<!-- res/transition/shared_image_transition -->
<?xml version="1.0" encoding="utf-8"?>
<transitionSet
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:duration="375"
    android:interpolator="@android:interpolator/fast_out_slow_in"
    android:transitionOrdering="together">
    <changeClipBounds/>
    <changeTransform/>
    <changeBounds/>
</transitionSet>
```

### 遷移の先送り

うまく動作させるには画像が読み込まれるまで、遷移を先送りする必要がある。

そのために、

- `onCreateView()`で`postponeEnterTransition()`を呼び出し、遷移を先送りする
- 読み込みが完了したタイミングで`startPostponedEnterTransition()`を呼び出して画面性を実行

```kotlin
override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
): View {
    // ...

    // 遷移を先送り
    postponeEnterTransition()

    Glide.with(requireContext())
        .load(imageUrl)
        .addListener(object : RequestListener<Drawable> {
            override fun onLoadFailed(/*...*/): Boolean {
                // 画像の読み込みが失敗したときも、UIが停止してしまわないように遷移を実行する
                startPostponedEnterTransition()
                return false
            }

            override fun onResourceReady(/*...*/): Boolean {
                // 先送りにしていた遷移を実行
                startPostponedEnterTransition()
                return false
            }
        })
        .into(binding.imageView)
}
```