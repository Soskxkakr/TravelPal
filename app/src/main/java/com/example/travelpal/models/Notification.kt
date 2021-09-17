package com.example.travelpal.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Notification(
    val userId : String = "",
    val notificationHeader : String = "",
    val notificationSubHeader : String = "",
    val date : String = "",
    var notificationId : String = ""
) : Parcelable
