package com.example.simplescramblernew.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplescramblernew.data.ScrambleDataStore
import com.example.simplescramblernew.data.ScrambleUiState
import kotlinx.coroutines.launch
import androidx.compose.runtime.State

// UI와 로직 분리를 위한 ViewModel
class ScrambleViewModel(application: Application) : AndroidViewModel(application) {
    private val context = getApplication<Application>()

    // 상태를 외부(UI)에서 수정하지 못하도록 캡슐화
    private val _uiState = mutableStateOf(ScrambleUiState())
    val uiState: State<ScrambleUiState> = _uiState

    private val faces = listOf("U", "R", "F", "B", "L", "D")
    private val rotations = listOf("", "'", "2")

    // 면이 속한 축 반환 (같은 축 3연속 방지용)
    private fun getAxis(face: String): String {
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
        _uiState.value = _uiState.value.copy(selectedEvent = event)
        generateScramble()
    }

    fun generateScramble() {
        val currentState = _uiState.value // 현재 UI 상태

        // 이전 스크램블 저장
        if (currentState.scramble != null) {
            val updatedList = currentState.scrambleList + currentState.scramble
            updateScrambleList(updatedList)
        }
        // 새 스크램블 생성
        val newScramble = buildScramble(currentState.selectedEvent)
        _uiState.value = _uiState.value.copy(scramble = newScramble)
    }

    private fun buildScramble(event: String) : String {
        val builder = StringBuilder()
        var lastFace = "" // 직전 면
        var secondLastFace = "" // 두 번째 직전 면

        val moves = if (event == "3x3x3") 20 else 11
        val facesToUse = if (event == "3x3x3") faces else faces.take(3) // U R F

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
        return builder.toString()
    }

    private fun updateScrambleList(newList: List<String>) {
        _uiState.value = _uiState.value.copy(scrambleList = newList)
        // 스크램블 리스트를 DataStore에 비동기로 저장
        viewModelScope.launch { ScrambleDataStore.save(context, newList) }
    }

    private fun loadScrambles() {
        viewModelScope.launch {
            val savedList = ScrambleDataStore.load(context)
            _uiState.value = _uiState.value.copy(scrambleList = savedList)
        }
    }

    fun deleteScramble(scramble: String) {
        val updatedList = _uiState.value.scrambleList - scramble
        updateScrambleList(updatedList)
    }

    fun deleteAllScrambles() {
        updateScrambleList(emptyList())
    }
}