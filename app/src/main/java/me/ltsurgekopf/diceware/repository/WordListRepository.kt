package me.ltsurgekopf.diceware.repository

import android.content.Context
import android.content.res.Resources
import me.ltsurgekopf.diceware.repository.model.Alternative
import me.ltsurgekopf.diceware.repository.model.PrimaryWord
import java.nio.charset.Charset

class WordListRepository(private val context: Context) {

    /**
     * retrieves a word list from the asset folder of the app, for bundled lists (english only!).
     * The list is identified by an identifier of the form "${lang}[[-${suffix}]], and should have a
     * file name ending with ".txt". The files are retrieved via the asset manager from the [path]
     * directory. Returns a somewhat lazy string sequence.
     */
    private fun getFromBundledAsset(identifier: String, path: String = "raw/"): Sequence<String> {

        val list = context.assets.list(path)
        val filename = "$identifier.txt"
        if (list != null && list.contains(filename)) {
            val fullName = path.plus(filename)
            return readAsset(fullName)
        }
        throw IllegalArgumentException("No asset with given filename $filename in path $path")
    }

    /**
     * gets a word list by its identifer of the form "${lang}[[-${suffix}]]. First considers
     * the bundled assets, then (NYI!) looks at the file system for user-added files
     */
    fun getByIdentifier(identifier: String = "en"): Sequence<String> {
        try {
            return getFromBundledAsset(identifier)
        } catch (e: IllegalArgumentException) {
            throw Resources.NotFoundException("A word list with this identifier could not be found")
        }
    }

    /**
     * helper function to read an asset by [filename] as a line sequence
     */
    private fun readAsset(filename: String, charset: Charset = Charsets.UTF_8): Sequence<String> =
        context.assets.open(filename).bufferedReader(charset).lineSequence()

    /**
     * parses a list of [diceRolls] into a base-6 number and calculates the line offset in the
     * word list. Example: {1,1,1,1,1} is line# 0, {1,1,1,1,2} is line# 1, {1,1,1,2,1} is line# 6.
     */
    fun calculateLineNumberFromRoll(diceRolls: List<Int>): Int {
        if (diceRolls.size != 5) {
            throw IllegalArgumentException("Exactly 5 dice rolls are required!")
        }
        if (diceRolls.any { i -> i < 1 || i > 6 }) {
            throw IllegalArgumentException("Dice roll not between 1 and 6")
        }

        var lineNumber: Int = 0
        lineNumber = diceRolls.joinToString(separator = "") { i -> (i - 1).toString() }.toInt(6)

        return lineNumber
    }

    /**
     * gets a word from the word list identified by [identifier] for a specified die ordering
     */
    fun getWordByDiceRoll(diceRolls: List<Int>, identifier: String = "en"): PrimaryWord {
        val offset = calculateLineNumberFromRoll(diceRolls)
        val wordList = getByIdentifier(identifier)
        val word = if (offset == 0) parseLine(wordList.first()) else parseLine(
            wordList.drop(offset).first()
        )
        return PrimaryWord(diceRolls, word!!, identifier)
    }


    /**
     * parses the [line] by removing the first 5 characters (die sequence) and removing whitespace
     */
    private fun parseLine(line: String?): String? = line?.substring(5)?.trim()

    /**
     * gets 4 alternatives from a word list ([identifier]) for [diceRolls], obtained by shifting
     * the first die to the end 4 times. Deliberately does not account for all possible combinations
     * (theoretically 5! = 120, but less on average due to duplicates die rolls), because this might
     * lead users to choose words they identify with
     */
    fun getPossibleWordsByDiceRoll(
        diceRolls: List<Int>,
        identifier: String = "en"
    ): PrimaryWord {
        val diceOrder = diceRolls.toMutableList()
        val primaryWord = getWordByDiceRoll(diceOrder, identifier)
        primaryWord.alternatives = mutableListOf()
        val alternatives = mutableListOf<Alternative>()
        for (item in 1..5) {
            val firstDie = diceOrder.removeAt(0)
            diceOrder.add(firstDie)
            val word = getWordByDiceRoll(diceOrder, identifier)
            alternatives.add(Alternative(word.diceRoll, word.word))
        }
        primaryWord.alternatives = alternatives
        return primaryWord
    }

}