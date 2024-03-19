package com.finastra.kvdtechnical.ui.photos

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import com.finastra.kvdtechnical.domain.Photos
import com.finastra.kvdtechnical.ui.photos.section.PhotoList

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PhotosScreen(
    photos: List<Photos> = listOf(),
    onEvent: (PhotoEvent) -> Unit,
    tabList: List<String>,
    isRefreshing: Boolean,
    lastPicReached: Boolean,
    selectedDate: String?,
    state: STATE,
) {

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        content = {
            PhotoList(
                photos = photos,
                onEvent = onEvent,
                snackbarHostState = snackbarHostState,
                tabList = tabList,
                isRefreshing = isRefreshing,
                lastPicReached = lastPicReached,
                selectedDate = selectedDate,
                state = state,
            )
        }
    )
}
//
@Preview
@Composable
fun PreviewPhotosScreen() {
    val photos = listOf<Photos>()
    val tabList = listOf("perseverance", "curiosity", "opportunity", "spirit")
    PhotosScreen(
        photos = photos,
        onEvent = {},
        tabList = tabList,
        isRefreshing = false,
        lastPicReached = false,
        selectedDate = null,
        state = STATE.LOADING,
    )
}




