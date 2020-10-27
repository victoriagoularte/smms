package br.com.unb.smms.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.unb.smms.R
import br.com.unb.smms.databinding.ItemPostPedingBinding
import br.com.unb.smms.domain.firebase.Post

class PostAdapter(var posts: List<Post>, private val onItemClick: ((Post) -> Unit)) :
    RecyclerView.Adapter<PostAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post_peding, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = posts.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val binding = holder.binding
        binding?.post = posts[position]
        binding?.executePendingBindings()

//        if (banks[position].selected == true) {
//            binding?.ivSelected?.visibility = View.VISIBLE
//        } else {
//            binding?.ivSelected?.visibility = View.INVISIBLE
//        }
    }

//    fun selected(position: Int) {
//        posts.map { it.selected = false }  //deselect all previous selected banks
//        posts[position].selected = true
//
//        notifyDataSetChanged()
//    }


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding: ItemPostPedingBinding? = ItemPostPedingBinding.bind(view)

        init {
            view.setOnClickListener {
//                selected(layoutPosition)
                onItemClick.invoke(posts[layoutPosition])
            }
        }
    }

}