package br.com.ifsp.gidessu.microredesocial.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.ifsp.gidessu.microredesocial.data.model.Post
import br.com.ifsp.gidessu.microredesocial.databinding.ActivityPostBinding

class PostAdapter(private val posts: Array<Post>) :
    RecyclerView.Adapter<PostAdapter.PostViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ActivityPostBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PostViewHolder(binding)
    }

    class PostViewHolder(val binding: ActivityPostBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]

        holder.binding.textDescription.text = post.descricao
        holder.binding.imgPost.setImageBitmap(post.imagem)
    }

    override fun getItemCount(): Int = posts.size
}
