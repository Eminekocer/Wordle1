package com.example.wordle

import android.os.Bundle
import android.view.View
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

        // QuizActivity'den gelen doğru ve yanlış cevap sayılarını al
        // Eğer bu veriler gelmezse (örneğin doğrudan bu aktiviteye gelinirse), varsayılan olarak 0 kullanılır.
        val correctAnswers = intent.getIntExtra("CORRECT_ANSWERS", 0)
        val incorrectAnswers = intent.getIntExtra("INCORRECT_ANSWERS", 0)

        // Bu verileri UI'da göster
        // Not: Bu TextView'ların (tvCorrectAnswers ve tvIncorrectAnswers)
        // activity_analyst.xml dosyanızda tanımlı olması gerekmektedir.
        binding.tvCorrectAnswers.text = "Bu Oturumda Doğru Cevaplar: $correctAnswers"
        binding.tvIncorrectAnswers.text = "Bu Oturumda Yanlış Cevaplar: $incorrectAnswers"

        kullaniciVerisiniYukle() // Diğer kullanıcı verilerini yükleme fonksiyonu
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


                } else {
                    Toast.makeText(this, "Kullanıcı verisi bulunamadı", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Veri alınamadı: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
