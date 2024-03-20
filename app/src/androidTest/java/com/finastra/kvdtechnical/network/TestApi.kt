package com.finastra.kvdtechnical.network;

import com.finastra.kvdtechnical.BuildConfig;
import com.finastra.kvdtechnical.network.model.MarsRoverPhotosApiModel;

import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

val API_KEY = BuildConfig.API_KEY ?: "DEMO_KEY";
interface TestApi {


    @GET ("rovers/{rover}/latest_photos")
    suspend fun getMarsRoverPhotos(
            @Path("rover") rover: String,
            @Query("page") page: Int? = 1,
            @Query("api_key") apiKey: String = API_KEY
    ): Response<MarsRoverPhotosApiModel>

    @GET ("rovers/{rover}/photos")
    suspend fun getPhotos(
            @Path("rover")rover: String,
            @Query("sol")sol: Int? = null,
            @Query("page")page: Int = 1,
            @Query("earth_date")earthDate: String? = null,
            @Query("api_key")apiKey: String = API_KEY
    ): Response<MarsRoverPhotosApiModel>


}
