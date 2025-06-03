package com.example.wordle

data class WordProgress(
    val wordId: Int,
    var streak: Int,
    var lastCorrectDate: Long,
    val history: MutableList<Long>
)