package com.giftech.meraihmimpi

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_home.*


class HomeActivity : AppCompatActivity(), Connector {

    private var list:ArrayList<Goal> = arrayListOf()

    lateinit var preferences: Preferences
    lateinit var database: DatabaseReference
    lateinit var user : User


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

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

        //Aksi saat floating button diklik
        btn_fab.setOnClickListener {
            //Buat pop up dialog dan inflate layout add_popup
            var dialog : AlertDialog.Builder = AlertDialog.Builder(this)
            val view:View = layoutInflater.inflate(R.layout.add_popup, null)

            //Masukkan text inputan pada goalname
            val goalName = view.findViewById<EditText>(R.id.et_text)

            //Set view dialog
            dialog.setView(view)

            //Set positive button
            dialog.setPositiveButton("Add") { _: DialogInterface, _: Int ->
                //Kondisi jika text inputan ada isinya
                if (goalName.text.isNotEmpty()) {
                    //Buat new data dan push ke child goal user tsb. Lalu isi value nya dengan goal
                    val newItemData = database.child(user.username).child("goal").push()
                    var goal = Goal()
                    goal.name = goalName.text.toString()
                    goal.UID = newItemData.key.toString()
                    newItemData.setValue(goal)

                    //Tambahkan goal tsb ke arraylist lalu refresh
                    list.add(goal)
                    refreshList()
                }
            }
            //Set positive button
            dialog.setNegativeButton("Cancel") { _: DialogInterface, _: Int ->
            }
            //Menampilkan dialog popup
            dialog.show()
        }

        //Aksi saat button profile di klik, arahkan ke profile
        btn_profile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        //Set welcome text dengan nama user
        tv_welcome.text = "Welcome, \n"+user.name
    }

    private fun addDataToList() {
        //ValueEventListener untuk mengambil data dari firebase dengan child goal user tsb
        database.child(user.username).child("goal").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //Hapus data dalam arraylist agar tidak jadi penumpukan data
                list.clear()

                //Ambil semua child dalam goal dan masukan ke items
                var items = snapshot.children
                //Lakukan iterasi pada setiap item lalu buat class dan tambahkan ke list
                items.forEach{
                    var goal = it.getValue(Goal::class.java)
                    list.add(goal!!)
                }
                refreshList()

                //Conditional saat list kosong, maka tampilkan informasi bahwa tidak ada goal
                if(list.size == 0){
                    ll_empty_home.visibility = View.VISIBLE
                } else{
                    ll_empty_home.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    //Function yang menampilkan recyclerview
    private fun showRecyclerView() {
        rv_goals.layoutManager = LinearLayoutManager(this)
        val goalAdapter = GoalAdapter(this, list)
        rv_goals.adapter = goalAdapter
    }

    //Function override dari interface connector untuk menampilkan ulang recyclerview
    override fun refreshList() {
        val goalAdapter = GoalAdapter(this, list)
        rv_goals.adapter = goalAdapter
    }

    //Function override dari interface connector untuk menghapus item goal dari list
    override fun deleteItem(iGoal: Goal, list: ArrayList<Goal>) {
        list.remove(iGoal)
    }

}