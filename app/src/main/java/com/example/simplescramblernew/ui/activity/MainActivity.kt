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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed

class MainActivity : ComponentActivity() { // Jetpack Compose 기반
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // 시스템 바까지 화면 확장
        setContent { // Compose UI
            SimpleScramblerNewTheme {
                Scaffold( // 화면 레이아웃 기본 틀
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    PagerScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun PagerScreen(modifier: Modifier = Modifier) {
    // Compose 생명주기에 맞는 ViewModel 인스턴스 (화면 간 상태 공유)
    val scrambleViewModel: ScrambleViewModel = viewModel()

    // 2페이지 Pager 상태 기억
    val pagerState = rememberPagerState(pageCount = { 2 })

    HorizontalPager( // Compose용 ViewPager 역할
        state = pagerState,
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black),
    ) { page ->
        Box( // Compose용 FrameLayout 역할
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center // 내용 중앙 정렬
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
    val uiState = scrambleViewModel.uiState.value
    val selectedEvent = uiState.selectedEvent
    var isDropdownExpanded by remember { mutableStateOf(false) }
    val events = listOf("3x3x3", "2x2x2")
    val scramble = uiState.scramble

    Column( // Compose용 LinearLayout 역할
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally // 가로 중앙 정렬
    ) {
        Box( // 종목 선택 영역
            modifier = Modifier.padding(top = 16.dp)) {
            Text(
                text = selectedEvent,
                fontSize = 40.sp,
                color = Color.White,
                modifier = Modifier.clickable { isDropdownExpanded = true }
            )
            DropdownMenu(
                expanded = isDropdownExpanded,
                onDismissRequest = { isDropdownExpanded = false }
            ) {
                events.forEach { event ->
                    DropdownMenuItem(
                        text = { Text(event) },
                        onClick = {
                            isDropdownExpanded = false
                            scrambleViewModel.setEvent(event)
                        }
                    )
                }
            }
        }
        Box( // 스크램블 표시 영역
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f) // 남은 공간 모두 차지
                .padding(16.dp)
                .clickable { scrambleViewModel.generateScramble() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = scramble ?: "TAP TO GENERATE",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
    }
}

// Material3 TopAppBar 사용을 위한 Experimental API opt-in
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(scrambleViewModel: ScrambleViewModel) {
    var selectedScramble by remember { mutableStateOf<String?>(null) }
    var showDeleteAllDialog by remember { mutableStateOf(false) }
    var menuExpanded by remember { mutableStateOf(false) }

    val uiState = scrambleViewModel.uiState.value
    val scrambleList = uiState.scrambleList

    selectedScramble?.let { scramble ->
        AlertDialog(
            onDismissRequest = { selectedScramble = null },
            title = { Text("삭제 확인") },
            text = { Text("이 스크램블을 삭제하시겠습니까?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        scrambleViewModel.deleteScramble(scramble)
                        selectedScramble = null
                    }
                ) { Text("삭제") }
            },
            dismissButton = {
                TextButton(
                    onClick = { selectedScramble = null }
                ) { Text("취소") }
            }
        )
    }
    if (showDeleteAllDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAllDialog = false },
            title = { Text("전체 삭제") },
            text = { Text("모든 스크램블을 삭제하시겠습니까?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        scrambleViewModel.deleteAllScrambles()
                        showDeleteAllDialog = false
                    }
                ) { Text("삭제") }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteAllDialog = false }
                ) { Text("취소") }
            }
        )
    }
    Scaffold(
        containerColor = Color.Black,
        topBar = {
            TopAppBar(
                windowInsets = WindowInsets(0), // 상태바 inset 제거
                title = {
                    Text(
                        text = "사용한 스크램블 목록",
                        color = Color.White
                    )
                },
                actions = {
                    Box { // 우측 더보기 메뉴 영역
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
                                onClick = {
                                    menuExpanded = false
                                    showDeleteAllDialog = true
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black
                )
            )
        }
    ) { innerPadding ->
        // Scaffold Content
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // TopAppBar 밑에서 스크램블 리스트 시작
                .padding(
                    start = 24.dp,
                    end = 24.dp,
                    bottom = 24.dp
                )
        ) {
            // 스크램블 리스트 역순 정렬
            itemsIndexed(scrambleList.asReversed(), key = { _, it -> it }) { index, scramble ->
                val number = scrambleList.size - index // 역순 정렬에 맞는 번호 계산

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .combinedClickable(
                            onClick = {},
                            onLongClick = { selectedScramble = scramble }
                        )
                ) {
                    Text(
                        text = "$number. $scramble",
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
}

// 미리보기 화면
@Preview(showBackground = true)
@Composable
fun GreetingPreview() { SimpleScramblerNewTheme {} }

/**
 * RecyclerView -> LazyColumn
-UI를 그리는 방식이라 View를 재활용하지 않음
-대신 필요한 것만 재구성(Recomposition)
 */