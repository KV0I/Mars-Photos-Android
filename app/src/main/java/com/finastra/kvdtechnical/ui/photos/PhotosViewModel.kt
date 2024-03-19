package com.finastra.kvdtechnical.ui.photos

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finastra.kvdtechnical.repository.PhotosRepository

import javax.inject.Inject

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

enum class STATE {
    LOADING,
    SUCCESS,
    ERROR
}

@HiltViewModel
class PhotosViewModel @Inject constructor(
        private val photosRepository: PhotosRepository,
): ViewModel() {

    private val coroutineExceptionHandler = CoroutineExceptionHandler{ _, throwable ->
        throwable.printStackTrace()
    }
    private var page by mutableIntStateOf(1)

    var state by mutableStateOf(STATE.LOADING)
    var uiState by mutableStateOf(PhotosUiState())
        private set
    var selectedRover by mutableStateOf("perseverance")
    var isRefreshing by mutableStateOf(false)
    var selectedDate: String? by mutableStateOf(null)
    var lastPicReached by mutableStateOf(false)

    private val resetPagination = {
        lastPicReached = false
        page = 1
    }

    var roverList by mutableStateOf(listOf("perseverance", "curiosity", "opportunity", "spirit"))

//    onEvent
    fun onEvent(event: PhotoEvent) {
        when(event) {
            PhotoEvent.LoadMorePhotos -> {
                Timber.d("LOADING MORE PHOTOS...")
                try {
                    page += 1
                    state = STATE.LOADING
                    viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
                        photosRepository.loadMorePhotos(rover = selectedRover, page =  page, earthDate = selectedDate)
                        val result = photosRepository.fetchPhotos(selectedRover,  page)
                        if (result.isNullOrEmpty()) lastPicReached = true
                        state = STATE.SUCCESS
                    }
                } catch (e: Exception) {
                    isRefreshing = false
                    state = STATE.ERROR
                    e.printStackTrace()
                }
            }
            PhotoEvent.SelectRover -> {
                try {
                    isRefreshing = true
                    resetPagination()
                    viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
                        photosRepository.deleteAllPhotos()
                        Timber.d("SELECTED ROVER: $selectedRover")
                        photosRepository.selectPhotosByRover(selectedRover, page = 1, earthDate = selectedDate)
                        isRefreshing = false
                        Timber.d("SELECT ROVER DONE")
                        val result = photosRepository.fetchPhotos(selectedRover, page = page, earthDate = selectedDate)
                        if (result.isNullOrEmpty()) lastPicReached = true
                    }
                } catch (e: Exception) {
                    Timber.d("ERROR: $e")
                    state = STATE.ERROR
                    isRefreshing = false
                    e.printStackTrace()
                }
            }
            PhotoEvent.SelectDate -> {
                Timber.d("SELECTING DATE...")
                isRefreshing = true
                try {
                    state = STATE.LOADING
                    viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
                        photosRepository.deleteAllPhotos()
                        Timber.d("SELECTED DATE: $selectedDate")
                        photosRepository.getPhotosByEarthDate(rover = selectedRover, page = 1, earthDate = selectedDate)
                        isRefreshing = false
                        state = STATE.SUCCESS
                    }
                } catch (e: Exception) {
                    Timber.d("ERROR: $e")
                    state = STATE.SUCCESS
                    e.printStackTrace()
                    isRefreshing = false
                }
            }
            PhotoEvent.RefreshDate -> {
                Timber.d("REFRESHING DATE...")
                try {
                    isRefreshing = true
//                    state = STATE.LOADING
                    resetPagination()
                    selectedDate = null
                    viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
                        photosRepository.deleteAllPhotos()
                        Timber.d("SELECTED DATE: $selectedDate")
                        photosRepository.loadMorePhotos(rover = selectedRover, page = 1)
//                        state = STATE.SUCCESS
                        isRefreshing = false
                    }
                } catch (e: Exception) {
                    Timber.d("ERROR: $e")
                    state = STATE.ERROR
                    e.printStackTrace()
                    isRefreshing = false
                }
            }
            PhotoEvent.Refresh -> {
                Timber.d("REFRESHING...")
                try {
                    isRefreshing = true
                    resetPagination()
                    viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
                        photosRepository.deleteAllPhotos()
                        photosRepository.loadMorePhotos(
                            rover = selectedRover,
                            page = 1,
                            earthDate = selectedDate
                        )
                        isRefreshing = false
                    }
                } catch (e: Exception) {
                    Timber.d("ERROR: $e")
                    state = STATE.ERROR
                    isRefreshing = false
                    e.printStackTrace()
                }
            }
        }

    }

    init {
        page = 1
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            photosRepository.deleteAllPhotos()
            photosRepository.getLatestPhoto(rover = "perseverance")
            photosRepository.photos.collect { list ->
                withContext(Dispatchers.Main) {
                    uiState = uiState.copy(
                        photos = list ?: listOf()
                    )
//                    if (list.isNullOrEmpty()) {
//                        uiState.lastPicReached = true
//////                        state = STATE.ERROR
//                    }
Timber.d("PHOTOS LISTENER: ${uiState.photos}")
                    state = STATE.SUCCESS
                }
            }
        }
    }

}
