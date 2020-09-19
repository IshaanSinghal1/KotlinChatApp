package com.example.chatkotlin

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.ImageDecoder
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {
    companion object{
        private const val IMAGE_CODE=0
        private const val PERMISSION_READ = 1
    }
    private var uri: Uri?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        register_button.setOnClickListener {
            performRegistration()
        }
        check_already.setOnClickListener {
            val intent= Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }
        profileImage.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    PERMISSION_READ
                )
            }else{
                val intent=Intent(Intent.ACTION_GET_CONTENT)
                intent.type="image/*"
                startActivityForResult(intent, IMAGE_CODE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode== IMAGE_CODE && resultCode==Activity.RESULT_OK&& data!=null){
            uri=data.data
            try {
                    if(Build.VERSION.SDK_INT <=28) {
                        val bitmap = MediaStore.Images.Media.getBitmap(
                            this.contentResolver,
                            uri
                        )
                        real_profile_image.setImageBitmap(bitmap)
                    } else {
                        val source = ImageDecoder.createSource(this.contentResolver, uri!!)
                        val bitmap = ImageDecoder.decodeBitmap(source)
                        real_profile_image.setImageBitmap(bitmap)
                    }
                profileImage.alpha=0f
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun performRegistration(){
        val email=EmailAddress.text.toString()
        val password=Password.text.toString()
        if(email.isEmpty()||password.isEmpty())
            return
        if(password.length<6){
            Toast.makeText(this,"Password length must be greater than 6",Toast.LENGTH_SHORT).show()
            return
        }
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password).addOnCompleteListener {
            if (!it.isSuccessful)
                return@addOnCompleteListener
            Log.i("Register","The user is registered successfully with ID ${it.result?.user?.uid}")
            uploadImageToFireBase()
        }.addOnFailureListener {
            Log.i("Register","Failed to register: ${it.message}")
            Toast.makeText(this,"Failed to register: ${it.message}",Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadImageToFireBase() {
        if(uri==null)
            return
        val filename=UUID.randomUUID().toString()
        val ref=FirebaseStorage.getInstance().getReference("/images/$filename")
        ref.putFile(uri!!).addOnSuccessListener { it ->
            Log.d("Register","Success in adding image : ${it.metadata?.path}")

            ref.downloadUrl.addOnSuccessListener {
                saveUserToDatabase(it.toString())
            }
        }.addOnFailureListener {
            Log.i("RegisterActivity","Failure Bolte")
        }
    }
    private fun saveUserToDatabase(image:String){
        val uid=FirebaseAuth.getInstance().uid?:""
        val ref= FirebaseDatabase.getInstance().getReference("/users/$uid")
        val user =User(uid,image,PersonName.text.toString())
        ref.setValue(user).addOnSuccessListener {
            Log.i("RegisterActivity","Successfully Added to database")
            val intent=Intent(this,MessageActivity::class.java)
            intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }.addOnFailureListener {
            Log.i("RegisterActivity","Database connection failed")
        }
    }

}

@Parcelize
class User(var uid:String,var profileImage:String,var username:String) : Parcelable {
    constructor():this("","","")
}