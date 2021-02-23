package com.example.diary.ui.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.example.diary.R
import com.example.kmmsharedmodule.domain.model.Comment

/**
 * Created by Angelo on 2/21/2021
 */
class CommentAdapter:RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    private var _commentsList = emptyList<Comment>()

    fun setData(data: List<Comment>){
        this._commentsList = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentAdapter.CommentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentAdapter.CommentViewHolder, position: Int) {
        val comment = _commentsList[position]
        holder.bindView(comment)
    }

    override fun getItemCount(): Int = _commentsList.size

    inner class CommentViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private val itemImgComment         = itemView.findViewById<AppCompatImageView>(R.id.item_img_comment)
        private val itemTvMessageComment    = itemView.findViewById<AppCompatTextView>(R.id.item_tv_message_comment)

        fun bindView(comment: Comment){
                //itemImgComment -> cargar imagen
                itemTvMessageComment.text = comment.message
        }
    }

}