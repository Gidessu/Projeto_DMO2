package br.com.ifsp.gidessu.microredesocial.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.ifsp.gidessu.microredesocial.util.Base64Converter
import br.com.ifsp.gidessu.microredesocial.adapter.PostAdapter
import br.com.ifsp.gidessu.microredesocial.data.model.Post
import br.com.ifsp.gidessu.microredesocial.databinding.ActivityHomeBinding
import br.com.ifsp.gidessu.microredesocial.ui.addpost.AddPostActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        // Configuração do Botão de Logout
        binding.btnLogout.setOnClickListener {
            firebaseAuth.signOut()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        // Configuração do Botão de Perfil
        binding.btnProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
            // Não chamamos finish() aqui para que o usuário possa voltar ao Feed
        }

        // Configuração do Botão de Criar Post
        binding.fabCreatePost.setOnClickListener {
            startActivity(Intent(this, AddPostActivity::class.java))
        }

        // 1. Carregar dados do Perfil do Usuário Logado
        carregarDadosPerfil()

        // 2. Carregar o Feed de Postagens
        configurarFeed()
    }

    private fun carregarDadosPerfil() {
        val email = firebaseAuth.currentUser?.email.toString()

        db.collection("usuarios").document(email).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val imageString = document.data?.get("fotoPerfil").toString()
                    val bitmap = Base64Converter.stringToBitmap(imageString)

                    binding.imgLogo.setImageBitmap(bitmap)
                    binding.txtUsername.text = document.data?.get("username").toString()
                    binding.txtNomeCompleto.text = document.data?.get("nomeCompleto").toString()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao carregar perfil: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }



    private fun configurarFeed() {
        db.collection("posts")
            .orderBy("data", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    Toast.makeText(this, "Nenhum post encontrado!", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                val listaPosts = mutableListOf<Post>()
                for (document in result) {
                    try {
                        val post = document.toObject(Post::class.java)
                        listaPosts.add(post)
                    } catch (e: Exception) {
                        android.util.Log.e("FirebaseError", "Erro ao converter: ${e.message}")
                    }
                }

                val adapter = PostAdapter(listaPosts)
                binding.rvPosts.layoutManager = LinearLayoutManager(this)
                binding.rvPosts.adapter = adapter
            }
            .addOnFailureListener { e ->
                android.util.Log.e("FirebaseError", "Falha na query: ${e.message}")
                Toast.makeText(this, "Erro: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}