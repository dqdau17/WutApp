package com.phantasmlance.whattfapp.reg_log

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.phantasmlance.whattfapp.MainActivity
import com.phantasmlance.whattfapp.R
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    @SuppressLint("CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val actionBar = supportActionBar
        actionBar!!.title = "Login"

        textToRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        sharedPreferences = getSharedPreferences("login_data", MODE_PRIVATE)

        editEmailLogin.setText(sharedPreferences.getString("email", ""))
        editPasswordLogin.setText(sharedPreferences.getString("password", ""))
        checkBoxRemember.isChecked = sharedPreferences.getBoolean("check", false)

        buttonLogin.setOnClickListener {
            val email = editEmailLogin.text.toString().trim()
            val password = editPasswordLogin.text.toString().trim()

            Log.d("LOG", "Attempt to login with email/password: $email/***")

            if (email.isEmpty() && password.isEmpty()) {
                Toast.makeText(this, "Please enter email or password!", Toast.LENGTH_SHORT).show()
            } else {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener {
                        val intent = Intent(this, MainActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)

                        if (checkBoxRemember.isChecked) {
                            val editor = sharedPreferences.edit()
                            editor.putString("email", email)
                            editor.putString("password", password)
                            editor.putBoolean("check", true)
                            editor.apply()
                        } else {
                            val editor = sharedPreferences.edit()
                            editor.remove("email")
                            editor.remove("password")
                            editor.remove("check")
                            editor.apply()
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            this,
                            "Your email or password is incorrect!",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@addOnFailureListener
                    }
            }
        }
    }
}