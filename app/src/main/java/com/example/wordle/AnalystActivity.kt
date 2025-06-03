package com.example.wordle

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.wordle.databinding.ActivityAnalystBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AnalystActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAnalystBinding
    private lateinit var tvUserName: TextView
    private lateinit var tvScience: TextView
    private lateinit var tvHistory: TextView
    private lateinit var tvArt: TextView
    private lateinit var tvSport: TextView
    private lateinit var progressBar: ProgressBar

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnalystBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        tvUserName = findViewById(R.id.tvUserName)
        tvScience = findViewById(R.id.tvScience)
        tvHistory = findViewById(R.id.tvHistory)
        tvArt = findViewById(R.id.tvArt)
        tvSport = findViewById(R.id.tvSport)
        progressBar = findViewById(R.id.progressBar)

        kullaniciVerisiniYukle()
    }

    private fun kullaniciVerisiniYukle() {
        val uid = auth.currentUser?.uid ?: return

        firestore.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                val isim = doc.getString("isim") ?: "Kullanıcı"
                val science = doc.getDouble("science") ?: 0.0
                val history = doc.getDouble("history") ?: 0.0
                val art = doc.getDouble("art") ?: 0.0
                val sport = doc.getDouble("sport") ?: 0.0
                val basari = doc.getDouble("basariYuzdesi") ?: 0.0

                tvUserName.text = isim
                tvScience.text = "Science: %${science.toInt()}"
                tvHistory.text = "History: %${history.toInt()}"
                tvArt.text = "Art: %${art.toInt()}"
                tvSport.text = "Sport: %${sport.toInt()}"
                progressBar.progress = basari.toInt()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Veri alınamadı", Toast.LENGTH_SHORT).show()
            }
    }
}