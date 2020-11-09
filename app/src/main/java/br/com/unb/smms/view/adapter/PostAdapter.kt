package br.com.unb.smms.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.unb.smms.R
import br.com.unb.smms.databinding.ItemPostPedingBinding
import br.com.unb.smms.domain.firebase.Post
import com.squareup.picasso.Picasso

class PostAdapter(var posts: List<Post>, private val onItemClick: ((Post) -> Unit)) :
    RecyclerView.Adapter<PostAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post_peding, parent, false)
        return ViewHolder(view, parent.context)
    }

    override fun getItemCount() = posts.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val binding = holder.binding
        binding?.post = posts[position]

        posts[position].urlPicture?.let {
            Picasso.get().load(posts[position].urlPicture).fit().centerCrop()
                .placeholder(R.drawable.progress_bar_default)
                .error(R.drawable.cancel_medium_ff)
                .into(binding?.ivPost)
        }

        if(!posts[position].annotations.isNullOrEmpty()) {
            var descriptions: List<String> = (posts[position].annotations)!!.map { it.description!! }
            binding?.tvTags?.text = "#" + descriptions?.joinToString(" #")
        }

        binding?.executePendingBindings()
    }

    inner class ViewHolder(view: View, context: Context) : RecyclerView.ViewHolder(view) {
        val binding: ItemPostPedingBinding? = ItemPostPedingBinding.bind(view)

        init {
            view.setOnClickListener {
//                selected(layoutPosition)
                onItemClick.invoke(posts[layoutPosition])
            }
        }
    }

}