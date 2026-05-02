package br.com.ifsp.gidessu.microredesocial.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.ifsp.gidessu.microredesocial.util.Base64Converter
import br.com.ifsp.gidessu.microredesocial.adapter.PostAdapter
import br.com.ifsp.gidessu.microredesocial.data.model.Post
import br.com.ifsp.gidessu.microredesocial.databinding.ActivityHomeBinding
import br.com.ifsp.gidessu.microredesocial.ui.addpost.AddPostActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private val db = Firebase.firestore

    private val listaCompletaPosts = mutableListOf<Post>()
    private lateinit var postAdapter: PostAdapter

    private var ultimoDoc: DocumentSnapshot? = null
    private var carregando = false
    private var fimDosDados = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        postAdapter = PostAdapter(listaCompletaPosts)
        binding.rvPosts.layoutManager = LinearLayoutManager(this)
        binding.rvPosts.adapter = postAdapter

        binding.btnLogout.setOnClickListener {
            firebaseAuth.signOut()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding.btnProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        binding.fabCreatePost.setOnClickListener {
            startActivity(Intent(this, AddPostActivity::class.java))
        }

        carregarDadosPerfil()
        configurarFeed()
        setupListeners()
    }

    private fun simplificarTexto(texto: String): String {
        val temp = java.text.Normalizer.normalize(texto, java.text.Normalizer.Form.NFD)

        val regex = Regex("\\p{InCombiningDiacriticalMarks}+")
        return regex.replace(temp, "").lowercase().trim()
    }

    private fun setupListeners() {
        binding.edtFiltro.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val filtro = simplificarTexto(s.toString())

                val filtrados = listaCompletaPosts.filter { post ->
                    simplificarTexto(post.localizacao).contains(filtro)
                }

                postAdapter.updateLista(filtrados)
            }

            override fun afterTextChanged(s: android.text.Editable?) {}
        })
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
    }

    private fun configurarFeed() {
        // Carrega os primeiros 5 posts
        carregarPosts()

        binding.rvPosts.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalItens = layoutManager.itemCount
                val ultimoVisivel = layoutManager.findLastVisibleItemPosition()

                if (!carregando && !fimDosDados && ultimoVisivel >= totalItens - 2) {
                    carregarPosts()
                }
            }
        })
    }

    private fun carregarPosts() {
        carregando = true

        var query = db.collection("posts")
            .orderBy("data", Query.Direction.DESCENDING)
            .limit(5)

        if (ultimoDoc != null) {
            query = query.startAfter(ultimoDoc!!)
        }

        query.get()
            .addOnSuccessListener { documentos ->
                if (!documentos.isEmpty) {
                    ultimoDoc = documentos.documents.last()

                    val novosPosts = documentos.toObjects(Post::class.java)

                    listaCompletaPosts.addAll(novosPosts)
                    postAdapter.updateLista(listaCompletaPosts)
                } else {
                    fimDosDados = true
                }
                carregando = false
            }
            .addOnFailureListener {
                carregando = false
                Toast.makeText(this, "Erro ao carregar feed", Toast.LENGTH_SHORT).show()
            }
    }
}