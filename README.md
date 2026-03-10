# [심플 스크램블러]
## 큐버들을 위한. 타이머 기능 없는. 심플한 큐브 스크램블러 앱
큐브 연습을 위한 무작위로 섞을 수 있는 방법을 제공합니다. (기호 숙지 필수)

<img src="https://github.com/user-attachments/assets/72582bed-3cdf-48e4-8611-ffe6e6f36ae1" width="300"/>
<img src="https://github.com/user-attachments/assets/26f09588-223f-4bed-89d5-4d68fccedae2" width="300"/>

## 🛠️ 기술 스택
- Kotlin
- Jetpack Compose
- Compose State (mutableStateOf)
- MVVM Architecture
- Android ViewModel
- Kotlin Coroutines
- Android DataStore
- Compose Pager
## ✅ 주요 기능 (Snippet)
### 👉 스크램블 생성 ( WCA(세계큐브협회) 규칙 기반 )
- 3x3x3 / 2x2x2 지원
- 같은 면 연속 회전 방지 (ex. U U')
- 같은 축 3연속 회전 방지 (ex. R L R)
- 2x2x2는 U R F면만 사용
```kotlin
private fun buildScramble(event: String) : String {
        val builder = StringBuilder()
        var lastFace = "" // 직전 면
        var secondLastFace = "" // 두 번째 직전 면

        val moves = if (event == "3x3x3") 20 else 11
        val facesToUse = if (event == "3x3x3") faces else faces.take(3)

        repeat(moves) {
            var face: String

            do {
                face = facesToUse.random()
            } while (
                face == lastFace ||
                (face == secondLastFace && getAxis(face) == getAxis(lastFace))
            )
            val rotation = rotations.random()

            if (builder.isNotEmpty()) builder.append(" ") // 첫 번째 기호가 아니면 공백 추가
            builder.append(face).append(rotation)

            secondLastFace = lastFace
            lastFace = face
        }
        return builder.toString()
    }
```
<br>

### 👉 스크램블 히스토리 저장
- DataStore를 사용해 생성된 스크램블을 로컬에 저장
```kotlin
private val Context.dataStore by preferencesDataStore(name = "scramble")

object ScrambleDataStore {
    private val scrambleListKey = stringPreferencesKey("scramble_list")
    private val gson = Gson()

    // 저장 (리스트 -> JSON)
    suspend fun save(context: Context, list: List<String>) {
        val json = gson.toJson(list)
        context.dataStore.edit { prefs ->
            prefs[scrambleListKey] = json
        }
    }

    // 복원 (JSON -> List<String>)
    suspend fun load(context: Context): List<String> {
        val prefs = context.dataStore.data.first()
        val json = prefs[scrambleListKey] ?: "[]"
        return gson.fromJson(json, object : TypeToken<List<String>>() {}.type)
    }
}
```

<br>

### 👉 스크램블 히스토리 관리
- 생성된 스크램블을 목록으로 확인
- 개별 삭제 및 전체 삭제 기능 지원
```kotlin
Scaffold(
    topBar = {
        TopAppBar(
            title = { Text("사용한 스크램블 목록") },
            actions = {
                Box {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "메뉴"
                        )
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("전체 삭제") },
                            onClick = {
                                menuExpanded = false
                                showDeleteAllDialog = true
                            }
                        )
                    }
                }
            }
        )
    }
) { innerPadding ->
    LazyColumn(
        modifier = Modifier
            .padding(innerPadding)
    ) {
        itemsIndexed(scrambleList.asReversed(), key = { _, it -> it }) { index, scramble ->
            val number = scrambleList.size - index // 역순 정렬에 맞는 번호 계산

            Column(
                modifier = Modifier
                    .combinedClickable(
                        onClick = {},
                        onLongClick = { selectedScramble = scramble }
                    )
            ) {
                Text(text = "$number. $scramble")
                HorizontalDivider()
            }
        }
    }
}
```
## 📂 프로젝트 구조
```text
app
 ┣ data
 ┃ ┗ ScrambleDataStore.kt
 ┣ ui
 ┃ ┗ activity
 ┃    ┗ MainActivity.kt
 ┣ viewmodel
 ┃ ┣ ScrambleViewModel.kt
 ┃ ┗ ScrambleUiState.kt
```
