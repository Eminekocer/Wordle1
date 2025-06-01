package com.mertatmaca.sinavmodul

import android.os.Bundle
import android.graphics.Color
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mertatmaca.sinavmodul.databinding.ActivityQuizBinding
import com.mertatmaca.sinavmodul.Word
import com.mertatmaca.sinavmodul.WordProgress

class QuizActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQuizBinding

    private val wordList = WordList.words

    private val progressMap = mutableMapOf<Int, WordProgress>()
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

        binding.nextButton.setOnClickListener {
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
            if (!progressMap.containsKey(word.id)) {
                progressMap[word.id] = WordProgress(word.id, 0, 0L, mutableListOf())
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
            val progress = progressMap[word.id] ?: return@filter false
            isEligibleForReview(progress)
        }
    }

    private fun showNextQuestion() {
        binding.feedbackText.text = ""
        binding.nextButton.visibility = View.GONE

        if (quizWords.isEmpty()) {
            Toast.makeText(this, "Bugün için tekrar edilecek kelime yok!", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        currentWord = quizWords[currentIndex]
        binding.questionText.text = currentWord.english
        correctAnswer = currentWord.turkish

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

        val progress = progressMap[currentWord.id] ?: return

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

        binding.nextButton.visibility = View.VISIBLE
    }

    private fun generateOptions(correct: String): List<String> {
        val options = mutableSetOf(correct)
        while (options.size < 4) {
            val randomOption = wordList.random().turkish
            options.add(randomOption)
        }
        return options.shuffled()
    }
}