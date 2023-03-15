package com.example.conduitechangement

data class Etat(
    val etat_actuel:Int,
    val etat_future:Int,
    val date : String?,
    val user_id :Int ,
    val atelier_id : Int
)
