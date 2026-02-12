package com.example.simplescramblernew

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
                    Greeting(
                        name = "Android", // 전달 값
                        modifier = Modifier.padding(innerPadding) // 패딩
                    )
                }
            }
        }
    }
}

// 화면 함수
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!", // 텍스트 출력
        modifier = modifier // 패딩 적용
    )
}

// 미리보기
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SimpleScramblerNewTheme {
        Greeting("Android")
    }
}