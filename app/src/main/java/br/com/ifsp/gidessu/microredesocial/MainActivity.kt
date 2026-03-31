package br.com.ifsp.gidessu.microredesocial

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.com.ifsp.gidessu.microredesocial.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth : FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupFirebase()
        setupListeners()
    }

    fun setupFirebase(){
        firebaseAuth = FirebaseAuth.getInstance()
    }

    fun autenticarUsuario(){
            val email = binding.edtEmail.text.toString()
            val password = binding.edtPassword.text.toString()

            firebaseAuth
                .signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        if (firebaseAuth.currentUser != null) {
//                            startActivity(Intent(this, HomeActivity::class.java))
                            startActivity(Intent(this, ProfileActivity::class.java))
                            finish()
                        }
                    } else {
                        Toast.makeText(this, "Erro no login", Toast.LENGTH_LONG).show()
                    }
                }
    }

    fun setupListeners(){
        binding.btnLogin.setOnClickListener{ autenticarUsuario() }
        binding.btnCreateAccount.setOnClickListener{
            startActivity(Intent(this, SignUpActivity::class.java)) }
    }

}