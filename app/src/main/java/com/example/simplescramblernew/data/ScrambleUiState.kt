package com.example.simplescramblernew.data

data class ScrambleUiState(
    val selectedEvent: String = "3x3x3",
    val scramble: String? = null,
    val scrambleList: List<String> = emptyList()
)