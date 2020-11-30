package com.giftech.meraihmimpi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*

class LoginActivity : AppCompatActivity() {
    lateinit var tUsername:String
    lateinit var tPassword:String

    lateinit var preferences: Preferences
    lateinit var database: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        preferences = Preferences(this)
        database = FirebaseDatabase.getInstance().getReference("user")

        btn_to_register.setOnClickListener {
            var intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        btn_login.setOnClickListener {
            tUsername = et_username_login.text.toString()
            tPassword = et_password_login.text.toString()

            if (tUsername.equals("")) {
                et_username_login.error = "Silahkan tulis Username Anda"
                et_username_login.requestFocus()
            } else if (tPassword.equals("")) {
                et_password_login.error = "Silahkan tulis Password Anda"
                et_password_login.requestFocus()
            } else{
                pushLogin(tUsername, tPassword)
            }
        }

    }

    private fun pushLogin(tUsername: String, tPassword: String) {
        database.child(tUsername).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                if (user == null) {
                    Toast.makeText(this@LoginActivity, "User tidak ditemukan", Toast.LENGTH_LONG).show()
                } else{
                    if(user.password == tPassword){
                        preferences.setValues("username", user.username)
                        preferences.setValues("name", user.name)
                        preferences.setValues("email", user.email)
                        preferences.setValues("password", user.password)
                        preferences.setValues("logged", "1")

                        finishAffinity()
                        val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                        intent.putExtra("user", user)
                        startActivity(intent)
                    } else if(user.password != tPassword){
                        et_password_login.error = "Wrong password lmao"
                        et_password_login.requestFocus()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}