package com.cananorek.kelimeezberoyunu

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.cananorek.kelimeezberoyunu.databinding.ActivityAddWordsBinding
import com.cananorek.kelimeezberoyunu.databinding.ActivityAnalizRaporuBinding

class AddWords : AppCompatActivity() {
    private lateinit var binding: ActivityAddWordsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddWordsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
}