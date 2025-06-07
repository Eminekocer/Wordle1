package com.example.wordle

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.wordle.databinding.ActivityQuizBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class QuizActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQuizBinding

    private val firestore = FirebaseFirestore.getInstance()
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

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
    private var maxDailyWords = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentUserId?.let { uid ->
            firestore.collection("Users").document(uid).get()
                .addOnSuccessListener { documentSnapshot ->
                    val count = documentSnapshot.getLong("kelimeSayisi")?.toInt() ?: 10
                    maxDailyWords = count
                    loadProgressFromFirestore()
                }
                .addOnFailureListener {
                    maxDailyWords = 10
                    loadProgressFromFirestore()
                }
        } ?: run {
            maxDailyWords = 10
            loadProgressFromFirestore()
        }

        binding.outImage.setOnClickListener {
            startActivity(Intent(this, HomePage::class.java))
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

        val optionButtons = listOf(
            binding.option1, binding.option2, binding.option3, binding.option4
        )

        optionButtons.forEach { button ->
            button.setOnClickListener {
                checkAnswer(button.text.toString())
            }
        }
    }

    private fun startQuizWithLimit() {
        quizWords = getTodaysQuizWords().take(maxDailyWords)
        if (quizWords.isEmpty()) {
            Toast.makeText(this, "Bugün için tekrar edilecek kelime yok!", Toast.LENGTH_LONG).show()
            finish()
        } else {
            showNextQuestion()
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

        currentWord = quizWords[currentIndex]
        correctAnswer = currentWord.turWord

        binding.questionText.text = currentWord.engWord
        binding.questionImage.setImageResource(currentWord.imageResId)
        binding.questionText1.text = "Question ${currentIndex + 1}/$maxDailyWords"

        val options = generateOptions(correctAnswer)
        val optionButtons = listOf(
            binding.option1, binding.option2, binding.option3, binding.option4
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
            if (progress.history.size > 6) {
                progress.history = progress.history.takeLast(6).toMutableList()
            }
        } else {
            binding.feedbackText.text = "Yanlış! Doğru cevap: $correctAnswer"
            progress.history.clear()
        }

        saveProgressToFirestore(currentWord.createdBy, progress)

        val optionButtons = listOf(
            binding.option1, binding.option2, binding.option3, binding.option4
        )

        optionButtons.forEach { button ->
            button.isEnabled = false
            button.setBackgroundColor(
                when (button.text) {
                    correctAnswer -> Color.GREEN
                    selected -> Color.RED
                    else -> Color.LTGRAY
                }
            )
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

    private fun loadProgressFromFirestore() {
        if (currentUserId == null) {
            initializeProgress()
            startQuizWithLimit()
            return
        }

        firestore.collection("Users")
            .document(currentUserId)
            .collection("WordProgress")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val progress = document.toObject(WordProgress::class.java)
                    progressMap[progress.wordId] = progress
                }
                initializeProgress()
                startQuizWithLimit()
            }
            .addOnFailureListener {
                initializeProgress()
                startQuizWithLimit()
            }
    }

    private fun saveProgressToFirestore(wordId: String, progress: WordProgress) {
        if (currentUserId == null) return

        firestore.collection("Users")
            .document(currentUserId)
            .collection("WordProgress")
            .document(wordId)
            .set(progress)
    }
}