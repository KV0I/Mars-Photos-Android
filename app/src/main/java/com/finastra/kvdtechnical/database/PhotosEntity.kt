package com.finastra.kvdtechnical.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.finastra.kvdtechnical.domain.Photos
import com.finastra.kvdtechnical.network.model.Camera
import com.finastra.kvdtechnical.network.model.Rover

@Entity
data class PhotosEntity constructor(
    @PrimaryKey
    val id: Int,
    val sol: Int,
    val camera_full_name: String,
//    val camera: Camera,
    val img_src: String,
    val earth_date: String,
//    val rover: Rover,
    val roverName: String
)

fun List<PhotosEntity>.asDomainModel(): List<Photos> {
    return map {
        Photos(
            id = it.id,
            camera_full_name = it.camera_full_name,
            roverName = it.roverName,
            sol = it.sol,
            img_src = it.img_src,
            earth_date = it.earth_date
        )
//            camera = it.camera,
//            rover = it.rover
    }
}