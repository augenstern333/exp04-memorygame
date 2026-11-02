package com.example.liushimin2024110523.exp04

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.liushimin2024110523.exp04.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var playerName = ""
    private var playerGender = ""
    private var playerStudentId = ""
    private var playerPhone = ""
    private var playerEmail = ""
    private var playerHobby = ""
    private var highScore = 0

    private val editInfoLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data ?: return@registerForActivityResult
            playerName = data.getStringExtra(getString(R.string.extra_name)) ?: playerName
            playerGender = data.getStringExtra(getString(R.string.extra_gender)) ?: playerGender
            playerStudentId = data.getStringExtra(getString(R.string.extra_student_id)) ?: playerStudentId
            playerPhone = data.getStringExtra(getString(R.string.extra_phone)) ?: playerPhone
            playerEmail = data.getStringExtra(getString(R.string.extra_email)) ?: playerEmail
            playerHobby = data.getStringExtra(getString(R.string.extra_hobby)) ?: playerHobby
            refreshPlayerInfo()
            Toast.makeText(this, R.string.info_saved, Toast.LENGTH_SHORT).show()
        }
    }

    private val gameLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val score = result.data?.getIntExtra(getString(R.string.extra_high_score), 0) ?: 0
            highScore = maxOf(highScore, score)
            refreshPlayerInfo()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        configureSystemBars(binding.root)

        // Keep player information in the current task, including Activity recreation.
        playerName = savedInstanceState?.getString(getString(R.string.extra_name))
            ?: getString(R.string.default_name)
        playerGender = savedInstanceState?.getString(getString(R.string.extra_gender))
            ?: getString(R.string.default_gender)
        playerStudentId = savedInstanceState?.getString(getString(R.string.extra_student_id))
            ?: getString(R.string.default_student_id)
        playerPhone = savedInstanceState?.getString(getString(R.string.extra_phone))
            ?: getString(R.string.default_phone)
        playerEmail = savedInstanceState?.getString(getString(R.string.extra_email))
            ?: getString(R.string.default_email)
        playerHobby = savedInstanceState?.getString(getString(R.string.extra_hobby))
            ?: getString(R.string.default_hobby)
        highScore = savedInstanceState?.getInt(getString(R.string.extra_high_score))
            ?: (getString(R.string.default_high_score).toIntOrNull() ?: 0)
        refreshPlayerInfo()

        binding.btnEditInfo.pressFeedback()
        binding.btnPlayGame.pressFeedback()
        binding.btnEditInfo.setOnClickListener {
            val editIntent = Intent(this, EditInfoActivity::class.java).apply {
                putExtra(getString(R.string.extra_name), playerName)
                putExtra(getString(R.string.extra_gender), playerGender)
                putExtra(getString(R.string.extra_student_id), playerStudentId)
                putExtra(getString(R.string.extra_phone), playerPhone)
                putExtra(getString(R.string.extra_email), playerEmail)
                putExtra(getString(R.string.extra_hobby), playerHobby)
            }
            editInfoLauncher.launch(editIntent)
        }
        binding.btnPlayGame.setOnClickListener {
            val gameIntent = Intent(this, MemoryGameActivity::class.java).apply {
                putExtra(getString(R.string.extra_high_score), highScore)
            }
            gameLauncher.launch(gameIntent)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(getString(R.string.extra_name), playerName)
        outState.putString(getString(R.string.extra_gender), playerGender)
        outState.putString(getString(R.string.extra_student_id), playerStudentId)
        outState.putString(getString(R.string.extra_phone), playerPhone)
        outState.putString(getString(R.string.extra_email), playerEmail)
        outState.putString(getString(R.string.extra_hobby), playerHobby)
        outState.putInt(getString(R.string.extra_high_score), highScore)
        super.onSaveInstanceState(outState)
    }

    private fun refreshPlayerInfo() {
        val emptyValue = getString(R.string.profile_not_set)
        binding.tvName.text = playerName
        binding.tvGreeting.text = getString(R.string.welcome_player, playerName)
        binding.tvMonogram.text = playerName.takeLast(1)
        binding.tvGender.text = playerGender.ifBlank { emptyValue }
        binding.tvStudentId.text = playerStudentId
        binding.tvPhone.text = playerPhone.ifBlank { emptyValue }
        binding.tvEmail.text = playerEmail.ifBlank { emptyValue }
        binding.tvHobby.text = playerHobby.ifBlank { emptyValue }
        binding.tvHighScore.text = getString(R.string.score_value, highScore)
    }
}
