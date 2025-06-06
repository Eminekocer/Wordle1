package com.example.wordle

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.wordle.databinding.ActivityAddWordBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// Veri modeli
data class Word(
    val engWord: String = "",
    val turWord: String = "",
    val createdBy: String = "",
    val imageResId: Int = 0
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
                val word = Word(
                    engWord = engWord,
                    turWord = turWord,
                    createdBy = userId
                )

                // "words" koleksiyonuna kelime ekle
                db.collection("words")
                    .add(word)
                    .addOnSuccessListener { wordRef ->
                        Log.d("Firestore", "Kelime eklendi: ${wordRef.id}")

                        // Örnek cümle eklenmişse alt koleksiyona ekle
                        if (sentence.isNotEmpty()) {
                            val sample = Sample(sentence)
                            wordRef.collection("samples")
                                .add(sample)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Kelime ve örnek cümle eklendi!", Toast.LENGTH_SHORT).show()
                                    clearInputFields() // Formu temizle
                                }
                                .addOnFailureListener { e ->
                                    Log.e("Firestore", "Örnek cümle eklenemedi", e)
                                }
                        } else {
                            Toast.makeText(this, "Kelime eklendi, örnek cümle boş bırakıldı.", Toast.LENGTH_SHORT).show()
                            clearInputFields() // Formu temizle
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

    // input alanlarını temizle
    private fun clearInputFields() {
        binding.editTextEnglish.editableText.clear()
        binding.editTextTurkish.editableText.clear()
        binding.editTextSample.text.clear()
    }
}
