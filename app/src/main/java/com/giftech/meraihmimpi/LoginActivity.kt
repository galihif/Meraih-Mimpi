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

        //Inisiasi preferences untuk menyimpan data aplikasi dan inisiasi database firebase
        preferences = Preferences(this)
        database = FirebaseDatabase.getInstance().getReference("user")

        //Pengarahan ke Register saat button register di klik
        btn_to_register.setOnClickListener {
            var intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        //Aksi saat button register di klik
        btn_login.setOnClickListener {
            //Simpan inputan dalam form ke variabel
            tUsername = et_username_login.text.toString()
            tPassword = et_password_login.text.toString()

            //Conditional ketika form input kosong
            if (tUsername.equals("")) {
                et_username_login.error = "Silahkan tulis Username Anda"
                et_username_login.requestFocus()
            } else if (tPassword.equals("")) {
                et_password_login.error = "Silahkan tulis Password Anda"
                et_password_login.requestFocus()
            } else{
                //Jalankan function pushLogin ketika form terisi semua
                pushLogin(tUsername, tPassword)
            }
        }
    }

    private fun pushLogin(tUsername: String, tPassword: String) {
        //ValueEventListener untuk mengambil data dari firebase dengan child username tsb
        database.child(tUsername).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                //Ambil class User dari database dan assign ke val user
                val user = snapshot.getValue(User::class.java)

                //Kondisi ketika user tidak ada di database, maka tampilkan toast
                if (user == null) {
                    Toast.makeText(this@LoginActivity, "User tidak ditemukan", Toast.LENGTH_LONG).show()
                } else{
                    //Kondisi ketika password input sama dengan password di database
                    if(user.password == tPassword){
                        //Setvalue preferences berdasarkan user
                        preferences.setValues("username", user.username)
                        preferences.setValues("name", user.name)
                        preferences.setValues("email", user.email)
                        preferences.setValues("password", user.password)
                        preferences.setValues("logged", "1")

                        //Arahkan ke Home
                        finishAffinity()
                        val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                        startActivity(intent)
                    } else if(user.password != tPassword){
                        //Ketika password inputan tidak sama dengan password database, tampilkan toast
                        et_password_login.error = "Wrong password LOL"
                        et_password_login.requestFocus()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                //Munculkan toast ketika database error
                Toast.makeText(this@LoginActivity, ""+error.message, Toast.LENGTH_LONG).show()
            }

        })
    }
}