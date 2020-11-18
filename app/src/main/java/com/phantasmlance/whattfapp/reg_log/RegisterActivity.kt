package com.phantasmlance.whattfapp.reg_log

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.phantasmlance.whattfapp.R
import com.phantasmlance.whattfapp.models.Users
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*

@Suppress("DEPRECATION")
class RegisterActivity : AppCompatActivity() {

    private var selectedPhotoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        imageViewRegister.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

        buttonRegister.setOnClickListener {
            performRegister()
        }

        linkLogin.setOnClickListener {
            redirectToLogin()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            // proceed and check what the selected image was...
            Log.d("REG", "Photo was selected.")

            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
            imageViewRegister.setImageBitmap(bitmap)
        }
    }

    private fun performRegister() {
        val email = editEmailRegister.text.toString()
        val password = editPasswordRegister.text.toString()

        if (email.isEmpty() && password.isEmpty()) {
            Toast.makeText(this, "Please enter text in email and password", Toast.LENGTH_SHORT)
                .show()
            return
        }

        // Firebase Authentication to create a user with email and password
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener

                // else if successful
                Log.d("Main", "Successfully created user with uid: ${it.result?.user?.uid}")

                uploadImageToFirebaseStorage()

                redirectToLogin()
            }
            .addOnFailureListener {
                Log.d("Main", "Failed to create user: ${it.message}")
                Toast.makeText(this, "Failed to create user: ${it.message}", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    private fun redirectToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun uploadImageToFirebaseStorage() {
        if (selectedPhotoUri == null) return

        val fileName = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$fileName")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener { it ->
                Log.d("REG", "Successfully uploaded image: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    Log.d("REG", "File Location: $it")

                    saveUserToFirebaseDatabase(it.toString())
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Can't upload this image.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveUserToFirebaseDatabase(profileImageUrl: String) {

        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user = Users(uid, editUsername.text.toString(), profileImageUrl)

        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("REG", "User is saved to database")
            }
            .addOnFailureListener {
                Log.d("REG", "Failed to set value to database: ${it.message}")
            }
    }
}