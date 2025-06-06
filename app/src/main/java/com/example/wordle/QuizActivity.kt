package com.example.wordle

import android.content.Intent
import android.os.Bundle
import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.wordle.HomePage
import com.example.wordle.WordList
import com.example.wordle.databinding.ActivityQuizBinding

class QuizActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQuizBinding

    private val wordList = WordList.words

    private val progressMap: MutableMap<String, WordProgress> = mutableMapOf()

    private val reviewIntervals = listOf(
        0L,
        1 * 24 * 60 * 60 * 1000L,
        7 * 24 * 60 * 60 * 1000L,
        30 * 24 * 60 * 60 * 1000L,
        90 * 24 * 60 * 60 * 1000L,
        180 * 24 * 60 * 60 * 1000L,
        365 * 24 * 60 * 60 * 1000L
    )

    private var quizWords = listOf<Word>()
    private var currentIndex = 0
    private lateinit var currentWord: Word
    private lateinit var correctAnswer: String

    // günlük maksimum gösterilecek kelime sayısı
    private val maxDailyWords = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeProgress()
        quizWords = getTodaysQuizWords().take(maxDailyWords)
        showNextQuestion()
        binding.outImage.setOnClickListener{
            startActivity(Intent(this, HomePage::class.java))
        }

        val optionButtons = listOf(
            binding.option1,
            binding.option2,
            binding.option3,
            binding.option4
        )

        optionButtons.forEach { button ->
            button.setOnClickListener {
                checkAnswer(button.text.toString())
            }
        }

        binding.nextImage.setOnClickListener {
            currentIndex++
            if (currentIndex < quizWords.size) {
                showNextQuestion()
            } else {
                quizWords = getTodaysQuizWords().take(maxDailyWords)
                currentIndex = 0
                if (quizWords.isEmpty()) {
                    Toast.makeText(this, "Bugün için tekrar edilecek kelime yok!", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    showNextQuestion()
                }
            }
        }
    }

    private fun initializeProgress() {
        wordList.forEach { word ->
            if (!progressMap.containsKey(word.createdBy)) {
                progressMap[word.createdBy] = WordProgress(word.createdBy, 0, 0L, mutableListOf())
            }
        }
    }

    private fun isEligibleForReview(progress: WordProgress): Boolean {
        if (progress.history.size >= 6) return false
        val now = System.currentTimeMillis()
        val requiredDelay = reviewIntervals.getOrElse(progress.history.size) { Long.MAX_VALUE }
        val lastTime = progress.history.lastOrNull() ?: 0L
        return now - lastTime >= requiredDelay
    }

    private fun getTodaysQuizWords(): List<Word> {
        return wordList.filter { word ->
            val progress = progressMap[word.createdBy] ?: return@filter false
            isEligibleForReview(progress)
        }
    }

    private fun showNextQuestion() {
        binding.feedbackText.text = ""
        binding.nextImage.visibility = View.GONE

        if (quizWords.isEmpty()) {
            Toast.makeText(this, "Bugün için tekrar edilecek kelime yok!", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        currentWord = quizWords[currentIndex]
        binding.questionText.text = currentWord.engWord
        binding.questionImage.setImageResource(currentWord.imageResId)
        binding.questionText1.text = "Question ${currentIndex + 1}/$maxDailyWords"

        correctAnswer = currentWord.turWord

        val options = generateOptions(correctAnswer)
        val optionButtons = listOf(
            binding.option1,
            binding.option2,
            binding.option3,
            binding.option4
        )

        optionButtons.forEachIndexed { index, button ->
            button.text = options[index]
            button.setBackgroundColor(Color.LTGRAY)
            button.isEnabled = true
        }
    }

    private fun checkAnswer(selected: String) {
        val isCorrect = selected == correctAnswer

        val progress = progressMap[currentWord.createdBy] ?: return

        if (isCorrect) {
            binding.feedbackText.text = "Doğru!"
            progress.history.add(System.currentTimeMillis())
            if (progress.history.size > 6) progress.history.drop(progress.history.size - 6)
        } else {
            binding.feedbackText.text = "Yanlış! Doğru cevap: $correctAnswer"
            progress.history.clear()
        }

        val optionButtons = listOf(
            binding.option1,
            binding.option2,
            binding.option3,
            binding.option4
        )

        optionButtons.forEach { button ->
            button.isEnabled = false
            if (button.text == correctAnswer) {
                button.setBackgroundColor(Color.GREEN)
            } else if (button.text == selected) {
                button.setBackgroundColor(Color.RED)
            }
        }

        binding.nextImage.visibility = View.VISIBLE
    }

    private fun generateOptions(correct: String): List<String> {
        val options = mutableSetOf(correct)
        while (options.size < 4) {
            val randomOption = wordList.random().turWord
            options.add(randomOption)
        }
        return options.shuffled()
    }
}