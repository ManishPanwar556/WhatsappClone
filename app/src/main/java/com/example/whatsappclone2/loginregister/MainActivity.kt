package com.example.whatsappclone2.loginregister

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.example.whatsappclone2.R
import com.example.whatsappclone2.messages.LatestMessage
import com.example.whatsappclone2.modals.User
//import com.example.whatsappclone.R
//import com.example.whatsappclone.messages.LatestMessage
//import com.example.whatsappclone.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.OnProgressListener
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {
    internal var storageReference: StorageReference? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        register_button_register.setOnClickListener {
            onregister()
        }
        val uid = FirebaseAuth.getInstance().uid
        if (uid != null) {
            val intent = Intent(this, LatestMessage::class.java)
            startActivity(intent)
            finish()
        }
        already_have_account_text_view.setOnClickListener {
            var i = Intent(this, LoginActivity::class.java)
            startActivity(i)
        }
        selectphoto_button_register.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
    }

    var selecteduri: Uri? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            selecteduri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selecteduri)
            selectphoto_imageview_register.setImageBitmap(bitmap)
            selectphoto_button_register.alpha = 0f
//            val bitmapDrawable = BitmapDrawable(bitmap)
//             selectphoto_imageview_register.background=bitmapDrawable
//            uploadImageToFirebaseDatabase()
        }
    }

    private fun onregister() {
        if (email.text.isEmpty() || password.text.isEmpty()) {
            Toast.makeText(this, "Email or Password cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }
        FirebaseAuth.getInstance()
            .createUserWithEmailAndPassword(email.text.toString(), password.text.toString())
            .addOnCompleteListener {
                if (!it.isSuccessful) {
                    return@addOnCompleteListener
                }
                uploadImageToFirebaseStorage()
            }
    }

    private fun uploadImageToFirebaseStorage() {
        if (selecteduri == null) return
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference()
            .child("image/" + selecteduri?.lastPathSegment)
        progressBar.visibility=View.VISIBLE
        ref.putFile(selecteduri!!).addOnProgressListener {
            var progress:Long=(100*it.bytesTransferred)/it.totalByteCount
            progressBar.setProgress(progress.toInt())
        }.addOnSuccessListener {
            Toast.makeText(
                this,
                "Successfully uploaded image",
                Toast.LENGTH_SHORT
            ).show()
            ref.downloadUrl.addOnSuccessListener {
                saveUserToFirebase(it.toString())
            }

        }.addOnFailureListener {
            Toast.makeText(this, "${it.toString()}", Toast.LENGTH_SHORT).show();
        }
        }


    private fun saveUserToFirebase(profileimgurl: String) {
        val uid = FirebaseAuth.getInstance().uid ?: null
        val reference = FirebaseDatabase.getInstance().getReference("/user/$uid")
        val user = User(uid.toString(), profileimgurl, username_edittext_register.text.toString())
        reference.setValue(user).addOnCompleteListener {
            //            Log.d("Registered Activity","${it.toString()}")
            val intent = Intent(
                this,
                LatestMessage::class.java
            )
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)


        }.addOnFailureListener {
            Toast.makeText(this, "${it.toString()}", Toast.LENGTH_SHORT).show()
        }
    }
}

