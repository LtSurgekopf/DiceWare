package me.ltsurgekopf.diceware

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import me.ltsurgekopf.diceware.repository.model.PrimaryWord

class WordCardViewAdapter(private val clickListener: WordCardClickListener) :
    RecyclerView.Adapter<WordCardViewAdapter.WordCardViewViewHolder>() {

    private val wordResults = mutableListOf<PrimaryWord>()

    fun addWordToResults(word: PrimaryWord) {
        wordResults.add(word)
        notifyDataSetChanged()
    }

    interface WordCardClickListener {
        fun itemClicked(primaryWord: PrimaryWord)
        fun itemLongClicked(primaryWord: PrimaryWord): Boolean
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordCardViewViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.word_card_view_holder,
                parent,
                false
            )
        return WordCardViewViewHolder(view)
    }

    override fun getItemCount(): Int = wordResults.size

    override fun onBindViewHolder(holder: WordCardViewViewHolder, position: Int) {
        val primaryWord = wordResults[position]
        holder.dieImageViews.zip(primaryWord.diceRoll).forEach {
            val iv = it.first
            val die = it.second
            iv.setImageResource(WordCardViewViewHolder.getResourceForDie(die))
            iv.contentDescription = die.toString()
        }
        holder.textViewWord.text = primaryWord.word
        holder.itemView.setOnLongClickListener {
            return@setOnLongClickListener clickListener.itemLongClicked(primaryWord)
        }
        holder.itemView.setOnClickListener {
            clickListener.itemClicked(primaryWord)
        }

    }

    fun removeWord(primaryWord: PrimaryWord) {
        wordResults.remove(primaryWord)
        notifyDataSetChanged()
    }

    fun clearWords() {
        wordResults.clear()
        notifyDataSetChanged()
    }

    class WordCardViewViewHolder(iv: View) : RecyclerView.ViewHolder(iv) {
        private val die1 = iv.findViewById(R.id.ivDice1) as ImageView
        private val die2 = iv.findViewById(R.id.ivDice2) as ImageView
        private val die3 = iv.findViewById(R.id.ivDice3) as ImageView
        private val die4 = iv.findViewById(R.id.ivDice4) as ImageView
        private val die5 = iv.findViewById(R.id.ivDice5) as ImageView

        val textViewWord = iv.findViewById(R.id.textViewWord) as TextView

        val dieImageViews = listOf(die1, die2, die3, die4, die5)


        companion object {
            fun getResourceForDie(die: Int): Int {
                return when (die) {
                    1 -> R.drawable.dice_1
                    2 -> R.drawable.dice_2
                    3 -> R.drawable.dice_3
                    4 -> R.drawable.dice_4
                    5 -> R.drawable.dice_5
                    6 -> R.drawable.dice_6
                    else -> R.drawable.empty_dice
                }
            }
        }
    }
}