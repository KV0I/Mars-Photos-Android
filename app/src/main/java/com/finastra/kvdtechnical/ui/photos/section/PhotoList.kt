package com.finastra.kvdtechnical.ui.photos.section

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.finastra.kvdtechnical.components.PhotoItem
import com.finastra.kvdtechnical.domain.Photos
import com.finastra.kvdtechnical.ui.photos.PhotoEvent
import com.finastra.kvdtechnical.ui.photos.PhotosViewModel
import com.finastra.kvdtechnical.ui.photos.STATE
import com.finastra.kvdtechnical.util.convertLongToTime
import kotlinx.coroutines.launch
import timber.log.Timber

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PhotoList(
    photos: List<Photos>,
    onEvent: (PhotoEvent) -> Unit,
    snackbarHostState: SnackbarHostState,
    tabList: List<String>,
    isRefreshing: Boolean,
    lastPicReached: Boolean,
    selectedDate: String?,
    state: STATE,
){

    val viewModel = hiltViewModel<PhotosViewModel>()

    // pull to refresh
    val refreshScope = rememberCoroutineScope()
    fun refresh() = refreshScope.launch {
        onEvent(PhotoEvent.Refresh)
    }
    val refreshState = rememberPullRefreshState(isRefreshing, ::refresh)

//    infinite scroll

    val scrollState = rememberLazyListState()
    val isItemReachEndScroll by remember {
        derivedStateOf {
            scrollState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ==
                    scrollState.layoutInfo.totalItemsCount - 1 &&
                    (scrollState.layoutInfo.totalItemsCount > 0)
        }
    }

    LaunchedEffect(key1 = isItemReachEndScroll , block = {
        if (isItemReachEndScroll && !lastPicReached && !isRefreshing && state != STATE.LOADING) {
            Timber.d("Load more photos")
            onEvent(PhotoEvent.LoadMorePhotos)
            snackbarHostState.showSnackbar("Loading more photos...")
        } else if (isItemReachEndScroll && photos.isNotEmpty()){
            snackbarHostState.showSnackbar("Last page of photo query reached")
        } else if (isRefreshing) {
            scrollState.scrollToItem(0)
        }
    })

    val isItemReachStartScroll by remember {
        derivedStateOf {
            scrollState.layoutInfo.visibleItemsInfo.firstOrNull()?.index == 0
        }
    }

    // back to refresh
    BackHandler(enabled = !isItemReachStartScroll) {
        refreshScope.launch {
            scrollState.animateScrollToItem(0)
        }
    }

    // date picker

    val openDialog = remember { mutableStateOf(false) }

    if (openDialog.value) {
        val datePickerState = rememberDatePickerState()
        val confirmEnabled = remember { derivedStateOf { datePickerState.selectedDateMillis != null } }
        DatePickerDialog(
            onDismissRequest = {
                openDialog.value = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        openDialog.value = false
                        var date = ""
                        if(datePickerState.selectedDateMillis != null){
                            date = convertLongToTime(datePickerState.selectedDateMillis!!)
                        }
                        viewModel.selectedDate = date
                        onEvent(PhotoEvent.SelectDate)
                    },
                    enabled = confirmEnabled.value
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        openDialog.value = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(
                state = datePickerState,

                )
        }

    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colorStops = arrayOf(
                        0.0f to MaterialTheme.colorScheme.surfaceTint,
                        0.2f to MaterialTheme.colorScheme.background,
                        0.8f to MaterialTheme.colorScheme.background,
                        1f to MaterialTheme.colorScheme.surface,
                    )
                )
            )
    ){
        Column {
            var selectedTabIndex by remember { mutableIntStateOf(0) }

            // portrait and landscape header
            val config = LocalConfiguration.current
            val orientation = remember { mutableIntStateOf(config.orientation) }
            val isPortrait = orientation.intValue == Configuration.ORIENTATION_PORTRAIT

            if (isPortrait) {
                Spacer(modifier = Modifier.height(50.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
    //                number of photos
//                    Text(text = "Number of photos: ${photos.size}",
//                        color = MaterialTheme.colorScheme.onBackground,)
    //                Text(text = "Current: ${getCurrentDate()}",
    //                    color = MaterialTheme.colorScheme.onBackground,)
                    Text(
                        text = "Mars Rover Photos",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = "Query by date and rover",
                        color = MaterialTheme.colorScheme.onBackground,)
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    OutlinedButton(
                        modifier = Modifier
                            .size(250.dp, 50.dp),
                        onClick = {
                            openDialog.value = true
                        }
                    ) {
                        Text(selectedDate ?: "Latest Photos")
                    }
                    OutlinedButton(
                        modifier = Modifier
                            .size(75.dp, 50.dp),
                        onClick = {
                            onEvent(PhotoEvent.RefreshDate)

                        }
                    ) {
                        Icon(Icons.Rounded.Refresh, contentDescription = "Refresh Pagination")
                    }
                }

            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Mars Rover Photos",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold)
                    OutlinedButton(
                        modifier = Modifier
                            .size(250.dp, 50.dp),
                        onClick = {
                            openDialog.value = true
                        }
                    ) {
                        Text(selectedDate ?: "Latest Photos")
                    }
                    OutlinedButton(
                        modifier = Modifier
                            .size(75.dp, 50.dp),
                        onClick = {
                            onEvent(PhotoEvent.RefreshDate)
                        }
                    ) {
                        Icon(Icons.Rounded.Refresh, contentDescription = "Refresh Pagination")
                    }

                    Text(text = "Query by date and rover",
                        color = MaterialTheme.colorScheme.onBackground,)
                }
            }
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.background,) {
                tabList.forEachIndexed { index, item ->
                    Tab(
                        text = { Text(
                            text = item.replaceFirstChar { it.uppercaseChar() },
                            fontSize =  12.sp
                        )
                        },
                        selected = selectedTabIndex == index,
                        onClick = {
                                selectedTabIndex = index
                                viewModel.selectedRover = item
                                onEvent(PhotoEvent.SelectRover)
                        }
                    )
                }
            }
            Box(modifier = Modifier
                .fillMaxSize()
                .pullRefresh(state = refreshState)) {

                Column {

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(space = 8.dp),
                        state = scrollState
                    ) {
                        if (!isRefreshing) {
                            if (isPortrait) {

                                items(photos) {  photo ->
                                    PhotoItem(item = photo, isPortrait = isPortrait)
                                }
                            } else {
//                                2 photo per row
                                items(photos.windowed(2, 2, true)) { photo ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(space = 8.dp)
                                    ) {
                                        photo.forEach {
                                            PhotoItem(item = it, isPortrait = isPortrait)
                                        }
                                    }
                                }
                            }
                            if (lastPicReached && photos.isNotEmpty()) {
                                item {
                                    Text(
                                        text = "Last page of photo query reached.",
                                        color = MaterialTheme.colorScheme.onBackground,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp)
                                    )
                                    Spacer(modifier = Modifier.height(100.dp))
                                }
                            }
                            if (photos.isEmpty() && state == STATE.SUCCESS) {
                                item {
                                    Text(
                                        text = "No photos found.",
                                        color = MaterialTheme.colorScheme.onBackground,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp)
                                    )
                                    Spacer(modifier = Modifier.height(100.dp))
                                }
                            }
                        }
                    }
                }
                PullRefreshIndicator(
                    modifier = Modifier.align(alignment = Alignment.TopCenter),
                    refreshing = isRefreshing,
                    state = refreshState,
                )

            }
        }
    }

}