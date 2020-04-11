package me.ltsurgekopf.diceware

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_scrolling.*
import kotlinx.android.synthetic.main.content_scrolling.*
import me.ltsurgekopf.diceware.repository.WordListRepository
import me.ltsurgekopf.diceware.repository.model.Password
import me.ltsurgekopf.diceware.repository.model.PrimaryWord

class ScrollingActivity : AppCompatActivity(), WordCardViewAdapter.WordCardClickListener {

    private lateinit var wordListRepository: WordListRepository
    private lateinit var wordCardViewAdapter: WordCardViewAdapter
    private val password = Password()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scrolling)
        setSupportActionBar(toolbar)
        val layoutManager = LinearLayoutManager(this)
        rvCardsWords.layoutManager = layoutManager
        wordCardViewAdapter = WordCardViewAdapter(this)
        rvCardsWords.adapter = wordCardViewAdapter
        wordListRepository = WordListRepository(this)
        fab.setOnClickListener {
            addDiceRoll()
        }
        textViewEntropy.text = getString(R.string.entropy, 0f)
        textViewNumberWords.text = getString(R.string.number_words, 0)
    }

    private fun addDiceRoll() {
        val randomDies = (1..5).map { _ -> (1..6).random() }
        val word = wordListRepository.getWordByDiceRoll(randomDies)
        wordCardViewAdapter.addWordToResults(word)
        password.primaryWords.add(word)
        updateTopGui()
        if (password.primaryWords.size == MAX_WORDS) {
            fab.isEnabled = false
            Snackbar.make(fab, getString(R.string.password_long_enough, MAX_WORDS), Snackbar.LENGTH_SHORT).show()
        }

    }

    private fun updateTopGui() {
        val entirePassword = password.entirePassword()
        val rating = password.rating
        val color = resources.getColor(password.ratingColor, theme)
        val entropy = "%.1f bits".format(password.entropy)
        val numWords = getString(R.string.number_words, password.primaryWords.size)

        textViewEntropy.text = entropy
        fullPasswordTextView.text = entirePassword
        progress_bar_entropy.progress = rating
        progress_bar_entropy.progressTintList = ColorStateList.valueOf(color)
        textViewNumberWords.text = numWords
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_scrolling, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return when (item.itemId) {
            R.id.action_reset -> {
                resetPassword()
                return true
            }
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun resetPassword() {
        password.primaryWords.clear()
        wordCardViewAdapter.clearWords()
        updateTopGui()
        fab.isEnabled = true
    }

    override fun itemClicked(primaryWord: PrimaryWord) {

    }

    private fun deleteWord(primaryWord: PrimaryWord) {
        password.primaryWords.remove(primaryWord)
        wordCardViewAdapter.removeWord(primaryWord)
        updateTopGui()
        fab.show()
    }

    override fun itemLongClicked(primaryWord: PrimaryWord): Boolean {
        Snackbar.make(
            rvCardsWords,
            getString(R.string.remove_word_word, primaryWord.word),
            Snackbar.LENGTH_LONG
        )
            .setAction(R.string.yes) {
                deleteWord(primaryWord)
            }.show()
        return true
    }

    companion object {
        const val MAX_WORDS = 16
    }
}
