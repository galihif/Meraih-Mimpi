package com.giftech.meraihmimpi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity(), Connector {

    private var list:ArrayList<Goal> = arrayListOf()

    lateinit var preferences: Preferences
    lateinit var database: DatabaseReference
    lateinit var user : User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        //Function untuk menampilkan recyclerview
        showRecyclerView()

        //Inisiasi preferences untuk menyimpan data aplikasi dan inisiasi database firebase
        preferences = Preferences(this)
        database = FirebaseDatabase.getInstance().getReference("user")

        //Buat user berdasarkan value di preferences
        user = User()
        user.username = preferences.getValues("username").toString()
        user.name = preferences.getValues("name").toString()
        user.email = preferences.getValues("email").toString()
        user.password = preferences.getValues("password").toString()

        //Function untuk menambahkan data dari firebase ke dalam arrayList
        addDataToList()

        //Set text profile dari nama user
        tv_name_profile.text = user.name

        //Saat back ditekan arahkan ke home
        iv_back.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        //Saat text logout ditekan arahkan ke login dan set logged ke 0
        tv_logout.setOnClickListener {
            finishAffinity()
            preferences.setValues("logged", "0")
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun addDataToList() {
        //ValueEventListener untuk mengambil data dari firebase dengan child goal user tsb
        database.child(user.username).child("goal").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //Hapus data dalam arraylist agar tidak jadi penumpukan data
                list.clear()

                //Ambil semua child dalam goal dan masukan ke items
                var items = snapshot.children
                //Lakukan iterasi pada setiap item yang status nya true lalu buat class dan tambahkan ke list
                items.forEach{
                    var goal = it.getValue(Goal::class.java)
                    if (goal!!.status!!.equals(true)){
                        list.add(goal!!)
                    }
                }
                refreshList()
                //Conditional saat list kosong, maka tampilkan informasi bahwa belum ada goal complete
                if(list.size == 0){
                    ll_empty_profile.visibility = View.VISIBLE
                } else{
                    ll_empty_profile.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    //Function yang menampilkan recyclerview
    private fun showRecyclerView() {
        rv_goals_achieved.layoutManager = LinearLayoutManager(this)
        val goalAdapter = GoalAdapter(this, list)
        tv_count_goal.text = list.size.toString()+" "
        rv_goals_achieved.adapter = goalAdapter
    }

    //Function override dari interface connector untuk menampilkan ulang recyclerview
    override fun deleteItem(goal: Goal, list: ArrayList<Goal>) {
        list.remove(goal)
    }

    //Function override dari interface connector untuk menghapus item goal dari list
    override fun refreshList() {
        val goalAdapter = GoalAdapter(this, list)
        tv_count_goal.text = list.size.toString()+" "
        rv_goals_achieved.adapter = goalAdapter
    }
}