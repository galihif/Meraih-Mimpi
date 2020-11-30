package com.giftech.meraihmimpi

class Goal{
    var name: String? = ""
    var status: Boolean? = false
    var UID: String? = null

    companion object Factory{
        fun createList(): Goal = Goal()
    }
}