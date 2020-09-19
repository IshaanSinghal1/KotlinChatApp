package com.example.chatkotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class NewMessageActivity : AppCompatActivity() {
    companion object{
         const val USER_KEY="USER_KEY"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)
        getDataRef()
    }
    private fun getDataRef(){
        val ref=FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var mylist= ArrayList<User>()
                snapshot.children.forEach {
                    val user=it.getValue(User::class.java)
                    if (user != null) {
                        mylist.add(user)
                    }
                }
                val userRecyclerView:RecyclerView=findViewById(R.id.new_user)
                userRecyclerView.layoutManager=LinearLayoutManager(this@NewMessageActivity)
                var adapter=MessageAdapter(mylist,this@NewMessageActivity,MessageAdapter.OnClickListener{
                    navigateNow(it)
                })
                userRecyclerView.adapter=adapter
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    fun navigateNow(user: User) {
        val intent= Intent(this,ChatLog::class.java)
        intent.putExtra(USER_KEY,user)
        startActivity(intent)
        finish()
    }
}