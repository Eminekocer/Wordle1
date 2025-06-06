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

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnalystBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)



        kullaniciVerisiniYukle()
    }

    private fun kullaniciVerisiniYukle() {
        val uid = auth.currentUser?.uid ?: return

        firestore.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                if (doc != null && doc.exists()) {
                    val isim = doc.getString("userName") ?: "Kullanıcı"
                    val science = doc.get("science").toString().toDoubleOrNull() ?: 0.0
                    val history = doc.getDouble("history") .toString().toDoubleOrNull()?: 0.0
                    val art = doc.getDouble("art") .toString().toDoubleOrNull()?: 0.0
                    val sport = doc.getDouble("sport").toString().toDoubleOrNull() ?: 0.0
                    val basari = doc.getDouble("basariYuzdesi") ?: 0.0

                    binding.tvUserName.text = isim
                    binding.tvScience.text = "Science: %${science.toInt()}"
                    binding.tvHistory.text = "History: %${history.toInt()}"
                    binding.tvArt.text = "Art: %${art.toInt()}"
                    binding.tvSport.text = "Sport: %${sport.toInt()}"
                    binding.progressBar.progress = basari.toInt()

                } else {
                    Toast.makeText(this, "Kullanıcı verisi bulunamadı", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Veri alınamadı: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
