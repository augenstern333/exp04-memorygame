package com.example.liushimin2024110523.exp04

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.recyclerview.widget.GridLayoutManager
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.example.liushimin2024110523.exp04.databinding.ActivityMemoryGameBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/** Controller: the model owns matching and scoring; the views only draw its current state. */
class MemoryGameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMemoryGameBinding
    private lateinit var game: CardMatchingGame
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var cardAdapter: CardAdapter
    private var attempts = 0
    private var bestScore = 0
    private var feedback = R.string.game_ready
    private var resultShown = false
    private var resultDialog: androidx.appcompat.app.AlertDialog? = null
    private val hideMismatch = Runnable {
        game.dismissMismatch()
        feedback = R.string.game_ready
        render()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMemoryGameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        configureSystemBars(binding.root)
        bestScore = savedInstanceState?.getInt(getString(R.string.state_best))
            ?: intent.getIntExtra(getString(R.string.extra_high_score), 0)
        @Suppress("DEPRECATION")
        val restoredGame = savedInstanceState?.getSerializable(getString(R.string.state_game)) as? CardMatchingGame
        if (restoredGame == null) {
            game = CardMatchingGame(CARD_COUNT)
        } else {
            game = restoredGame
            attempts = savedInstanceState?.getInt(getString(R.string.state_attempts)) ?: 0
            feedback = savedInstanceState?.getInt(getString(R.string.state_feedback), R.string.game_ready) ?: R.string.game_ready
            resultShown = savedInstanceState?.getBoolean(getString(R.string.state_result_shown)) ?: false
        }
        buildCards()
        render(animateChanges = false)
        updateResult()
        if (game.hasPendingMismatch) handler.postDelayed(hideMismatch, MISMATCH_PREVIEW_MS)
        if (game.isComplete && resultShown) binding.root.post { showResult() }

        binding.btnRestart.pressFeedback()
        binding.btnExit.pressFeedback()
        binding.btnBack.pressFeedback()
        binding.btnRestart.setOnClickListener {
            val hasProgress = attempts > 0 || (0 until game.cardCount).any { game.cardAtIndex(it).isChosen }
            if (hasProgress && !game.isComplete) {
                MaterialAlertDialogBuilder(this)
                    .setTitle(R.string.restart_title)
                    .setMessage(R.string.restart_message)
                    .setNegativeButton(R.string.keep_playing, null)
                    .setPositiveButton(R.string.restart_confirm) { _, _ -> startNewGame() }
                    .show()
            } else startNewGame()
        }
        binding.btnExit.setOnClickListener { finishGame() }
        binding.btnBack.setOnClickListener { finishGame() }
        binding.btnRules.setOnClickListener {
            MaterialAlertDialogBuilder(this).setTitle(R.string.game_rules_title)
                .setMessage(R.string.game_rules_message).setPositiveButton(R.string.got_it, null).show()
        }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() = finishGame()
        })
    }

    private fun startNewGame() {
        handler.removeCallbacksAndMessages(null)
        resultDialog?.dismiss()
        resultDialog = null
        updateResult()
        game.reset()
        attempts = 0
        resultShown = false
        feedback = R.string.game_ready
        buildCards()
        render(animateChanges = false)
    }

    private fun buildCards() {
        val columns = resources.getInteger(R.integer.board_columns)
        binding.gridCards.layoutManager = GridLayoutManager(this, columns)
        binding.gridCards.isNestedScrollingEnabled = false
        binding.gridCards.itemAnimator = null
        cardAdapter = CardAdapter(game, ::chooseCard)
        binding.gridCards.adapter = cardAdapter
    }

    private fun chooseCard(index: Int) {
        if (game.hasPendingMismatch || game.isComplete || game.cardAtIndex(index).isMatched) return
        val card = game.cardAtIndex(index)
        val wasChosen = card.isChosen
        val selectionCount = game.selectedCount
        game.chooseCardAtIndex(index)
        if (selectionCount == game.matchSize - 1 && !wasChosen) attempts++
        feedback = when {
            game.isComplete -> R.string.game_won_status
            game.hasPendingMismatch -> R.string.game_mismatch
            card.isMatched -> R.string.game_match
            wasChosen && game.selectedCount == 0 -> R.string.game_ready
            game.selectedCount == 2 -> R.string.game_pick_third
            else -> R.string.game_pick_second
        }
        updateResult()
        render()
        if (game.hasPendingMismatch) {
            handler.removeCallbacks(hideMismatch)
            handler.postDelayed(hideMismatch, MISMATCH_PREVIEW_MS)
        }
        if (game.isComplete) showResult()
    }

    private fun render(animateChanges: Boolean = true) {
        if (animateChanges) cardAdapter.refreshCards()
        else cardAdapter.notifyDataSetChanged()
        binding.tvScore.text = getString(R.string.score_value, game.score)
        binding.tvPairs.text = getString(R.string.pairs_value, game.matchedGroups, game.cardCount / game.matchSize)
        binding.tvAttempts.text = getString(R.string.attempts_value, attempts)
        binding.tvFeedback.setText(feedback)
    }

    private fun updateResult() {
        bestScore = maxOf(bestScore, game.score)
        setResult(Activity.RESULT_OK, Intent().putExtra(getString(R.string.extra_high_score), bestScore))
    }

    private fun showResult() {
        if (isFinishing || isDestroyed || resultDialog?.isShowing == true) return
        resultShown = true
        resultDialog = MaterialAlertDialogBuilder(this)
            .setTitle(R.string.game_win)
            .setMessage(getString(R.string.game_result, game.score, bestScore, attempts))
            .setPositiveButton(R.string.game_again) { _, _ -> startNewGame() }
            .setNegativeButton(R.string.btn_exit) { _, _ -> finishGame() }
            .show()
    }

    private fun finishGame() {
        handler.removeCallbacksAndMessages(null)
        updateResult()
        finish()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable(getString(R.string.state_game), game)
        outState.putInt(getString(R.string.state_attempts), attempts)
        outState.putInt(getString(R.string.state_best), bestScore)
        outState.putInt(getString(R.string.state_feedback), feedback)
        outState.putBoolean(getString(R.string.state_result_shown), resultShown)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        resultDialog?.dismiss()
        super.onDestroy()
    }

    companion object {
        private const val CARD_COUNT = 24
        private const val MISMATCH_PREVIEW_MS = 600L
    }
}
