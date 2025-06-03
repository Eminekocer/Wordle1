package com.example.wordle.Activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.wordle.LoginActivity
import com.example.wordle.SignupActivity
import com.example.wordle.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.signupButton.setOnClickListener{
            startActivity(Intent(this,SignupActivity::class.java))
        }
        binding.loginButton.setOnClickListener{
            startActivity(Intent(this,LoginActivity::class.java))
        }
    }
}