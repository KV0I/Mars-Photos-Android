package com.finastra.kvdtechnical.ui.photos;

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewModelScope
import com.finastra.kvdtechnical.domain.Photos
import com.finastra.kvdtechnical.network.MarsRoverPhotosApi
import com.finastra.kvdtechnical.repository.PhotosRepository

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filter
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
//        private val page: Int = 1
): ViewModel() {
    private var lastOffset: Long by mutableLongStateOf(0.toLong())
    var errorMessage: String by mutableStateOf("")

    var lastPicReached by mutableStateOf(false)


//    val scope = rememberCoroutineScope()
//    val snackbarHostState = remember { SnackbarHostState() }



    private val coroutineExceptionHandler = CoroutineExceptionHandler{ _, throwable ->
        throwable.printStackTrace()
    }

    var state by mutableStateOf(STATE.LOADING)

    private var page by mutableIntStateOf(1)

    var uiState by mutableStateOf(PhotosUiState())
        private set

    var selectedRover by mutableStateOf("perseverance")

    var isRefreshing by mutableStateOf(false)

    var refreshPhotos = {
        lastPicReached = false
        isRefreshing = true
        page = 1
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            photosRepository.deleteAllPhotos()
            photosRepository.loadMorePhotos(rover = selectedRover, page = 1, earthDate = selectedDate)
            lastPicReached = false
        isRefreshing = false
        }
    }

    var selectedDate: String? by mutableStateOf(null)

    var roverList by mutableStateOf(listOf("perseverance", "curiosity", "opportunity", "spirit"))

    var selectRover = {

        isRefreshing = true
        uiState = uiState.copy(loading = true)
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            photosRepository.loadMorePhotos(selectedRover, page = 1, earthDate = selectedDate)
            photosRepository.photos.collect { list ->
                uiState = uiState.copy(photos = list?: listOf())
                Timber.d("Method: DROPDOWN")
                Timber.d("Photos load: $list")
                Timber.d("Photos count: ${list?.size}")
            }
        }

        uiState = uiState.copy(loading = false)
        isRefreshing = false
    }

    fun loadMorePhotos() {
        try {
            uiState = uiState.copy(loading = true)
            page += 1
            state = STATE.LOADING
            viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
                photosRepository.loadMorePhotos(rover = selectedRover, page = page)
                val result = photosRepository.fetchPhotos(selectedRover, page)
                if (result.isNullOrEmpty()) lastPicReached = true
                state = STATE.SUCCESS
                // pwede pang filter ng rover photosRepository.photos.filter

            }
        } catch (e: Exception) {
            state = STATE.ERROR
            e.printStackTrace()
        }
    }

//    onEvent
    fun onEvent(event: PhotoEvent) {
        when(event) {
//            PhotoEvent.GetPhotos -> {
//                viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
//                    try {
//                        val result = photosApi.getPhotos(rover = selectedRover)
//                    }
//                    catch (e: Exception) {
//                        e.printStackTrace()
//                    }
//                }
//            }
            PhotoEvent.LoadPhotos -> {
                timber.log.Timber.d("LOADING PHOTOS")
                try {
                    state = STATE.LOADING
                    viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
                        photosRepository.getPhotos(selectedRover)
                        state = STATE.SUCCESS
                    }
                } catch (e: Exception) {
                    Timber.d("ERROR: $e")
                    state = STATE.ERROR
                    e.printStackTrace()
                }
            }
            PhotoEvent.LoadMorePhotos -> {
                loadMorePhotos()
            }
            PhotoEvent.SelectRover -> {
                selectRover()
            }
        }
    }

    init {
        page = 1
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
//            check internet connection

            photosRepository.getLatestPhoto(rover = "curiosity")
//            photosRepository.loadMorePhotos(rover = selectedRover, page = 1, earthDate = selectedDate)
            photosRepository.photos.collect { list ->
                withContext(Dispatchers.Main) {
                    if (list.isNullOrEmpty()) {
                        state = STATE.ERROR
                    } else {
                        uiState = uiState.copy(
                            photos = list
                        )
                        state = STATE.SUCCESS
                    }
                }
            }
        }
    }

}
