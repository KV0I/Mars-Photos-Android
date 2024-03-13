package com.finastra.kvdtechnical.domain;

import com.finastra.kvdtechnical.network.model.Camera;
import com.finastra.kvdtechnical.network.model.Rover;

data class Photos (
    val id: Int,
    val camera_full_name: String,
    val sol: Int,
//    val camera:Camera,
    val img_src: String,
    val earth_date: String,
//    val rover:Rover
    val roverName: String
)
