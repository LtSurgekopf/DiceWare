package me.ltsurgekopf.diceware.repository.model

data class PrimaryWord(
    val diceRoll: List<Int>,
    val word: String,
    val wordListIdentifier: String,
    var alternatives: List<Alternative>? = null
)

data class Alternative(
    val diceRoll: List<Int>,
    val word: String
)