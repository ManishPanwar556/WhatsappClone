package com.example.whatsappclone2.modals

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(val uid:String,val profileimgurl: String,val username:String): Parcelable {
    constructor():this(uid="",profileimgurl = "",username = "")
}