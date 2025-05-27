package com.danono.dodgedrive.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.danono.dodgedrive.ScoreboardActivity
import com.danono.dodgedrive.adapters.ScoreAdapter
import com.danono.dodgedrive.databinding.FragmentScoreListBinding
import com.danono.dodgedrive.interfaces.ScoreCallback
import com.danono.dodgedrive.model.ScoreManager
import com.danono.dodgedrive.model.ScoreRecord


class ScoreListFragment : Fragment() {
    private lateinit var binding: FragmentScoreListBinding
    private lateinit var scoreAdapter: ScoreAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentScoreListBinding.inflate(inflater, container, false)
        initScoreList()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.post {
            adjustRecyclerViewHeight()
        }
    }

    private fun initScoreList() {
        scoreAdapter = ScoreAdapter(emptyList())
        scoreAdapter.scoreCallback = object : ScoreCallback {
            override fun onScoreClicked(score: ScoreRecord, position: Int) {
                (activity as? ScoreboardActivity)?.showLocation(
                    score.latitude,
                    score.longitude
                )
            }
        }

        binding.scoreListLSTScores.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = scoreAdapter
            clipToPadding = false
            setPadding(paddingLeft, paddingTop, paddingRight, 8.dp)
        }

        updateScoreList()
    }

    private fun adjustRecyclerViewHeight() {
        val recyclerView = binding.scoreListLSTScores
        val adapter = recyclerView.adapter
        if ((adapter?.itemCount ?: 0) > 0) {
            recyclerView.layoutManager?.findViewByPosition(0)?.let { firstItem ->
                val itemHeight = firstItem.height
                val desiredHeight = (itemHeight * 3) +
                        recyclerView.paddingTop +
                        recyclerView.paddingBottom +
                        (16.dp)

                recyclerView.layoutParams = recyclerView.layoutParams.apply {
                    height = desiredHeight
                }
            }
        }
    }

    private fun updateScoreList() {
        scoreAdapter.updateScores(ScoreManager.getTopScores())
    }

    private val Int.dp: Int
        get() = (this * resources.displayMetrics.density).toInt()

    companion object {
        const val ARG_PLAYER_NAME = "ARG_PLAYER_NAME"
        const val ARG_SCORE = "ARG_SCORE"
        const val ARG_DATE = "ARG_DATE"

    }
}