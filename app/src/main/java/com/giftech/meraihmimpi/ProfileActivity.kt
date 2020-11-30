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

        showRecyclerView()

        preferences = Preferences(this)
        database = FirebaseDatabase.getInstance().getReference("user")

        user = User()
        user.username = preferences.getValues("username").toString()
        user.name = preferences.getValues("name").toString()
        user.email = preferences.getValues("email").toString()
        user.password = preferences.getValues("password").toString()

        addDataToList()

        tv_name_profile.text = user.name

        iv_profile.setOnClickListener {
            Toast.makeText(this, ""+list.size.toString(), Toast.LENGTH_LONG).show()
        }

        iv_back.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        tv_logout.setOnClickListener {
            finishAffinity()
            preferences.setValues("logged", "0")
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun addDataToList() {
        database.child(user.username).child("goal").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var items = snapshot.children
                list.clear()
                items.forEach{
                    var goal = it.getValue(Goal::class.java)
                    if (goal!!.status!!.equals(true)){
                        list.add(goal!!)
                    }
                }
                refreshList()
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

    private fun showRecyclerView() {
        rv_goals_achieved.layoutManager = LinearLayoutManager(this)
        val goalAdapter = GoalAdapter(this, list)
        tv_count_goal.text = list.size.toString()+" "
        rv_goals_achieved.adapter = goalAdapter
    }

    override fun deleteItem(goal: Goal, list: ArrayList<Goal>) {
        list.remove(goal)
    }

    override fun refreshList() {
        val goalAdapter = GoalAdapter(this, list)
        tv_count_goal.text = list.size.toString()+" "
        rv_goals_achieved.adapter = goalAdapter
    }
}