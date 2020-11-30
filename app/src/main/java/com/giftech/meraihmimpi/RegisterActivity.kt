package com.giftech.meraihmimpi

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {
    lateinit var tUsername:String
    lateinit var tName:String
    lateinit var tEmail:String
    lateinit var tPassword:String

    lateinit var preferences: Preferences
    lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        preferences = Preferences(this)
        database = FirebaseDatabase.getInstance().getReference("user")


        if(preferences.getValues("logged").equals("1")){
            finishAffinity()
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        btn_to_login.setOnClickListener {
            var intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        btn_register.setOnClickListener {
            tUsername = et_username.text.toString()
            tName = et_name.text.toString()
            tEmail = et_email.text.toString()
            tPassword = et_password.text.toString()

            if (tUsername.equals("")) {
                et_username.error = "Fill your username!"
                et_username.requestFocus()
            } else if (tName.equals("")) {
                et_name.error = "Fill your name!"
                et_name.requestFocus()
            } else if (tEmail.equals("")) {
                et_email.error = "Fill your email!"
                et_email.requestFocus()
            } else if (tPassword.equals("")) {
                et_password.error = "Fill your password!"
                et_password.requestFocus()
            } else{
                saveUser(tUsername, tName, tEmail, tPassword)
            }
        }


    }

    private fun saveUser(tUsername: String, tName: String, tEmail: String, tPassword: String) {
        val tUser = User()
        tUser.username = tUsername
        tUser.name = tName
        tUser.email = tEmail
        tUser.password = tPassword

        if (tUsername != null){
            checkUser(tUsername, tUser)
        }
    }

    private fun checkUser(tUsername: String, tUser: User) {
        database.child(tUsername).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val fUser = snapshot.getValue(User::class.java)
                if (fUser == null){
                    database.child(tUsername).setValue(tUser)

                    preferences.setValues("username", tUser.username.toString())
                    preferences.setValues("name", tUser.name.toString())
                    preferences.setValues("email", tUser.email.toString())
                    preferences.setValues("password", tUser.password.toString())

                    preferences.setValues("logged", "1")
                    finishAffinity()
                    val intent = Intent(this@RegisterActivity, HomeActivity::class.java)
                    intent.putExtra("user", tUser)
                    startActivity(intent)
                } else if (fUser != null){
                    et_username.error = "Username already used!"
                    et_username.requestFocus()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@RegisterActivity, ""+error.message, Toast.LENGTH_LONG).show()
            }

        })
    }
}