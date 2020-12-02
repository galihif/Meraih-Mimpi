package com.giftech.meraihmimpi

//Class goal untuk menyimpan goal user

class Goal{
    var name: String? = ""
    var status: Boolean? = false
    var UID: String? = null

    companion object Factory{
        fun createList(): Goal = Goal()
    }
}