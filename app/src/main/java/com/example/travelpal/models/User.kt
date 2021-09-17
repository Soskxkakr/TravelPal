package com.example.travelpal.models

data class User(
    val id : String = "",
    val firstName : String = "",
    val lastName : String = "",
    val email : String = "",
    val contactNo : String = "",
    var image : String = "",
    var wishLists : ArrayList<String> = ArrayList()
)
