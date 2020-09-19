package com.example.chatkotlin

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.message_recieved.view.*
import kotlinx.android.synthetic.main.message_sent.view.*

class ChatAdapter(var list: List<Message>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object{
        const val MY_MESSAGE = 0
        const val OTHER_MESSAGE=1
    }
    class SenderHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bindView(message:Message){
            itemView.sent_message.text=message.text
        }
    }
    class ReceiverHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        fun bindView(message: Message){
            itemView.received_message.text=message.text
        }
    }

    override fun getItemViewType(position: Int): Int {
        val message=list[position]
        if(FirebaseAuth.getInstance().uid== message.sender)
            return MY_MESSAGE
        else
            return OTHER_MESSAGE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater=LayoutInflater.from(parent.context)
        val holder=when(viewType){
            MY_MESSAGE-> {
                val view=layoutInflater.inflate(R.layout.message_sent,parent,false)
                SenderHolder(view)
            }
            else ->{
                val view=layoutInflater.inflate(R.layout.message_recieved,parent,false)
                ReceiverHolder(view)
            }
        }
        return holder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message=list[position]
        when(getItemViewType(position)){
            MY_MESSAGE-> {
                holder as SenderHolder
                holder.bindView(message)
            }
            else->{
                holder as ReceiverHolder
                holder.bindView(message)
            }
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }
}
