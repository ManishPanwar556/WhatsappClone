package com.example.whatsappclone2.loginregister

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.whatsappclone2.R
import com.example.whatsappclone2.messages.LatestMessage
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        back_to_register_textview.setOnClickListener {
            finish()
        }
        login_button_login.setOnClickListener {
            val auth = FirebaseAuth.getInstance()
            auth.signInWithEmailAndPassword(
                email_edittext_login.text.toString(),
                password_edittext_login.text.toString()
            ).addOnCompleteListener {
                val intent = Intent(this, LatestMessage::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}
