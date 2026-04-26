package br.com.ifsp.gidessu.microredesocial.ui.addpost

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import br.com.ifsp.gidessu.microredesocial.R
import br.com.ifsp.gidessu.microredesocial.databinding.ActivityAddPostBinding
import br.com.ifsp.gidessu.microredesocial.ui.HomeActivity
import br.com.ifsp.gidessu.microredesocial.util.Base64Converter
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AddPostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddPostBinding
    private var imageSelected = false // Flag para saber se o usuário escolheu uma foto

    private val galery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            binding.imgPost.setImageURI(uri)
            imageSelected = true
        } else {
            Toast.makeText(this, "Nenhuma foto selecionada", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnChangePhoto.setOnClickListener {
            galery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.btnSave.setOnClickListener {
            salvarPost()
        }
    }

    private fun salvarPost() {
        val firebaseAuth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()
        val user = firebaseAuth.currentUser

        if (user == null) {
            Toast.makeText(this, "Usuário não autenticado", Toast.LENGTH_SHORT).show()
            return
        }

        val descricao = binding.edtDescricao.text.toString()

        // 1. Lógica do Placeholder: Se não selecionou imagem, envia String vazia
        val fotoString = if (imageSelected) {
            Base64Converter.drawableToString(binding.imgPost.drawable)
        } else {
            ""
        }

        val dataAtual = Timestamp.now()

        if (descricao.isBlank()) {
            Toast.makeText(this, "Digite uma descrição", Toast.LENGTH_SHORT).show()
            return
        }

        // IMPORTANTE: A chave aqui deve ser "imagem" para bater com o data class Post
        val dados = hashMapOf(
            "descricao" to descricao,
            "imagem" to fotoString,
            "data" to dataAtual
        )

        binding.btnSave.isEnabled = false // Evita cliques duplos

        db.collection("posts")
            .add(dados)
            .addOnSuccessListener {
                Toast.makeText(this, "Post salvo com sucesso!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            }
            .addOnFailureListener {
                binding.btnSave.isEnabled = true
                Toast.makeText(this, "Erro ao salvar o post", Toast.LENGTH_SHORT).show()
            }
    }
}