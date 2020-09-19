package com.example.chatkotlin

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mikhaellopez.circularimageview.CircularImageView

class HistoryAdapter (var listMessage:List<Message>, var context: Context, val onClickListener: OnClickListener): RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var userName=itemView.findViewById<TextView>(R.id.MessageName2)
        val userImage=itemView.findViewById<CircularImageView>(R.id.friend_image2)
        var lastMessage=itemView.findViewById<TextView>(R.id.lastmsg)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater= LayoutInflater.from(context)
        var view=inflater.inflate(R.layout.history_log_box,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currUser=listMessage[position]
        var messageUser=""
        if(currUser.sender==FirebaseAuth.getInstance().uid){
            messageUser=currUser.reciever
        }else{
            messageUser=currUser.sender
        }
        val ref=FirebaseDatabase.getInstance().getReference("/users/${messageUser}")
        ref.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val user=snapshot.getValue(User::class.java)
                Glide.with(context).load(user!!.profileImage).into(holder.userImage)
                holder.userName.text=user.username
                holder.itemView.setOnClickListener {
                    onClickListener.onClick(user)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        holder.lastMessage.text=currUser.text

    }

    override fun getItemCount(): Int {
        return listMessage.size
    }
    class OnClickListener(val clickListener:(user:User)->Unit){
        fun onClick(user:User)=clickListener(user)
    }

}

