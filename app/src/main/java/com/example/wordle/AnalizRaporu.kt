package com.cananorek.kelimeezberoyunu

import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.cananorek.kelimeezberoyunu.databinding.ActivityAnalizRaporuBinding
import com.cananorek.kelimeezberoyunu.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AnalizRaporu : AppCompatActivity() {
    private lateinit var binding: ActivityAnalizRaporuBinding

    private lateinit var tvKullaniciAdi: TextView
    private lateinit var tvScience: TextView
    private lateinit var tvHistory: TextView
    private lateinit var tvArt: TextView
    private lateinit var tvSport: TextView
    private lateinit var progressBar: ProgressBar

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnalizRaporuBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        tvKullaniciAdi = findViewById(R.id.textView12)
        tvScience = findViewById(R.id.textView16)
        tvHistory = findViewById(R.id.textView17)
        tvArt = findViewById(R.id.textView18)
        tvSport = findViewById(R.id.textView19)
        progressBar = findViewById(R.id.ProgressBar)

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

                tvKullaniciAdi.text = isim
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
