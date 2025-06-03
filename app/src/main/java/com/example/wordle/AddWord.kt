package com.example.wordle

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.wordle.databinding.ActivityAddWordBinding

class AddWord : AppCompatActivity() {
    private lateinit var binding: ActivityAddWordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddWordBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
}