package com.cananorek.kelimeezberoyunu

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.cananorek.kelimeezberoyunu.databinding.ActivityAnalizRaporuBinding
import com.cananorek.kelimeezberoyunu.databinding.ActivityAyarlarBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class Ayarlar : AppCompatActivity() {
    private lateinit var binding: ActivityAyarlarBinding
    private lateinit var etKelimeSayisi: EditText
    private lateinit var prefs: SharedPreferences
    private val firestore= FirebaseFirestore.getInstance()
    private val auth= FirebaseAuth.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAyarlarBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        etKelimeSayisi=findViewById(R.id.etKelimeSayisi)
        val btnKaydet= findViewById<Button>(R.id.btnKaydet)

        //Firebaseden ve localden veriyi getir
        veriyiYukle()
        btnKaydet.setOnClickListener {
            val girilenSayi= etKelimeSayisi.text.toString().toIntOrNull()
            if (girilenSayi!=null && girilenSayi>0){
                kaydet(girilenSayi)
            }else{
                Toast.makeText(this,"Geçerli bir sayı girin", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun veriyiYukle(){
        val uid= auth.currentUser?.uid?: return
        firestore.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                val girilenSayi= doc.getLong("kelimeSayisi")?.toInt()
                if(girilenSayi!= null){
                    etKelimeSayisi.setText(girilenSayi.toString())
                }
            }
            .addOnFailureListener{
                Toast.makeText(this,"Veri alınamadı", Toast.LENGTH_SHORT).show()
            }
    }
    private fun kaydet(girilenSayi: Int) {
        val uid = auth.currentUser?.uid ?: return
        val veri = mapOf("kelimeSayisi" to girilenSayi)
        firestore.collection("users").document(uid)
            .set(veri,SetOptions.merge())
            .addOnSuccessListener {
                Toast.makeText(this, "Veriler kaydedildi", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Veriler kaydedilemedi", Toast.LENGTH_SHORT).show()

            }
            }
    }
