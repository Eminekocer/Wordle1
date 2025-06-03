package com.example.wordle

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.wordle.databinding.ActivityLearnWordsBinding


class LearnWords : AppCompatActivity() {

    class LearnWords : AppCompatActivity() {
        private lateinit var binding: ActivityLearnWordsBinding
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = ActivityLearnWordsBinding.inflate(layoutInflater)
            val view = binding.root
            setContentView(view)
        }
    }
}