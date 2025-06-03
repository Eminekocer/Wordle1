package com.example.wordle

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.wordle.databinding.ActivitySignupBinding

class SignupActivity : AppCompatActivity() {
    private  lateinit var binding: ActivitySignupBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

    }
    private fun isValidPassword(password: String): Boolean {
        return password.length in 8..15 &&
                password.any { it.isDigit() } &&
                password.any { it.isLetter() }
    }


    fun continueButton(view: View) {
        val nameText = binding.signupName.text.toString()
        val emailText = binding.signupEmail.text.toString()
        val passwordText = binding.signupPassword.text.toString()

        if (nameText.isEmpty() || passwordText.isEmpty()  || emailText.isEmpty()) {
            showToast("Lütfen boş alan bırakmayınız!")
            return
        }
        if (!isValidPassword(passwordText)) {
            showToast("Şifre 8-15 karakter olmalı, en az 1 harf ve 1 rakam içermeli!")
            return
        }

        startActivity(Intent(this, HomePage::class.java))
    }


}
