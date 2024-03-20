package com.finastra.kvdtechnical.network;

import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory


object TestApiImplementation {

    fun provideApi(): TestApi = Retrofit.Builder()
        .baseUrl("https://api.nasa.gov/mars-photos/api/v1/")
        .addConverterFactory(MoshiConverterFactory.create())
        .build()
        .create(TestApi::class.java)

}
