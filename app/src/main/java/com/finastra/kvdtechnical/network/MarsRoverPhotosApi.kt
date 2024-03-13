package com.finastra.kvdtechnical.network


import com.finastra.kvdtechnical.BuildConfig
import com.finastra.kvdtechnical.network.model.MarsRoverPhotosApiModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


//interface MarsRoverPhotosApi {
//    @GET("rovers/{rover}/photos")
//    suspend fun getMarsPhotos(
//        @Path("rover") rover: String,
//        @Query("sol") sol: Int,
//        @Query
//            ("api_key") apiKey: String
//    ): List<MarsRoverPhotosApiModel>
//}

val API_KEY = BuildConfig.API_KEY ?: "DEMO_KEY"

// get latest from curiosity
interface MarsRoverPhotosApi {

    @GET("rovers/{rover}/latest_photos")

//    @GET("rovers/curiosity/photos?sol=1000&api_key=$API_KEY")
    suspend fun getLatestPhotos(
        @Path("rover") rover: String,
        @Query("page") page: Int? = null,
        @Query("api_key") apiKey: String = API_KEY
    ): MarsRoverPhotosApiModel

    @GET("rovers/{rover}/photos")
    suspend fun getPhotos(
        @Path("rover") rover: String,
        @Query("sol") sol: Int? = null,
        @Query("page") page: Int = 1,
        @Query("earth_date") earthDate: String? = null,
        @Query("api_key") apiKey: String = API_KEY
    ): MarsRoverPhotosApiModel

}

