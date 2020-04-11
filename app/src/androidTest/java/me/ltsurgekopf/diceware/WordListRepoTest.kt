package me.ltsurgekopf.diceware

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import me.ltsurgekopf.diceware.repository.WordListRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class WordListRepoTest {

    lateinit var appContext: Context
    lateinit var wordListRepository: WordListRepository

    @Before
    fun setup() {
        appContext = InstrumentationRegistry.getInstrumentation().targetContext
        wordListRepository = WordListRepository(appContext)
    }

    @Test
    fun readEnListYieldsSomething() {
        assertNotNull(wordListRepository.getByIdentifier("en"))
    }

    @Test
    fun testCalculateLineNumberFromRollExpected() {
        val outputsInputs: List<Pair<Int, List<Int>>> = listOf(
            Pair(6, listOf(1, 1, 1, 2, 1)),
            Pair(0, listOf(1, 1, 1, 1, 1)),
            Pair(1, listOf(1, 1, 1, 1, 2)),
            Pair(1757, listOf(2, 3, 1, 5, 6))
        )
        outputsInputs.forEach {
            assert(calculateLineNumberIsCorrect(it.first, it.second))
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun testCalculateLineNumberFromRollOutOfRangeThrows() {
        wordListRepository.calculateLineNumberFromRoll(listOf(0, 1, 1, 1, 7))
    }

    @Test(expected = IllegalArgumentException::class)
    fun testCalculateLineNumberFromRollWrongLengthThrows() {
        wordListRepository.calculateLineNumberFromRoll(listOf(1))
    }

    private fun calculateLineNumberIsCorrect(expected: Int, inputs: List<Int>): Boolean =
        wordListRepository.calculateLineNumberFromRoll(inputs) == expected

    @Test
    fun getWordByDiceRollCorrect() {
        val inputs: List<Pair<List<Int>, String>> = listOf(
            Pair(listOf(1, 1, 1, 1, 1), "a"),
            Pair(listOf(1, 1, 1, 1, 2), "a&p"),
            Pair(listOf(6, 6, 6, 6, 6), "@")
        )
        inputs.forEach {
            val wordByDiceRoll = wordListRepository.getWordByDiceRoll(it.first)
            assertNotNull(wordByDiceRoll)
            assertEquals(it.second, wordByDiceRoll.word)
        }
    }

    @Test
    fun getPossibleWordsByDiceRollReturnsFiveOptions() {
        val inputs = listOf(1, 2, 3, 4, 5)
        val word = wordListRepository.getPossibleWordsByDiceRoll(inputs)
        assertNotNull(word.alternatives)
        assertEquals(5, word.alternatives?.size)
    }
}