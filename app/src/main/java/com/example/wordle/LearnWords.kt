package com.cananorek.kelimeezberoyunu

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.cananorek.kelimeezberoyunu.databinding.ActivityAnalizRaporuBinding
import com.cananorek.kelimeezberoyunu.databinding.ActivityKelimeOgrenBinding

class LearnWords : AppCompatActivity() {
    private lateinit var binding: ActivityKelimeOgrenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKelimeOgrenBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
}