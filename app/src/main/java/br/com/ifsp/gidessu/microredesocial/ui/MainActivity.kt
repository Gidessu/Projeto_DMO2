package br.com.ifsp.gidessu.microredesocial.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.ifsp.gidessu.microredesocial.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Inicializa o Firebase primeiro
        firebaseAuth = FirebaseAuth.getInstance()

        // 2. VERIFICAÇÃO DE LOGIN AUTOMÁTICO
        // Se já existe um usuário logado, vai direto para a Home
        if (firebaseAuth.currentUser != null) {
            irParaHome()
            return // Impede que o resto do layout seja inflado desnecessariamente
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
    }

    private fun irParaHome() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish() // Importante para que o usuário não volte para o login ao apertar "voltar"
    }

    fun autenticarUsuario(){
        val email = binding.edtEmail.text.toString()
        val password = binding.edtPassword.text.toString()

        if (email.isBlank() || password.isBlank()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            return
        }

        firebaseAuth
            .signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    irParaHome()
                } else {
                    Toast.makeText(this, "Erro no login: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    fun setupListeners(){
        binding.btnLogin.setOnClickListener{ autenticarUsuario() }
        binding.btnCreateAccount.setOnClickListener{
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }
}