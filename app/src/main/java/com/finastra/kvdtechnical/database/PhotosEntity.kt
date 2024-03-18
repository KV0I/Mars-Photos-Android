package com.finastra.kvdtechnical.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.finastra.kvdtechnical.domain.Photos

@Entity
data class PhotosEntity constructor(
    val id: Int,
    val sol: Int,
    val camera_full_name: String,
//    val camera: Camera,
    val img_src: String,
    val earth_date: String,
//    val rover: Rover,
    val roverName: String,

) {
    @PrimaryKey(autoGenerate = true)
    var photoId: Int = 0
}

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