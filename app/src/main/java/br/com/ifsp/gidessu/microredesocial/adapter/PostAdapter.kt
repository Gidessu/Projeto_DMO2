package br.com.ifsp.gidessu.microredesocial.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.ifsp.gidessu.microredesocial.R
import br.com.ifsp.gidessu.microredesocial.data.model.Post
import br.com.ifsp.gidessu.microredesocial.util.Base64Converter
import java.text.SimpleDateFormat
import java.util.Locale

class PostAdapter(private val postList: List<Post>) :
    RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    class PostViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgPost: ImageView = view.findViewById(R.id.imgPost)
        val txtDescricao: TextView = view.findViewById(R.id.txtDescricao)
        val txtData: TextView = view.findViewById(R.id.txtData)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = postList[position]
        holder.txtDescricao.text = post.descricao

        if (!post.imagem.isNullOrEmpty()) {
            val bitmap = Base64Converter.stringToBitmap(post.imagem)
            holder.imgPost.setImageBitmap(bitmap)
            holder.imgPost.visibility = View.VISIBLE
        } else {
            holder.imgPost.setImageResource(R.drawable.empty_profile)
            holder.imgPost.visibility = View.VISIBLE
        }

        post.data?.let {
            val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            holder.txtData.text = formatter.format(it.toDate())
        }
    }

    override fun getItemCount() = postList.size
}