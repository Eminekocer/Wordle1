package com.example.wordle

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Register : AppCompatActivity() {
  private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        // Firebase başlat
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val emailEditText = findViewById<EditText>(R.id.signupEmail)
        val passwordEditText = findViewById<EditText>(R.id.signupPassword)
        val usernameEditText = findViewById<EditText>(R.id.signupName)
        val registerButton = findViewById<Button>(R.id.continueButton)

        registerButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val username = usernameEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
                Toast.makeText(this, "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Firebase Authentication ile kullanıcı oluştur
            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener { result ->
                    val uid = result.user?.uid ?: return@addOnSuccessListener

                    val userMap = hashMapOf(
                        "userId" to uid,
                        "userName" to username,
                        "email" to email,
                        "puan" to 0,
                        "science" to 0.0,
                        "history" to 0.0,
                        "art" to 0.0,
                        "sport" to 0.0,
                        "basariYuzdesi" to 0.0
                    )
                    // Firestore'a kullanıcı bilgisi ekle
                    firestore.collection("Users").document(uid).set(userMap)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Kayıt başarılı!", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, LoginActivity::class.java))
                            finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Firestore hatası: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Kayıt başarısız: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}


   
