package me.ltsurgekopf.diceware.repository.model

import android.graphics.Color
import me.ltsurgekopf.diceware.R

data class Password(
    var primaryWords: MutableList<PrimaryWord> = mutableListOf<PrimaryWord>()
) {
    fun entirePassword(): String = primaryWords.joinToString(separator = " ") { p -> p.word }

    val entropy: Double
        get() = ENTROPY_PER_WORD * primaryWords.size

    val rating: Int
        get() = when (primaryWords.size) {
            0 -> 0
            in 1..4 -> 1
            5 -> 2
            6 -> 3
            7 -> 4
            else -> 5
        }

    val ratingColor: Int
        get() = when (rating) {
            0 -> R.color.rating0
            1 -> R.color.rating1
            2 -> R.color.rating2
            3 -> R.color.rating3
            4 -> R.color.rating4
            else -> R.color.rating5
        }

    companion object {
        const val ENTROPY_PER_WORD = 12.9
    }
}