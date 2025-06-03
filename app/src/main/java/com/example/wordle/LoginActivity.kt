package com.example.wordle

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.wordle.databinding.ActivityLoginBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth

    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

        binding =ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //Firebase Authentication başlat,
        auth= FirebaseAuth.getInstance()
        binding.forgotPassword.setOnClickListener{
           forgotPassword(view)
        }



    }
    fun login1Button(view: View){
        val email=binding.loginEmail.text.toString()
        val password=binding.loginPassword.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            toast("Lütfen tüm alanları doldurun")
            return
        }
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener { task->

            if (task.isSuccessful){
                toast("Giriş Başarılı")
                startActivity(Intent(this,HomePage::class.java))
                finish()
            }
            else {
                toast("Hata: ${task.exception?.localizedMessage}")
            }
        }
    }
    private fun toast(mesaj: String){
        Toast.makeText(this,mesaj,Toast.LENGTH_SHORT).show()
    }
    /*fun KayitOl(view: View){
        startActivity(Intent(this, Register::class.java))
    }*/

    fun forgotPassword(view: View){
        startActivity(Intent(this,ForgotPassword::class.java))
    }


}