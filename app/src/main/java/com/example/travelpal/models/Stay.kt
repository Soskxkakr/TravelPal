package com.example.travelpal.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Stay (
    val userId : String = "",
    val userName : String = "",
    val stayImg : String = "",
    val title : String = "",
    val location : String = "",
    val description : String = "",
    val price : String = "",
    var stayId : String = ""
) : Parcelable
