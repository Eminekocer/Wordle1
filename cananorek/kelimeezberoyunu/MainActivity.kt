package com.cananorek.kelimeezberoyunu

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.cananorek.kelimeezberoyunu.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
    fun butonLearnWords(view: View){
      startActivity(Intent(this,LearnWords::class.java))
    }
    fun butonAddWords(view: View){
      startActivity(Intent(this,AddWords::class.java))
    }
    fun butonPuzzle(view: View){
      startActivity(Intent(this,Puzzle::class.java))
    }
    fun butonSinav(view: View){
     startActivity(Intent(this,Exam::class.java))
    }
    fun butonAyarlar(view: View){
        startActivity(Intent(this,Ayarlar::class.java))
    }
    fun butonAnaliz(view: View){
        startActivity(Intent(this,AnalizRaporu::class.java))
    }
    }
