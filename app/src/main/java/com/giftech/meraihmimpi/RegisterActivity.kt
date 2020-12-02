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

        //Inisiasi preferences untuk menyimpan data aplikasi dan inisiasi database firebase
        preferences = Preferences(this)
        database = FirebaseDatabase.getInstance().getReference("user")

        //Cek apakah sudah login sebelumnya, jika sudah langsung diarahkan ke home
        if(preferences.getValues("logged").equals("1")){
            finishAffinity()
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        //Pengarahan ke Login saat button login di klik
        btn_to_login.setOnClickListener {
            var intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        //Aksi saat button register di klik
        btn_register.setOnClickListener {
            //Menyimpan data text pada form ke variabel
            tUsername = et_username.text.toString()
            tName = et_name.text.toString()
            tEmail = et_email.text.toString()
            tPassword = et_password.text.toString()

            //Conditonal saat form ada yang kosong
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
                //Saat form terisi semua, jalankan function saveUser
                saveUser(tUsername, tName, tEmail, tPassword)
            }
        }


    }

    private fun saveUser(tUsername: String, tName: String, tEmail: String, tPassword: String) {
        //Init class dari data yang berasal dari form
        val tUser = User()
        tUser.username = tUsername
        tUser.name = tName
        tUser.email = tEmail
        tUser.password = tPassword

        //Conditional saat username terisi
        if (tUsername != null){
            //Jalankan function checkUser
            checkUser(tUsername, tUser)
        }
    }

    private fun checkUser(tUsername: String, tUser: User) {
        //ValueEventListener untuk mengambil data dari firebase, yang child nya username tadi
        database.child(tUsername).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                //Ambil data class User yang ada di firebase lalu assign ke val fUser
                val fUser = snapshot.getValue(User::class.java)

                //Conditional ketika fUser kosong, artinya username belum digunakan
                if (fUser == null){
                    //Masukan data tUser ke firebase dengan child username nya
                    database.child(tUsername).setValue(tUser)

                    //Masukan data user dalam preferences
                    preferences.setValues("username", tUser.username.toString())
                    preferences.setValues("name", tUser.name.toString())
                    preferences.setValues("email", tUser.email.toString())
                    preferences.setValues("password", tUser.password.toString())

                    /*Setting value logged ke 1 menandakan bahwa user telah login,
                    kedepannya agar setiap buka aplikasi tidak muncul page register
                    lagi
                     */
                    preferences.setValues("logged", "1")

                    //Arahkan intent ke Home dan passing data user
                    finishAffinity()
                    val intent = Intent(this@RegisterActivity, HomeActivity::class.java)
                    startActivity(intent)
                } else if (fUser != null){
                    /*
                    Kondisi saat user ada dalam database, menandakan
                    bahwa username telah digunakan. Buat alert error
                    berisi pesan pada form username
                     */
                    et_username.error = "Username already used!"
                    et_username.requestFocus()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                //Munculkan toast ketika database error
                Toast.makeText(this@RegisterActivity, ""+error.message, Toast.LENGTH_LONG).show()
            }

        })
    }
}