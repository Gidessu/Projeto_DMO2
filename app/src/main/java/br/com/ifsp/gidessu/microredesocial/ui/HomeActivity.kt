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

        binding.btnLogout.setOnClickListener {
            firebaseAuth.signOut()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding.btnProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        carregarDadosPerfil()

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
                val listaPosts = mutableListOf<Post>()

                for (document in result) {
                    val post = document.toObject(Post::class.java)
                    listaPosts.add(post)
                }

                val adapter = PostAdapter(listaPosts.toTypedArray())

                binding.rvPosts.layoutManager = LinearLayoutManager(this)
                binding.rvPosts.adapter = adapter
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao carregar feed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}