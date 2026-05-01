package br.com.ifsp.gidessu.microredesocial.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import br.com.ifsp.gidessu.microredesocial.util.Base64Converter
import br.com.ifsp.gidessu.microredesocial.databinding.ActivityProfileBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding

    private val galeria = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()) {
            uri ->
        if (uri != null) {
            binding.edtProfile.setImageURI(uri)
        } else {
            Toast.makeText(this, "Nenhuma imagem foi selecionada", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        changeProfile()

        saveProfile()

        setupCancelButton()
    }

    fun changeProfile(){
        binding.btnEditPhoto.setOnClickListener {
            galeria.launch(
                PickVisualMediaRequest(
                    ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }
    }

    private fun setupCancelButton() {
        binding.btnCancel.setOnClickListener {
            // Redireciona para a Home sem realizar alterações no Firestore
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    fun saveProfile() {
        binding.btnSave.setOnClickListener {
            val firebaseAuth = FirebaseAuth.getInstance()
            val user = firebaseAuth.currentUser

            if (user != null) {
                val email = user.email.toString()
                val username = binding.username.text.toString()
                val nomeCompleto = binding.nomeCompleto.text.toString()
                val senha = binding.edtSenha.text.toString()
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
                        if (senha.isNotEmpty()) {
                            user.updatePassword(senha).addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(this, "Perfil e senha atualizados!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(this, "Erro ao atualizar senha: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                                }
                            }
                        }

                        startActivity(Intent(this, HomeActivity::class.java))
                        finish()
                    }
            }
        }
    }
}