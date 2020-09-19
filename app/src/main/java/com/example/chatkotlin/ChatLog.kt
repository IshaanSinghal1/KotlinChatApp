package com.example.chatkotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_chat_log.*

class ChatLog : AppCompatActivity() {
    companion object{
        var user: User? =null
    }
    var list= ArrayList<Message>()
    var adapter=ChatAdapter(list)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)
        user=intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        supportActionBar?.title =user?.username
        send_button.setOnClickListener {
            sendMessageInitiate()
            input_message.text=null
        }
        listenMessage()
        val chatRecycler=findViewById<RecyclerView>(R.id.recycler_chat)
        chatRecycler.layoutManager=LinearLayoutManager(this)
        chatRecycler.adapter=adapter

    }

    private fun listenMessage() {
        var fromId=FirebaseAuth.getInstance().uid
        val ref=FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/${user?.uid}")
        ref.addChildEventListener(object :ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message=snapshot.getValue(Message::class.java)
                Log.i("My",message?.text)
                if (message != null) {
                    list.add(message)
                }
                Log.i("ChatChat",list.size.toString())
                adapter.notifyDataSetChanged()
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun sendMessageInitiate(){
        val sender= FirebaseAuth.getInstance().uid ?: return
        val myref=FirebaseDatabase.getInstance().getReference("/user-messages/$sender/${user?.uid}").push()
        val friendref=FirebaseDatabase.getInstance().getReference("/user-messages/${user?.uid}/$sender").push()
        val messageText=input_message.text.toString()
        val message=Message(myref.key!!, user!!.uid,sender,messageText,System.currentTimeMillis()/1000)
        myref.setValue(message)
            .addOnSuccessListener {
                Log.i("Chat log","Adding key ${myref.key} successful")
            }
        friendref.setValue(message)
        val latestFromRef=FirebaseDatabase.getInstance().getReference("/latest-messages/$sender/${user?.uid}")
        latestFromRef.setValue(message)
        val latestToRef=FirebaseDatabase.getInstance().getReference("/latest-messages/${user?.uid}/$sender")
        latestToRef.setValue(message)
    }

}