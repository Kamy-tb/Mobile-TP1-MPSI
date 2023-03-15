package com.example.conduitechangement

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface EndPoint {
    @GET("getuser/{nom_user}/{pwd}")
    suspend fun verifyUser(@Path ("nom_user")nom_user: String,@Path("pwd")pwd:String):Response<User>

    @GET("getatelier")
    suspend fun getatelier():Response<List<Atelier>>

    @POST("addetat")
    suspend fun addetat(@Body etat: Etat) : Response<String>
}
