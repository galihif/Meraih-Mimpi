package com.giftech.meraihmimpi

//Interface untuk menghubungkan adapter dan activity

interface Connector {
    fun deleteItem(goal: Goal, list: ArrayList<Goal>)
    fun refreshList()
}