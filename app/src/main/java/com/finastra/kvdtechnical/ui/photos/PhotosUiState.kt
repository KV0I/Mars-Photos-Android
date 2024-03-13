package com.finastra.kvdtechnical.ui.photos

import com.finastra.kvdtechnical.domain.Photos

data class PhotosUiState (
    val loading: Boolean = false,
    val photos: List<Photos> = listOf(),
    val error: String = "",
    val offline: Boolean = false
)