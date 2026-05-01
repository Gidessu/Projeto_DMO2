package br.com.ifsp.gidessu.microredesocial.data.model

import com.google.firebase.Timestamp



data class Post(
    val descricao: String = "",
    val imagem: String = "",
    val autor: String = "",
    val data: Timestamp? = null,
    val localizacao: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)