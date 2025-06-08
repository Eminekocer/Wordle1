package com.example.wordle

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.wordle.databinding.ActivityLearnWordsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentSnapshot



class LearnWords : AppCompatActivity() {

        private var maxDailyWords: Int = 10

        private lateinit var binding: ActivityLearnWordsBinding
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = ActivityLearnWordsBinding.inflate(layoutInflater)
            val view = binding.root
            setContentView(view)
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            binding.imageView10.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }

            if (uid != null) {
                FirebaseFirestore.getInstance().collection("Users").document(uid).get()
                    .addOnSuccessListener { documentSnapshot ->
                        val count = documentSnapshot.getLong("kelimeSayisi")?.toInt() ?: 10
                        maxDailyWords = count
                        startLearningWithLimit()
                    }
                    .addOnFailureListener {
                        maxDailyWords = 10
                        startLearningWithLimit()
                    }
            } else {
                maxDailyWords = 10
                startLearningWithLimit()
            }
        }

        private fun startLearningWithLimit() {
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            if (uid != null) {
                FirebaseFirestore.getInstance().collection("Kelime")
                    .whereEqualTo("userID", uid)
                    .limit(maxDailyWords.toLong())
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        val wordList = mutableListOf<Word>()
                        for (document in querySnapshot) {
                            val word = document.toObject(Word::class.java)
                            wordList.add(word)
                        }

                        // Burada kelimeleri gösterme işlemleri yapılır
                    }
            }

        }
    }
