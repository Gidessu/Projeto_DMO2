package br.com.ifsp.gidessu.microredesocial.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.ifsp.gidessu.microredesocial.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.btnCreateAccount.setOnClickListener{
            cadastrarUsuario()
        }
    }



    fun cadastrarUsuario(){
        val email = binding.edtEmail.text.toString()
        val password = binding.edtPassword.text.toString()
        val confirmation = binding.edtPasswordValidation.text.toString()

        if(email.isEmpty() || password.isEmpty() || confirmation.isEmpty()){
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_LONG).show()
            return
        }

        if(password != confirmation){
            Toast.makeText(this, "As senhas não coincidem", Toast.LENGTH_LONG).show()
            return
        }

        firebaseAuth
            .createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                } else {
                    // Aqui é bom mostrar o erro real do Firebase para ajudar no debug
                    val erro = task.exception?.message ?: "Erro desconhecido"
                    Toast.makeText(this, "Erro no cadastro: $erro", Toast.LENGTH_LONG).show()
                }
            }
    }

}