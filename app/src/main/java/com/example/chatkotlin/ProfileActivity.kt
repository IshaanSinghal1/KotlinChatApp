package com.example.chatkotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        val user=intent.getParcelableExtra<User>("UserInfo")
        Log.i("USERINFO",user.toString())
        Glide.with(this).load(user.profileImage).into(profile_pic)
        UserNametext.text=user.username
    }
}