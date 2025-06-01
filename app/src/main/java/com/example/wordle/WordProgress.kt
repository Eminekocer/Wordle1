package com.mertatmaca.sinavmodul

data class WordProgress(
    val wordId: Int,
    var streak: Int,
    var lastCorrectDate: Long,
    val history: MutableList<Long>
)