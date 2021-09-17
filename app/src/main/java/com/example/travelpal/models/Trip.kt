package com.example.travelpal.models

data class Trip (
    val stayId : String = "",
    val userId : String = "",
    val tripImg : String = "",
    val tripName : String = "",
    val username : String = "",
    val fromDate : String = "",
    val toDate : String = "",
    val noOfGuest : String = "",
    val totalPrice : String = "",
    var tripId : String = ""
)
