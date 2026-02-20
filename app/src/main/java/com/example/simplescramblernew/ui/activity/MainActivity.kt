package com.example.simplescramblernew.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.simplescramblernew.ui.theme.SimpleScramblerNewTheme
import com.example.simplescramblernew.viewmodel.ScrambleViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { // Compose UI
            SimpleScramblerNewTheme { // 테마
                Scaffold( // 기본 레이아웃
                    modifier = Modifier.fillMaxSize() // 전체 화면
                ) { innerPadding -> // 기본 패딩
                    PagerScreen(
                        modifier = Modifier.padding(innerPadding) // 패딩 값 전달
                    )
                }
            }
        }
    }
}

// 화면을 그리는 함수
@Composable
fun PagerScreen(modifier: Modifier = Modifier) {
    val viewModel: ScrambleViewModel = viewModel()

    val pagerState = rememberPagerState( // 페이지 상태 저장
        pageCount = { 3 }
    )
    HorizontalPager(
        state = pagerState,
        modifier = modifier.fillMaxSize()
    ) { page ->
        Box( // 내부 레이아웃 컨테이너
            modifier = Modifier.fillMaxSize(), // 전체 화면
            contentAlignment = Alignment.Center // 가운데 정렬
        ) {
            when (page) {
                0 -> ScrambleScreen(viewModel)
                1 -> ListScreen(viewModel)
                2 -> Text("세 번째 페이지")
            }
        }
    }
}

@Composable
fun ScrambleScreen(viewModel: ScrambleViewModel) {
    // Compose 상태로 관리되는 스크램블 문자열 (초기 안내 문구 포함)
    var scramble by remember { mutableStateOf("TAP TO GENERATE") }

    var expanded by remember { mutableStateOf(false) } // 드롭다운 상태
    var selectedEvent by remember { mutableStateOf("3x3x3") }

    val faces = listOf("U", "R", "F", "B", "L", "D")
    val rotations = listOf("", "'", "2")

    // 면이 속한 회전 축 반환
    fun getAxis(face: String): String {
        return when (face) {
            "U", "D" -> "UD"
            "L", "R" -> "LR"
            "F", "B" -> "FB"
            else -> ""
        }
    }

    fun createScramble(): String {
        val builder = StringBuilder()
        var lastFace = "" // 직전 면 저장 (연속 동일 면 방지 ex. U U2)
        var secondLastFace = "" // 두 번째 직전 면 저장 (같은 축 3연속 방지 ex. R L R)

        val moves = if (selectedEvent == "3x3x3") 20 else 11
        val facesToUse = if (selectedEvent == "3x3x3") {
            faces
        } else {
            faces.subList(0, 3) // U R F
        }
        repeat(moves) {
            var face: String

            do {
                face = facesToUse.random()
            } while (
                face == lastFace || // 직전 면과 동일하거나
                // 같은 축이 3연속이면 다시
                (face == secondLastFace && getAxis(face) == getAxis(lastFace))
            )
            val rotation = rotations.random()

            if (builder.isNotEmpty()) builder.append(" ") // 첫 수가 아니면 공백 추가
            builder.append(face).append(rotation)

            secondLastFace = lastFace
            lastFace = face
        }
        return builder.toString()
    }

    fun generateScramble() {

        // 현재 값이 기본 문구가 아닐 때만 저장
        if (scramble != "TAP TO GENERATE") {
            viewModel.addScramble(scramble)
        }
        // 새 스크램블 생성
        scramble = createScramble()
    }

    Column( // 세로로 배치 (LinearLayout)
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .padding(top = 16.dp)
                .wrapContentSize(Alignment.TopCenter) // 드롭다운 위치를 텍스트에 맞춤
        ) {
            Text(
                text = selectedEvent,
                fontSize = 40.sp,
                color = Color.White,
                modifier = Modifier
                    .clickable { expanded = true } // 텍스트 클릭 시 드롭다운 열기
            )
            DropdownMenu(
                expanded = expanded, // 드롭다운 표시 여부
                onDismissRequest = { expanded = false } // 바깥 클릭 시 닫기
            ) {
                DropdownMenuItem(
                    text = { Text("3x3x3") },
                    onClick = {
                        selectedEvent = "3x3x3"
                        expanded = false
                        generateScramble()
                    }
                )
                DropdownMenuItem(
                    text = { Text("2x2x2") },
                    onClick = {
                        selectedEvent = "2x2x2"
                        expanded = false
                        generateScramble()
                    }
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f) // 남은 공간 전부 사용
                .padding(16.dp)
                .clickable { generateScramble() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = scramble,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ListScreen(viewModel: ScrambleViewModel) {

    val scrambleList = viewModel.scrambleList

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(scrambleList) { scramble ->
                Text(
                    text = scramble,
                    color = Color.White,
                    fontSize = 18.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

// 미리보기
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SimpleScramblerNewTheme {
        //PagerScreen()
        //ListScreen()
    }
}

/**
 * ViewPager -> HorizontalPager
 * RecyclerView -> LazyColumn
-UI를 그리는 방식이라서 View를 재활용하지 않음
-대신 필요한 것만 재구성(Recomposition)
 */