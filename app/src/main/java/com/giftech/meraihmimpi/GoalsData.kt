package com.giftech.meraihmimpi

//Dummy data untuk mencoba recycler view

object GoalsData {
    private val goalNames = arrayOf(
        "Menguasai Android",
        "Bisa Ngehack",
        "Belajar Sepeda",
        "Beli HP baru",
        "Jago renang",
        "Bisa masak air",
        "Android",
    )

    private val goalStatus = arrayOf(
            true,
            false,
            false,
            false,
            false,
            true,
            false,
    )

    val listData: ArrayList<Goal>
        get() {
            val list = arrayListOf<Goal>()
            for (position in goalNames.indices) {
                val goal = Goal()
                goal.name = goalNames[position]
                goal.status = goalStatus[position]
                list.add(goal)
            }
            return list
        }
}