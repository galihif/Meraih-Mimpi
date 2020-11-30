package com.giftech.meraihmimpi

interface Connector {
    fun deleteItem(goal: Goal, list: ArrayList<Goal>)
    fun refreshList()
}