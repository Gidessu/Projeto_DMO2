package br.com.ifsp.gidessu.microredesocial

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.scale
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.com.ifsp.gidessu.microredesocial.databinding.ActivityHomeBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import java.io.ByteArrayOutputStream
import kotlin.io.encoding.Base64

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var firebaseAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.btnLogout.setOnClickListener{
            firebaseAuth.signOut()

            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }



        val firebaseAuth = FirebaseAuth.getInstance()
        val email = firebaseAuth.currentUser!!.email.toString()
        val db = Firebase.firestore
        db.collection("usuarios").document(email).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    val document = task.result
                    val imageString = document.data!!["fotoPerfil"].toString()
                    val bitmap = Base64Converter.stringToBitmap(imageString)
                    binding.imgLogo.setImageBitmap(bitmap)
                    binding.txtUsername.text = document.data!!["username"].toString()
                    binding.txtNomeCompleto.text = document.data!!["nomeCompleto"].toString()
                }
            }
    }





}