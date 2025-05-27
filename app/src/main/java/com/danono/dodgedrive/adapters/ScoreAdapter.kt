package com.danono.dodgedrive.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.danono.dodgedrive.databinding.ScoreItemBinding
import com.danono.dodgedrive.interfaces.ScoreCallback
import com.danono.dodgedrive.model.ScoreRecord
import java.time.format.DateTimeFormatter

class ScoreAdapter(private var scores: List<ScoreRecord>) :
    RecyclerView.Adapter<ScoreAdapter.ScoreViewHolder>() {

    var scoreCallback: ScoreCallback? = null

    inner class ScoreViewHolder(val binding: ScoreItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                scoreCallback?.onScoreClicked(
                    getItem(adapterPosition),
                    adapterPosition
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScoreViewHolder {
        val binding = ScoreItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ScoreViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ScoreViewHolder, position: Int) {
        val score = getItem(position)
        with(holder.binding) {
            playerName.text = score.playerName
            scoreDate.text = score.date.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"))
            scoreValue.text = score.score.toString()
            milesValue.text = "Saved"
        }
    }




    override fun getItemCount() = scores.size

    fun getItem(position: Int) = scores[position]

    fun updateScores(newScores: List<ScoreRecord>) {
        scores = newScores
        notifyDataSetChanged()
    }
}