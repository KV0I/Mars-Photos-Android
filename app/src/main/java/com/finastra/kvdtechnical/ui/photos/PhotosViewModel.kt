package com.finastra.kvdtechnical.ui.photos;

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewModelScope
import com.finastra.kvdtechnical.repository.PhotosRepository
import com.finastra.kvdtechnical.util.isInternetAvailable

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel
class PhotosViewModel @Inject constructor(
        private val photosRepository: PhotosRepository,
//        private val page: Int = 1
): ViewModel() {
    private val coroutineExceptionHandler = CoroutineExceptionHandler{ _, throwable ->
        throwable.printStackTrace()
    }

    var uiState by mutableStateOf(PhotosUiState())
        private set

    var isRefreshing by mutableStateOf(false)


    var refreshPhotos = {
        isRefreshing = true
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            photosRepository.refreshPhotos()
        isRefreshing = false
        }
    }

//    fun loadMorePhotos() {
//        uiState = uiState.copy(loading = true)
//        page + 1
//        viewModelScope.launch(Dispatchers.IO) {
//            photosRepository.loadMorePhotos(page)
//        }
//        uiState = uiState.copy(loading = false)
//    }

    init {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
//            check internet connection

            photosRepository.getLatestPhoto()
            photosRepository.photos.collect { list ->
                withContext(Dispatchers.Main) {
                    uiState = if (list.isNullOrEmpty()) {
                        uiState.copy(loading = false)
                    } else {
                        uiState.copy(
                            loading = false,
                            offline = false,
                            photos = list
                        )
                    }
                }
            }
        }
    }

}
