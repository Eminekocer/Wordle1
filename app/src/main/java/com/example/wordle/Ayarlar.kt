package com.cananorek.kelimeezberoyunu

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.cananorek.kelimeezberoyunu.databinding.ActivityAnalizRaporuBinding
import com.cananorek.kelimeezberoyunu.databinding.ActivityAyarlarBinding

class Ayarlar : AppCompatActivity() {
    private lateinit var binding: ActivityAyarlarBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAyarlarBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
}