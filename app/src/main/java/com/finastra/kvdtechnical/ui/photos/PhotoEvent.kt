package com.finastra.kvdtechnical.ui.photos


open class PhotoEvent {
    object LoadPhotos : PhotoEvent()
    object LoadMorePhotos : PhotoEvent()

    object SelectRover : PhotoEvent()

    object GetPhotos : PhotoEvent()

}
