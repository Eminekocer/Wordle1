package com.example.wordle

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.wordle.Activity.MainActivity
import com.example.wordle.databinding.ActivityHomePageBinding
import com.example.wordle.QuizActivity

class HomePage : AppCompatActivity() {
    private lateinit var binding : ActivityHomePageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =ActivityHomePageBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.createQuiz.setOnClickListener{createQuiz(view)}
        binding.addWord.setOnClickListener{addWord(view)}
        binding.analysisReport.setOnClickListener{analysisReport(view)}
        binding.settingsButton.setOnClickListener{settingsButton(view)}
        binding.wordle.setOnClickListener{wordle(view)}
        binding.learnWords.setOnClickListener{learnWords(view)}



    }
    fun learnWords(view: View){
        startActivity(Intent(this,LearnWords::class.java))
    }
    fun addWord(view: View){
        startActivity(Intent(this,AddWord::class.java))
    }
    fun wordle(view: View){
        startActivity(Intent(this,Wordle::class.java))
    }
    fun createQuiz(view: View){
        startActivity(Intent(this,QuizActivity::class.java))
    }
    fun analysisReport(view: View){
        startActivity(Intent(this,AnalystActivity::class.java))
    }
    fun settingsButton(view: View){
        startActivity(Intent(this,SettingsActivity::class.java))
    }
}
