package com.example.liushimin2024110523.exp04

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.liushimin2024110523.exp04.databinding.ActivityEditInfoBinding

class EditInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 接收从 MainActivity 传来的当前信息，填入输入框
        val name = intent.getStringExtra(getString(R.string.extra_name)) ?: ""
        val gender = intent.getStringExtra(getString(R.string.extra_gender)) ?: ""
        val studentId = intent.getStringExtra(getString(R.string.extra_student_id)) ?: ""
        val phone = intent.getStringExtra(getString(R.string.extra_phone)) ?: ""
        val email = intent.getStringExtra(getString(R.string.extra_email)) ?: ""
        val hobby = intent.getStringExtra(getString(R.string.extra_hobby)) ?: ""

        binding.etName.setText(name)
        binding.etStudentId.setText(studentId)
        binding.etPhone.setText(phone)
        binding.etEmail.setText(email)

        // 性别单选按钮
        if (gender == "男") {
            binding.rbMale.isChecked = true
        } else if (gender == "女") {
            binding.rbFemale.isChecked = true
        }

        // 爱好复选框
        binding.cbTravel.isChecked = hobby.contains("旅游")
        binding.cbReading.isChecked = hobby.contains("阅读")
        binding.cbGame.isChecked = hobby.contains("游戏")
        binding.cbShopping.isChecked = hobby.contains("购物")

        // 保存按钮：把修改后的信息返回给 MainActivity
        binding.btnSave.setOnClickListener {
            // 收集性别
            val selectedGenderId = binding.rgGender.checkedRadioButtonId
            val selectedGender = if (selectedGenderId == binding.rbMale.id) {
                getString(R.string.gender_male)
            } else if (selectedGenderId == binding.rbFemale.id) {
                getString(R.string.gender_female)
            } else {
                ""
            }

            // 收集爱好
            val hobbies = mutableListOf<String>()
            if (binding.cbTravel.isChecked) hobbies.add(getString(R.string.hobby_travel))
            if (binding.cbReading.isChecked) hobbies.add(getString(R.string.hobby_reading))
            if (binding.cbGame.isChecked) hobbies.add(getString(R.string.hobby_game))
            if (binding.cbShopping.isChecked) hobbies.add(getString(R.string.hobby_shopping))
            val hobbyStr = hobbies.joinToString("、")

            val resultIntent = Intent().apply {
                putExtra(getString(R.string.extra_name), binding.etName.text.toString().trim())
                putExtra(getString(R.string.extra_gender), selectedGender)
                putExtra(getString(R.string.extra_student_id), binding.etStudentId.text.toString().trim())
                putExtra(getString(R.string.extra_phone), binding.etPhone.text.toString().trim())
                putExtra(getString(R.string.extra_email), binding.etEmail.text.toString().trim())
                putExtra(getString(R.string.extra_hobby), hobbyStr)
            }
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }

        // 取消按钮：不保存直接返回
        binding.btnCancel.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }
}
