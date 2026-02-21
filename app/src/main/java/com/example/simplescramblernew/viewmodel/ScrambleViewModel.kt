package com.example.simplescramblernew.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

// ViewModel -> 화면/로직 분리, 상태 안전 유지
class ScrambleViewModel : ViewModel() {
    var scrambleList = mutableStateListOf<String>() // Compose 자동 갱신
        private set // 외부에서 수정 못함

    fun addScramble(scramble: String) {
        scrambleList.add(scramble)
    }
}