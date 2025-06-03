package com.cananorek.kelimeezberoyunu

import android.content.Intent
import android.os.Bundle
import android.os.Message
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.cananorek.kelimeezberoyunu.databinding.ActivityRegisterBinding

class Register : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
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


    fun KayitOl(view: View) {
            val nameText = binding.nameText.text.toString()
            val surnameText = binding.surnameText.text.toString()
            val emailText = binding.emailText.text.toString()
            val passwordText = binding.passwordText.text.toString()

            if (nameText.isEmpty() || passwordText.isEmpty() || surnameText.isEmpty() || emailText.isEmpty()) {
                showToast("Lütfen boş alan bırakmayınız!")
                return
            }
            if (!isValidPassword(passwordText)) {
                showToast("Şifre 8-15 karakter olmalı, en az 1 harf ve 1 rakam içermeli!")
                return
            }

            startActivity(Intent(this, MainActivity::class.java))
        }

    }
