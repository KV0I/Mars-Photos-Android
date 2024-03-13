package com.finastra.kvdtechnical.network.model

import com.finastra.kvdtechnical.database.PhotosEntity
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

//@JsonClass(generateAdapter = true)
data class MarsRoverPhotosApiModel(
    val photos: List<Photo>?,
//    @Json(name = "latest_photos")
    val latest_photos: List<Photo>?
)

data class Photo(
    val camera: Camera,
//    @Json(name = "earth_date")
    val earth_date: String,
    val id: Int,
//    @Json(name = "img_src")
    val img_src: String,
    val rover: Rover,
    val sol: Int
)

data class Camera(
//    @Json(name = "full_name")
    val full_name: String,
    val id: Int,
    val name: String,
//    @Json(name = "rover_id")
    val rover_id: Int
)

data class Rover(
//    val cameras: List<CameraX>,
    val id: Int,
//    @Json(name = "landing_date")
//    val landingDate: String,
//    @Json(name = "launch_date")
//    val launchDate: String,
//    @Json(name = "max_date")
//    val maxDate: String,
//    @Json(name = "max_sol")
//    val maxSol: Int,
    val name: String,
    val status: String,
//    @Json(name = "total_photos")
//    val totalPhotos: Int
)

data class CameraX(
    @Json(name = "full_name")
    val fullName: String,
    val name: String
)

fun MarsRoverPhotosApiModel.asDatabaseModel(): List<PhotosEntity>? {
//    use latest_photos if it exist, else use photos
    val photos = latest_photos ?: photos
    return photos?.map {
        PhotosEntity(
            id = it.id,
            camera_full_name = it.camera.full_name,
            roverName = it.rover.name,
            sol = it.sol,
            img_src = it.img_src,
            earth_date = it.earth_date
        )
    }
}