package com.example.liushimin2024110523.exp04

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.example.liushimin2024110523.exp04.databinding.ActivityEditInfoBinding

class EditInfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        configureSystemBars(binding.root)

        // Android restores the input views automatically after a configuration change.
        if (savedInstanceState == null) populateCurrentInfo()

        binding.etName.doAfterTextChanged { binding.inputName.error = null }
        binding.etStudentId.doAfterTextChanged { binding.inputStudentId.error = null }
        binding.etPhone.doAfterTextChanged { binding.inputPhone.error = null }
        binding.etEmail.doAfterTextChanged { binding.inputEmail.error = null }
        binding.btnSave.pressFeedback()
        binding.btnCancel.pressFeedback()
        binding.btnBack.pressFeedback()
        binding.btnSave.setOnClickListener { saveInfo() }
        binding.btnCancel.setOnClickListener { cancelEdit() }
        binding.btnBack.setOnClickListener { cancelEdit() }
    }

    private fun populateCurrentInfo() {
        binding.etName.setText(intent.getStringExtra(getString(R.string.extra_name)).orEmpty())
        binding.etStudentId.setText(intent.getStringExtra(getString(R.string.extra_student_id)).orEmpty())
        binding.etPhone.setText(intent.getStringExtra(getString(R.string.extra_phone)).orEmpty())
        binding.etEmail.setText(intent.getStringExtra(getString(R.string.extra_email)).orEmpty())
        when (intent.getStringExtra(getString(R.string.extra_gender))) {
            getString(R.string.gender_male) -> binding.rbMale.isChecked = true
            getString(R.string.gender_female) -> binding.rbFemale.isChecked = true
        }
        val hobbies = intent.getStringExtra(getString(R.string.extra_hobby)).orEmpty()
            .split(getString(R.string.hobby_separator)).toSet()
        binding.cbTravel.isChecked = getString(R.string.hobby_travel) in hobbies
        binding.cbReading.isChecked = getString(R.string.hobby_reading) in hobbies
        binding.cbGame.isChecked = getString(R.string.hobby_game) in hobbies
        binding.cbShopping.isChecked = getString(R.string.hobby_shopping) in hobbies
    }

    private fun saveInfo() {
        val name = binding.etName.text.toString().trim()
        val studentId = binding.etStudentId.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        binding.inputName.error = if (name.isBlank()) getString(R.string.error_name_required) else null
        binding.inputStudentId.error = if (studentId.isBlank()) {
            getString(R.string.error_student_id_required)
        } else null
        binding.inputPhone.error = if (phone.isNotBlank() &&
            (!Patterns.PHONE.matcher(phone).matches() || phone.count(Char::isDigit) < 7)
        ) getString(R.string.error_phone_invalid) else null
        binding.inputEmail.error = if (email.isNotBlank() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            getString(R.string.error_email_invalid)
        } else null

        val invalidInput = listOf(
            binding.inputName to binding.etName,
            binding.inputStudentId to binding.etStudentId,
            binding.inputPhone to binding.etPhone,
            binding.inputEmail to binding.etEmail
        ).firstOrNull { (container, _) -> container.error != null }
        if (invalidInput != null) {
            invalidInput.second.requestFocus()
            return
        }

        val selectedGender = when (binding.rgGender.checkedRadioButtonId) {
            binding.rbMale.id -> getString(R.string.gender_male)
            binding.rbFemale.id -> getString(R.string.gender_female)
            else -> ""
        }
        val hobbies = buildList {
            if (binding.cbTravel.isChecked) add(getString(R.string.hobby_travel))
            if (binding.cbReading.isChecked) add(getString(R.string.hobby_reading))
            if (binding.cbGame.isChecked) add(getString(R.string.hobby_game))
            if (binding.cbShopping.isChecked) add(getString(R.string.hobby_shopping))
        }.joinToString(getString(R.string.hobby_separator))
        val result = Intent().apply {
            putExtra(getString(R.string.extra_name), name)
            putExtra(getString(R.string.extra_gender), selectedGender)
            putExtra(getString(R.string.extra_student_id), studentId)
            putExtra(getString(R.string.extra_phone), phone)
            putExtra(getString(R.string.extra_email), email)
            putExtra(getString(R.string.extra_hobby), hobbies)
        }
        setResult(Activity.RESULT_OK, result)
        finish()
    }

    private fun cancelEdit() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }
}
