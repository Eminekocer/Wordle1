package com.example.wordle

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.wordle.databinding.ActivityHomePageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log


class HomePage : AppCompatActivity() {
    private lateinit var binding: ActivityHomePageBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomePageBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val currentUserId = auth.currentUser?.uid

        if (currentUserId != null) {
            Log.d("Test", "Firestore sorgusu başladı.")

            db.collection("Users").document(currentUserId).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val userName = document.getString("userName")
                        Log.d("Firestore", "Kullanıcı adı Firestore'dan çekildi: $userName")

                        binding.textViewUsername.text = "Hi, $userName"
                    } else {
                        binding.textViewUsername.text = "Hi!"
                    }
                }
                .addOnFailureListener {
                    binding.textViewUsername.text = "Hi!"
                }
        }

        binding.createQuiz.setOnClickListener { createQuiz(view) }
        binding.addWord.setOnClickListener { addWord(view) }
        binding.analysisReport.setOnClickListener { analysisReport(view) }
        binding.settingsButton.setOnClickListener { settingsButton(view) }
        binding.wordle.setOnClickListener { wordle(view) }
        binding.learnWords.setOnClickListener { learnWords(view) }
    }

    fun learnWords(view: View) {
        startActivity(Intent(this, LearnWords::class.java))
    }

    fun addWord(view: View) {
        startActivity(Intent(this, AddWord::class.java))
    }

    fun wordle(view: View) {
        startActivity(Intent(this, Wordle::class.java))
    }

    fun createQuiz(view: View) {
        startActivity(Intent(this, QuizActivity::class.java))
    }

    fun analysisReport(view: View) {
        startActivity(Intent(this, AnalystActivity::class.java))
    }

    fun settingsButton(view: View) {
        startActivity(Intent(this, SettingsActivity::class.java))
    }
}
