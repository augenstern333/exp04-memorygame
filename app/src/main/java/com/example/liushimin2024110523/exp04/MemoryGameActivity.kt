package com.example.liushimin2024110523.exp04

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.GridLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.liushimin2024110523.exp04.databinding.ActivityMemoryGameBinding

/**
 * 翻牌游戏 Activity —— MVC 模式
 *
 * Model: Card / Deck / CardMatchingGame（参考附件代码实现）
 * View:  GridLayout 中的 Button 扑克牌 + 分数状态栏
 * Controller: MemoryGameActivity（接收点击、调用Model、同步View）
 */
class MemoryGameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMemoryGameBinding

    // Model 层：游戏核心逻辑（来自附件参考代码）
    private lateinit var game: CardMatchingGame
    private val cardCount = 12  // 12张牌

    private val handler = Handler(Looper.getMainLooper())
    private var isProcessing = false  // 配对动画期间锁定

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMemoryGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startNewGame()

        // 重新开始按钮
        binding.btnRestart.setOnClickListener {
            startNewGame()
        }
    }

    /** 开始一局新游戏 */
    private fun startNewGame() {
        game = CardMatchingGame(cardCount)
        isProcessing = false
        buildCardViews()
        updateScoreDisplay()
    }

    /** View层：根据Model生成12张扑克牌按钮 */
    private fun buildCardViews() {
        binding.gridCards.removeAllViews()
        val margin = dpToPx(6)

        for (i in 0 until cardCount) {
            val card = game.cardAtIndex(i)
            val btn = Button(this).apply {
                text = if (card.isChosen || card.isMatched) card.toString() else "?"
                textSize = 24f
                setBackgroundColor(getCardColor(card))
                setTextColor(getCardTextColor(card))
                setOnClickListener { onCardClicked(i) }
            }

            val params = GridLayout.LayoutParams().apply {
                width = 0
                height = 0
                columnSpec = GridLayout.spec(i % 4, 1, 1f)
                rowSpec = GridLayout.spec(i / 4, 1, 1f)
                this.setMargins(margin, margin, margin, margin)
            }
            binding.gridCards.addView(btn, params)
        }
    }

    /** 根据卡片状态返回背景色 */
    private fun getCardColor(card: Card): Int {
        return when {
            card.isMatched -> getColor(R.color.card_matched_color)  // 配对成功=绿色
            card.isChosen -> getColor(R.color.card_face_color)       // 已翻开=黄色
            else -> getColor(R.color.card_back_color)               // 背面=蓝色
        }
    }

    /** 根据卡片状态返回文字颜色 */
    private fun getCardTextColor(card: Card): Int {
        return if (card.isChosen || card.isMatched) Color.BLACK else Color.WHITE
    }

    /** Controller层：卡片被点击 */
    private fun onCardClicked(index: Int) {
        if (isProcessing) return
        val card = game.cardAtIndex(index)
        if (card.isMatched) return

        // 点击前：先数有几张已翻开但未配对的牌
        var chosenBefore = 0
        for (i in 0 until cardCount) {
            if (game.cardAtIndex(i).isChosen && !game.cardAtIndex(i).isMatched) {
                chosenBefore++
            }
        }

        // 调用Model处理
        game.chooseCardAtIndex(index)

        // 只有当点击前已经有1张翻开的牌（即这是第二次翻牌），才需要判断是否配对失败
        val needDelayFlipBack = if (chosenBefore == 1) {
            // 第二次翻牌后，检查两张是否配对成功
            // 配对成功：两张都matched
            // 配对失败：只有当前这张是chosen，另一张被翻回false了
            var chosenAfter = 0
            for (i in 0 until cardCount) {
                if (game.cardAtIndex(i).isChosen && !game.cardAtIndex(i).isMatched) {
                    chosenAfter++
                }
            }
            chosenAfter == 1  // 只有一张翻开 = 配对失败，需要延迟翻回
        } else {
            false  // 第一次翻牌，不需要延迟
        }

        // 刷新所有卡片显示
        refreshAllCards()
        updateScoreDisplay()

        if (needDelayFlipBack) {
            isProcessing = true
            handler.postDelayed({
                refreshAllCards()
                isProcessing = false
            }, 800)
        }

        // 检查游戏是否结束（全部配对成功）
        if (isGameWon()) {
            handler.postDelayed({
                Toast.makeText(this, "恭喜赢了！得分: ${game.score}", Toast.LENGTH_LONG).show()
                val resultIntent = Intent().apply {
                    putExtra(getString(R.string.extra_high_score), game.score)
                }
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }, 500)
        }
    }

    /** 检查游戏是否胜利 */
    private fun isGameWon(): Boolean {
        for (i in 0 until cardCount) {
            if (!game.cardAtIndex(i).isMatched) return false
        }
        return true
    }

    /** 刷新所有卡片的显示 */
    private fun refreshAllCards() {
        for (i in 0 until cardCount) {
            val btn = binding.gridCards.getChildAt(i) as? Button ?: continue
            val card = game.cardAtIndex(i)
            btn.text = if (card.isChosen || card.isMatched) card.toString() else "?"
            btn.setBackgroundColor(getCardColor(card))
            btn.setTextColor(getCardTextColor(card))
        }
    }

    /** 更新分数显示 */
    private fun updateScoreDisplay() {
        binding.tvMoves.text = getString(R.string.label_score) + game.score
        var matched = 0
        for (i in 0 until cardCount) {
            if (game.cardAtIndex(i).isMatched) matched++
        }
        binding.tvPairs.text = getString(R.string.label_pairs) + "$matched/${cardCount / 2}"
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}
