package com.example.simplescramblernew.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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

class MainActivity : ComponentActivity() { // Compose 기반의 메인 액티비티
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { // Compose UI
            SimpleScramblerNewTheme { // 테마
                Scaffold( // 기본 레이아웃 (Material 구조체)
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding -> // 시스템 UI 영역을 고려한 패딩 값 제공
                    PagerScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun PagerScreen(modifier: Modifier = Modifier) {
    // Compose 생명주기에 맞는 ViewModel 인스턴스 (화면 간 상태 공유)
    val scrambleViewModel: ScrambleViewModel = viewModel()

    // 현재 페이지 상태 기억
    val pagerState = rememberPagerState(pageCount = { 2 })

    HorizontalPager(
        state = pagerState,
        modifier = modifier.fillMaxSize()
    ) { page ->
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when (page) {
                0 -> ScrambleScreen(scrambleViewModel)
                1 -> ListScreen(scrambleViewModel)
            }
        }
    }
}

@Composable
fun ScrambleScreen(scrambleViewModel: ScrambleViewModel) {
    val selectedEvent = scrambleViewModel.selectedEvent // 선택된 종목 상태
    var expanded by remember { mutableStateOf(false) } // 드롭다운 상태
    val scramble = scrambleViewModel.scramble

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .padding(top = 16.dp)
                // 내용 크기만큼만 차지, 상단 중앙 정렬
                .wrapContentSize(Alignment.TopCenter)
        ) {
            Text(
                text = selectedEvent,
                fontSize = 40.sp,
                color = Color.White,
                modifier = Modifier.clickable { expanded = true }
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false } // 바깥 클릭 시 메뉴 닫기
            ) {
                DropdownMenuItem(
                    text = { Text("3x3x3") },
                    onClick = {
                        expanded = false
                        scrambleViewModel.setEvent("3x3x3")
                    }
                )
                DropdownMenuItem(
                    text = { Text("2x2x2") },
                    onClick = {
                        expanded = false
                        scrambleViewModel.setEvent("2x2x2")
                    }
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp)) // 세로 여백

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f) // 남은 공간 모두 차지
                .padding(16.dp)
                .clickable { scrambleViewModel.generateScramble() },
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(scrambleViewModel: ScrambleViewModel) {

    val scrambleList = scrambleViewModel.scrambleList
    var selectedScramble by remember { mutableStateOf<Int?>(null) } // 삭제 대상 index
    var menuExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        TopAppBar( // 상단 앱바 (TopAppBar)
            title = {
                Text(
                    text = "사용한 스크램블 목록",
                    color = Color.White
                )
            },
            actions = { // 액션 영역
                Box {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "메뉴",
                            tint = Color.White
                        )
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("전체 삭제") },
                            onClick = { menuExpanded = false }
                        )
                    }
                }
            },
            // TopAppBar 색상 설정 (Material3 전용)
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Black
            )
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = 24.dp,
                    end = 24.dp,
                    bottom = 32.dp
                )
        ) {
            itemsIndexed(scrambleList.asReversed()) { reversedIndex, scramble -> // 최신순 정렬
                val realIndex = scrambleList.lastIndex - reversedIndex // 원본 index 계산

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .combinedClickable(
                            onClick = {},
                            // 롱클릭 시 해당 스크램블의 원본 index를 저장
                            onLongClick = { selectedScramble = realIndex }
                        )
                ) {
                    Text(
                        text = scramble,
                        color = Color.White,
                        fontSize = 18.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )
                    HorizontalDivider( // 구분선
                        thickness = 1.dp,
                        color = Color.DarkGray
                    )
                }
            }
        }
    }
    selectedScramble?.let { scramble ->
        AlertDialog(
            onDismissRequest = { selectedScramble = null }, // 바깥 클릭 시 선택 해제
            title = { Text("삭제 확인") },
            text = { Text("이 스크램블을 삭제하시겠습니까?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        scrambleViewModel.deleteScramble(scramble)
                        selectedScramble = null // 삭제 후 상태 초기화
                    }
                ) { Text("삭제") }
            },
            dismissButton = {
                TextButton(
                    onClick = { selectedScramble = null } // 취소 시 상태 초기화
                ) { Text("취소") }
            }
        )
    }
}

// 미리보기 화면
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SimpleScramblerNewTheme {
        //PagerScreen()
        //ListScreen()
    }
}

/**
 * Column -> LinearLayout
 * Box -> FrameLayout
 * ViewPager -> HorizontalPager
 * RecyclerView -> LazyColumn
-UI를 그리는 방식이라 View를 재활용하지 않음
-대신 필요한 것만 재구성(Recomposition)
 */