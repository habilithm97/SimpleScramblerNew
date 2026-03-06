# [심플 스크램블러]
## 큐버들을 위한. 타이머 기능 없는. 심플한 큐브 스크램블러 앱
큐브를 무작위로 섞을 수 있는 방법을 제공합니다. (3x3x3 및 2x2x2, 기호 숙지 필수)

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
👉 스크램블 생성 로직 (세계 큐브 협회 (WCA) 규칙 기반)
- 같은 면 연속 회전 방지 (ex. U U')
- 같은 축 3연속 회전 방지 (ex R L R)
- 2x2x2 큐브는 U R F면만 사용
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
