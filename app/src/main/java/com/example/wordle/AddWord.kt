package com.example.wordle

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.wordle.databinding.ActivityAddWordBinding
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

// Veri modeli
data class Word(
    val createdBy: String = "",
    val engWord: String = "",
    val turWord: String = "",
    val imageResId: Int = 0,
    val correctCount: Int = 0,
    val nextReviewDate: Timestamp = Timestamp.now(),
    val lastCorrectDate: Timestamp = Timestamp.now(),
    val reviewHistory: List<Int> = listOf()
)

data class Sample(
    val sentence: String = ""
)

class AddWord : AppCompatActivity() {
    private lateinit var binding: ActivityAddWordBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddWordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()

        binding.imageView10.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnAddWord.setOnClickListener {
            val engWord = binding.editTextEnglish.text.toString().trim()
            val turWord = binding.editTextTurkish.text.toString().trim()
            val sentence = binding.editTextSample.text.toString().trim()
            val userId = auth.currentUser?.uid

            if (userId == null) {
                Toast.makeText(this, "Giriş yapmadan kelime ekleyemezsiniz!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (engWord.isNotEmpty() && turWord.isNotEmpty()) {
                // Bugünün tarihi
                val today = Timestamp.now()

                // Yarının tarihi (ilk tekrar için)
                val calendar = Calendar.getInstance()
                calendar.add(Calendar.DAY_OF_YEAR, 1)
                val nextReviewDate = Timestamp(calendar.time)

                val word = Word(
                    engWord = engWord,
                    turWord = turWord,
                    createdBy = userId,
                    correctCount = 0,
                    nextReviewDate = nextReviewDate,
                    lastCorrectDate = today,
                    reviewHistory = listOf()
                )

                // Firestore: Users/{userId}/words
                db.collection("Users")
                    .document(userId)
                    .collection("words")
                    .add(word)
                    .addOnSuccessListener { wordRef ->
                        Log.d("Firestore", "Kelime eklendi: ${wordRef.id}")

                        if (sentence.isNotEmpty()) {
                            val sample = Sample(sentence)
                            wordRef.collection("samples")
                                .add(sample)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Kelime ve örnek cümle eklendi!", Toast.LENGTH_SHORT).show()
                                    clearInputFields()
                                }
                                .addOnFailureListener { e ->
                                    Log.e("Firestore", "Örnek cümle eklenemedi", e)
                                    Toast.makeText(this, "Kelime eklendi ama örnek cümle eklenemedi!", Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            Toast.makeText(this, "Kelime eklendi, örnek cümle boş bırakıldı.", Toast.LENGTH_SHORT).show()
                            clearInputFields()
                        }

                    }
                    .addOnFailureListener { e ->
                        Log.e("Firestore", "Kelime eklenemedi", e)
                        Toast.makeText(this, "Kelime eklenemedi!", Toast.LENGTH_SHORT).show()
                    }

            } else {
                Toast.makeText(this, "İngilizce ve Türkçe alanları boş olamaz!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun clearInputFields() {
        binding.editTextEnglish.editableText.clear()
        binding.editTextTurkish.editableText.clear()
        binding.editTextSample.text.clear()
    }
}
