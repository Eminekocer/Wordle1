package com.example.wordle

data class WordProgress(
    val wordId: String,
    var correctCount: Int = 0,
    var lastReviewTime: Long = 0L,
    var history: MutableList<Long> = mutableListOf()
)
