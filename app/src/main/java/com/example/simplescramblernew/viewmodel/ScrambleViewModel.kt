package com.example.simplescramblernew.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

class ScrambleViewModel : ViewModel() {
    var scrambleList = mutableStateListOf<String>() // Compose 자동 갱신
        private set // 외부에서 수정 못함

    fun addScramble(scramble: String) {
        scrambleList.add(scramble)
    }
}