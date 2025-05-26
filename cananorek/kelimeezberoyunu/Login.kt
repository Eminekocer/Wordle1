package com.cananorek.kelimeezberoyunu

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.cananorek.kelimeezberoyunu.databinding.ActivityLoginBinding
import com.cananorek.kelimeezberoyunu.databinding.ActivityRegisterBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class Login : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

        binding =ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //Firebase Authentication başlat,
        auth= FirebaseAuth.getInstance()

  

    }
    fun GirisYap(view: View){
val email=binding.emailText.text.toString()
val password=binding.passwordText.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            toast("Lütfen tüm alanları doldurun")
            return
        }
auth.signInWithEmailAndPassword(email,password).addOnCompleteListener { task->
    when{
        task.isSuccessful->{
            toast("Giriş Başarılı")
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }
        task.exception is FirebaseAuthInvalidUserException->{
            toast("Kullanıcı bulunamadı, kayıt ekranına yönlendiriliyorsunuz")
            startActivity(Intent(this,Register::class.java).apply{
                putExtra("email",email)
            })
        }
        else -> toast("Hata: ${task.exception?.localizedMessage}")
    }
}
    }
    private fun toast(mesaj: String){
        Toast.makeText(this,mesaj,Toast.LENGTH_SHORT).show()
    }

    fun SifremiUnuttum(view: View){
        startActivity(Intent(this,ForgotPassword::class.java))
    }


}