package com.cananorek.kelimeezberoyunu

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.cananorek.kelimeezberoyunu.databinding.ActivityAnalizRaporuBinding
import com.cananorek.kelimeezberoyunu.databinding.ActivityExamBinding

class Exam : AppCompatActivity() {
    private lateinit var binding: ActivityExamBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExamBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
}