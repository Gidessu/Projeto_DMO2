package br.com.ifsp.gidessu.microredesocial.data.model

import android.graphics.Bitmap
import com.google.firebase.Timestamp

data class Post(
    val descricao: String= "",
    val imagem: String = "",
    val data: Timestamp? = null
)