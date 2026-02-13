package com.example.simplescramblernew

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.simplescramblernew.ui.theme.SimpleScramblerNewTheme

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
                0 -> Text("첫 번째 페이지")
                1 -> Text("두 번째 페이지")
                2 -> Text("세 번째 페이지")
            }
        }
    }
}

// 미리보기
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SimpleScramblerNewTheme {
        PagerScreen()
    }
}