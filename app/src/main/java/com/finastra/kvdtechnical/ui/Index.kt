@file:OptIn(ExperimentalMaterialApi::class)

package com.finastra.kvdtechnical.ui

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.finastra.kvdtechnical.components.NoNetwork
import com.finastra.kvdtechnical.ui.photos.LoadingIndicator
import com.finastra.kvdtechnical.ui.photos.PhotosScreen
import com.finastra.kvdtechnical.ui.photos.PhotosViewModel
import com.finastra.kvdtechnical.ui.photos.STATE
import com.finastra.kvdtechnical.util.isInternetAvailable
import timber.log.Timber

@Composable
fun Index() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "listPage"
    ) {
//        produce a hello world
        composable("listPage") {
            val viewModel = hiltViewModel<PhotosViewModel>()
            val state = viewModel.state
            val onEvent = viewModel::onEvent
            val photos = viewModel.uiState.photos
            val lastPicReached = viewModel.lastPicReached
            PhotosScreen(
                navController = navController,
                state = state,
                photos = photos,
                viewModel = viewModel,
                onEvent = onEvent,
                lastPicReached = viewModel.lastPicReached
            )
            if (state == STATE.LOADING) {
                timber.log.Timber.tag("PhotosScreen").d("Loading...")
                LoadingIndicator()
//        onEvent(PhotoEvent.LoadPhotos)
            } else if (isInternetAvailable(LocalContext.current).not() && photos.isEmpty()){
                // Offline
                Timber.tag("PhotosScreen").d("Offline...")
                NoNetwork()
            } else if (viewModel.errorMessage.isNotEmpty()) {
                // Error
                println("Error...")
            }
        }

    }
}
//