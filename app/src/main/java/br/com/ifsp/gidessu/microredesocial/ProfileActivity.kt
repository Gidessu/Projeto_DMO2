package br.com.ifsp.gidessu.microredesocial

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.com.ifsp.gidessu.microredesocial.databinding.ActivityProfileBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        changeProfile()

        saveProfile()

    }

    fun changeProfile(){
        val galeria = registerForActivityResult(
            ActivityResultContracts.PickVisualMedia()) {
                uri ->
            if (uri != null) {
                binding.edtProfile.setImageURI(uri)
            } else {
                Toast.makeText(this, "Nenhuma foto selecionada", Toast.LENGTH_LONG).show()
            }
        }
        binding.btnEditPhoto.setOnClickListener {
            galeria.launch(
                PickVisualMediaRequest(
                    ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }
    }

    fun saveProfile() {
        binding.btnEditPhoto.setOnClickListener {
            val firebaseAuth = FirebaseAuth.getInstance()
            if (firebaseAuth.currentUser != null) {
                val email = firebaseAuth.currentUser!!.email.toString()
                val username = binding.username.text.toString()
                val nomeCompleto = binding.nomeCompleto.text.toString()
                val fotoPerfilString = Base64Converter.drawableToString(binding.edtProfile.drawable)
                val db = Firebase.firestore
                val dados = hashMapOf(
                    "nomeCompleto" to nomeCompleto,
                    "username" to username,
                    "fotoPerfil" to fotoPerfilString
                )
                db.collection("usuarios").document(email)
                    .set(dados)
                    .addOnSuccessListener {
                        startActivity(Intent(this, HomeActivity::class.java))
                        finish()
                    }
            }
        }
    }




}