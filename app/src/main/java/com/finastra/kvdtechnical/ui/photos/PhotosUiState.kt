package com.finastra.kvdtechnical.ui.photos

import com.finastra.kvdtechnical.domain.Photos

data class PhotosUiState (
    val photos: List<Photos> = listOf(),
    val errorMessage: String = "",
)

// TODO: Error handling


open class PhotoEvent {
    object LoadMorePhotos : PhotoEvent()
    object SelectRover : PhotoEvent()
    object SelectDate : PhotoEvent()
    object RefreshDate : PhotoEvent()
    object Refresh : PhotoEvent()

}
