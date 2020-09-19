package com.example.chatkotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_message.*

class MessageActivity : AppCompatActivity() {
    var listMessage=ArrayList<Message>()
    var adapter=HistoryAdapter(listMessage,this,HistoryAdapter.OnClickListener{
        navigate(it)})
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)
        val uid=FirebaseAuth.getInstance().uid
        if(uid==null){
            val intent=Intent(this,MainActivity::class.java)
            intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
        displayUsers()
        new_message.setOnClickListener {
            val intent=Intent(this,NewMessageActivity::class.java)
            startActivity(intent)
        }
    }
    var messageMap= HashMap<String,Message>()
    private fun refreshView(){
        listMessage.clear()
        messageMap.forEach{
            listMessage.add(it.value)
        }
        Log.i("MyList",listMessage.toString())
        adapter.notifyDataSetChanged()
    }
    private fun displayUsers(){
        var recHistory=findViewById<RecyclerView>(R.id.message_history)
        recHistory.adapter=adapter
        recHistory.layoutManager=LinearLayoutManager(this)
        var myId=FirebaseAuth.getInstance().uid
        val ref=FirebaseDatabase.getInstance().getReference("/latest-messages/$myId")
        ref.addChildEventListener(object:ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                var message=snapshot.getValue(Message::class.java)
                if (message != null) {

                    messageMap[snapshot.key!!]=message
                }
                refreshView()
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val message=snapshot.getValue(Message::class.java)
                if (message != null) {
                    Log.i("MyList",message.text)
                    messageMap.put(snapshot.key!!,message)
                }
                refreshView()
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater=menuInflater.inflate(R.menu.message_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId){
            R.id.logoutOption-> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            R.id.profileOption->{
                val intent = Intent(this, ProfileActivity::class.java)
                val myId=FirebaseAuth.getInstance().uid
                Log.i("UserInfo",myId)
                val myInfo=FirebaseDatabase.getInstance().getReference("/users/$myId")
                myInfo.addListenerForSingleValueEvent(object :ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val user=snapshot.getValue(User::class.java)
                        Log.i("UserInfo",user.toString())
                        intent.putExtra("UserInfo",user)
                        startActivity(intent)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
                //intent.putExtra("UserInfo",user)

            }

        }
        return super.onOptionsItemSelected(item)
    }
    private fun navigate(user: User) {
        val intent= Intent(this,ChatLog::class.java)
        intent.putExtra(NewMessageActivity.USER_KEY,user)
        startActivity(intent)
    }
}
