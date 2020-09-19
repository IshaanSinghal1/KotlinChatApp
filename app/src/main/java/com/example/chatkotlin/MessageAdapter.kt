package com.example.chatkotlin

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mikhaellopez.circularimageview.CircularImageView


class MessageAdapter(var list:List<User>,var context: Context,val onClickListener: OnClickListener): RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

     class ViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView){
         var userName=itemView.findViewById<TextView>(R.id.MessageName)
         val userImage=itemView.findViewById<CircularImageView>(R.id.friend_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater=LayoutInflater.from(context)
        var view=inflater.inflate(R.layout.recycler_box_main,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currUser=list[position]
        holder.userName.text=currUser.username
        Glide.with(context).load(currUser.profileImage).into(holder.userImage)
        holder.itemView.setOnClickListener {
            onClickListener.onClick(currUser)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
    class OnClickListener(val clickListener:(user:User)->Unit){
        fun onClick(user:User)=clickListener(user)
    }

}
