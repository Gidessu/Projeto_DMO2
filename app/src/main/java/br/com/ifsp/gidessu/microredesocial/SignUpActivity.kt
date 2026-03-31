package br.com.ifsp.gidessu.microredesocial

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.com.ifsp.gidessu.microredesocial.databinding.ActivityMainBinding
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
            Toast.makeText(this, "Digite a mesma senha", Toast.LENGTH_LONG).show()
            return
        }

        firebaseAuth
            .signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                        startActivity(Intent(this, HomeActivity::class.java))
                        finish()
                } else {
                    Toast.makeText(this, "Erro no login", Toast.LENGTH_LONG).show()
                }
            }
    }

}