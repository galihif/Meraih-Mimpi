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

        showRecyclerView()

        preferences = Preferences(this)
        database = FirebaseDatabase.getInstance().getReference("user")

        user = User()
        user.username = preferences.getValues("username").toString()
        user.name = preferences.getValues("name").toString()
        user.email = preferences.getValues("email").toString()
        user.password = preferences.getValues("password").toString()

        addDataToList()

        btn_fab.setOnClickListener {
            var dialog : AlertDialog.Builder = AlertDialog.Builder(this)
            val view:View = layoutInflater.inflate(R.layout.add_popup, null)
            val goalName = view.findViewById<EditText>(R.id.et_text)

            dialog.setView(view)
            dialog.setPositiveButton("Add") { _: DialogInterface, _: Int ->
                if (goalName.text.isNotEmpty()) {
                    var goal = Goal()
                    val newItemData = database.child(user.username).child("goal").push()
                    goal.name = goalName.text.toString()
                    goal.UID = newItemData.key.toString()

                    newItemData.setValue(goal)
                    list.add(goal)
                    refreshList()
                }
            }
            dialog.setNegativeButton("Cancel") { _: DialogInterface, _: Int ->
            }
            dialog.show()
        }

        btn_profile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        tv_welcome.text = "Welcome, \n"+user.name



    }

    private fun addDataToList() {

        database.child(user.username).child("goal").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var items = snapshot.children
                list.clear()
                items.forEach{
                    var goal = it.getValue(Goal::class.java)
                    list.add(goal!!)
                }
                refreshList()
                Log.v("man", list.size.toString())
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


    private fun showRecyclerView() {
        rv_goals.layoutManager = LinearLayoutManager(this)
        val goalAdapter = GoalAdapter(this, list)
        rv_goals.adapter = goalAdapter
    }

    override fun refreshList() {
        val goalAdapter = GoalAdapter(this, list)
        rv_goals.adapter = goalAdapter
    }


    override fun deleteItem(iGoal: Goal, list: ArrayList<Goal>) {
        list.remove(iGoal)
    }

}