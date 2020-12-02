package com.giftech.meraihmimpi

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

//Class user untuk menyimpan data user

@Parcelize
class User (
    var username:String ="",
    var name:String ="",
    var email:String ="",
    var password:String ="",
) : Parcelable