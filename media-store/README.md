# Media Store
Media Storeではある方法によりファイルのUriを取得する。  
取得したUriを参照することで画像や動画を表示することができる。

## 選択ツール
検索条件を満たすすべてのドキュメント プロバイダのドキュメントにアクセスできる**システム UI**
- ### インテントからの結果を受け取るために定数を用意
  ```kotlin
  private const val RESULT_PICK_IMAGE_FILE = 1000
  ```
- ### `ACTION_GET_CONTENT`でインテントを立ち上げ
  目的のMIMEタイプを指定
  ```kotlin
  val intent = Intent(Intent.**ACTION_OPEN_DOCUMENT**)
      .addCategory(Intent.**CATEGORY_OPENABLE**) 
      .setType("image/*")
  startActivityForResult(intent, RESULT_PICK_IMAGE_FILE)
  ```

- ### URIを取得
  onActivityResultの`data`から`URI`を取得
  ```kotlin
  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
      when (requestCode) {
          RESULT_PICK_IMAGE_FILE -> {
              if (resultCode != RESULT_OK) return
              data?.let {
                  val uri = it.data
                  // URIを元に画像を表示する
              }
          }
      }
  }
  ```

### `CATEGORY_OPENABLE`
onActivityResultで受け取るファイルのURIを以下に限定
- コンテンツ プロバイダ（`content`: URI）からアクセスできる
- `openFileDescriptor()` でファイル ストリームとして利用できるファイル

---

## ContentResolverにより取得
- ### クエリを作成
  ```kotlin
  // 取得するカラム
  // nullを指定するとすべてのカラムが返されて非効率的
  val projection = arrayOf(
      MediaStore.Images.Media._ID,
      MediaStore.Images.Media.DISPLAY_NAME,
      MediaStore.Images.Media.DATE_ADDED
  )
  ```
  ```kotlin
  // SQLのwhere句と同じフォーマット(WHEREは省く)
  // 指定がないとすべての行を返す
  val selection = "${MediaStore.Images.Media.DATE_ADDED} >= ?"
  // "?" をsectionにつけた場合、配列の要素が順に置き換えられる
  val selectionArgs = arrayOf(
      Utils.dateToTimestamp(day = 22, month = 10, year = 2008).toString()
  )
  ```
  ```kotlin
  // SQLのORDER BY と同じフォーマットで
  // 順番を指定することができる
  val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"
  ```
  ```kotlin
  val cursor: Cursor? = contentResolver.query(
      MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
      projection,
      selection,
      selectionArgs,
      sortOrder
  )
  ```

- ### ファイルに関する情報を取得
  ```kotlin
  withContext(Dispatchers.IO) {
              cursor?.use { cursor ->
      /*
      *	use句を使うことで、最後にcursorがcloseされる
      */
  ```
  ```kotlin
  // ファイルに関する情報のカラム
  val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
  val dateModifiedColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
  val displayNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
  ```
  ```kotlin
  while (cursor.moveToNext()) {
      // カラムからファイルに関する情報を取り出す
      val id = cursor.getLong(idColumn)
      val dateModified = Date(TimeUnit.SECONDS.toMillis(cursor.getLong(dateModifiedColumn)))
      val displayName = cursor.getString(displayNameColumn)

      // 取得したメディアidを,パスに追加してURIを取得する
      val contentUri = ContentUris.withAppendedId(
          MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
          id
      )
  }
  ```