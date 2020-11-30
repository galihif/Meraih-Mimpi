package com.giftech.meraihmimpi

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.database.*

class GoalAdapter (context: Context,private val listGoal: ArrayList<Goal>): RecyclerView.Adapter<GoalAdapter.ListViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback
    private var connector:Connector = context as Connector
    lateinit var database: DatabaseReference
    lateinit var preferences: Preferences
    lateinit var user: User
    private var mContext:Context = context

    interface OnItemClickCallback {
        fun onItemClicked(data: Goal)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvName: TextView = itemView.findViewById(R.id.tv_goal_name)
        var btnStatus: ImageView = itemView.findViewById(R.id.btn_status)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view:View = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_goal,parent,false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        preferences = Preferences(mContext)
        val goal =listGoal[position]
        val user = User()
        user.username = preferences.getValues("username").toString()

        database = FirebaseDatabase.getInstance().getReference("user")
        var itemReference = database.child(user.username).child("goal").child(goal.UID.toString())

        holder.tvName.text = goal.name

        if (goal.status == false){
            Glide.with(holder.itemView.context)
                    .load(R.drawable.circle_white)
                    .apply(RequestOptions().override(40,40))
                    .into(holder.btnStatus)
        } else{
            Glide.with(holder.itemView.context)
                    .load(R.drawable.ic_complete)
                    .apply(RequestOptions().override(40,40))
                    .into(holder.btnStatus)
        }

        holder.btnStatus.setOnClickListener {
            goal.status = !goal.status!!
            connector.refreshList()
            database.child(user.username).child("goal").child(goal.UID.toString()).child("status").setValue(goal.status)
//            Toast.makeText(holder.itemView.context, "Goal " +goal.name+" Completed", Toast.LENGTH_LONG).show()
        }

        holder.itemView.setOnLongClickListener {
            itemReference.removeValue()
            connector.deleteItem(goal, listGoal)
            connector.refreshList()
            Toast.makeText(holder.itemView.context, "Goal " +goal.name+" Deleted", Toast.LENGTH_LONG).show()
            true
        }
    }

    override fun getItemCount(): Int {
        return listGoal.size
    }
}