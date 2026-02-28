package com.example.simplescramblernew.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplescramblernew.data.ScrambleDataStore
import kotlinx.coroutines.launch

// ViewModel -> 화면/로직 분리, 상태 안전 유지
class ScrambleViewModel(application: Application) : AndroidViewModel(application) {
    private val context = getApplication<Application>()

    var selectedEvent by mutableStateOf("3x3x3")
        private set

    var scramble by mutableStateOf("TAP TO GENERATE")
        private set

    var scrambleList = mutableStateListOf<String>()
        private set

    val faces = listOf("U", "R", "F", "B", "L", "D")
    val rotations = listOf("", "'", "2")

    // 면이 속한 축 반환 (같은 축 3연속 방지용)
    fun getAxis(face: String): String {
        return when (face) {
            "U", "D" -> "UD"
            "L", "R" -> "LR"
            "F", "B" -> "FB"
            else -> ""
        }
    }

    init {
        loadScrambles()
    }

    fun setEvent(event: String) {
        selectedEvent = event
        generateScramble()
    }

    fun generateScramble() {
        if (scramble != "TAP TO GENERATE") {
            scrambleList.add(scramble)

            // 리스트 변경 후 저장
            viewModelScope.launch {
                ScrambleDataStore.save(context, scrambleList)
            }
        }
        val builder = StringBuilder()
        var lastFace = "" // 직전 면
        var secondLastFace = "" // 두 번째 직전 면

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
                face == lastFace || // 같은 면 연속 방지 (U U2)
                (face == secondLastFace && getAxis(face) == getAxis(lastFace)) // 같은 축 3연속 방지 (R L R)
            )
            val rotation = rotations.random()

            if (builder.isNotEmpty()) builder.append(" ") // 첫 번째 기호가 아니면 공백 추가
            builder.append(face).append(rotation)

            secondLastFace = lastFace
            lastFace = face
        }
        scramble  = builder.toString()
    }

    private fun loadScrambles() {
        viewModelScope.launch {
            val savedList = ScrambleDataStore.load(context)
            scrambleList.clear()
            scrambleList.addAll(savedList)
        }
    }

    fun deleteScramble(index: Int) {
        scrambleList.removeAt(index)

        viewModelScope.launch {
            ScrambleDataStore.save(context, scrambleList)
        }
    }
}