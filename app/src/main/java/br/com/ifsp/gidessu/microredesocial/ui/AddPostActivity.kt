package br.com.ifsp.gidessu.microredesocial.ui.addpost

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import br.com.ifsp.gidessu.microredesocial.databinding.ActivityAddPostBinding
import br.com.ifsp.gidessu.microredesocial.ui.HomeActivity
import br.com.ifsp.gidessu.microredesocial.util.Base64Converter
import br.com.ifsp.gidessu.microredesocial.util.LocalizacaoHelper
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class AddPostActivity : AppCompatActivity(), LocalizacaoHelper.Callback {

    private lateinit var binding: ActivityAddPostBinding
    private var imageSelected = false

    private val LOCATION_PERMISSION_REQUEST_CODE = 1001

    private var latitudeAtual: Double? = null
    private var longitudeAtual: Double? = null

    private val galery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            binding.imgPost.setImageURI(uri)
            imageSelected = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        solicitarLocalizacao()

        binding.btnChangePhoto.setOnClickListener {
            galery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.btnSave.setOnClickListener {
            verificarEChamarHelper()
            salvarPost()
        }

    }


    @android.annotation.SuppressLint("MissingPermission")
    private fun verificarEChamarHelper() {
        val fineLocation = androidx.core.app.ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
        val coarseLocation = androidx.core.app.ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)

        if (fineLocation == android.content.pm.PackageManager.PERMISSION_GRANTED ||
            coarseLocation == android.content.pm.PackageManager.PERMISSION_GRANTED) {

            // Agora o erro sumirá, pois a função está "blindada" pela anotação
            LocalizacaoHelper(this).obterLocalizacaoAtual(this)
        } else {
            Toast.makeText(this, "Permissão necessária para localização", Toast.LENGTH_SHORT).show()
        }
    }

    private fun solicitarLocalizacao() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            // Instancia o Helper e solicita a localização
            LocalizacaoHelper(this).obterLocalizacaoAtual(this)
        }
    }

    private var enderecoParaSalvar: String = ""

    override fun onLocalizacaoRecebida(endereco: Address, latitude: Double, longitude: Double) {
        enderecoParaSalvar = endereco.subAdminArea

        this.latitudeAtual = latitude
        this.longitudeAtual = longitude

        runOnUiThread {
            Toast.makeText(this, "Local encontrado: $enderecoParaSalvar", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onErro(mensagem: String) {
        runOnUiThread { Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show() }
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
        if (descricao.isBlank()) {
            Toast.makeText(this, "Digite uma descrição", Toast.LENGTH_SHORT).show()
            return
        }

        binding.btnSave.isEnabled = false // Evita múltiplos cliques enquanto processa

        // Busca o username do autor antes de salvar
        val email = user.email.toString()
        db.collection("usuarios").document(email).get()
            .addOnSuccessListener { document ->
                val nomeAutor = document.getString("username") ?: "Usuário"

                val fotoString = if (imageSelected) {
                    Base64Converter.drawableToString(binding.imgPost.drawable)
                } else {
                    ""
                }

                // --- MONTAGEM DOS DADOS PARA O FIRESTORE ---
                val dados = hashMapOf(
                    "descricao" to descricao,
                    "imagem" to fotoString,
                    "autor" to nomeAutor,
                    "data" to Timestamp.now(),
                    "localizacao" to enderecoParaSalvar,
                    "latitude" to (latitudeAtual ?: 0.0),
                    "longitude" to (longitudeAtual ?: 0.0)
                )

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
            .addOnFailureListener {
                binding.btnSave.isEnabled = true
                Toast.makeText(this, "Erro ao buscar dados do autor", Toast.LENGTH_SHORT).show()
            }
    }
}