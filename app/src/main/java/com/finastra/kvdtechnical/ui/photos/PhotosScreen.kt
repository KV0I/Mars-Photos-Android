package com.finastra.kvdtechnical.ui.photos

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material3.Surface
import androidx.compose.material3.TextField
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RadialGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.imageLoader
import coil.request.ImageRequest
import coil.util.DebugLogger
import com.finastra.kvdtechnical.components.NoNetwork
import com.finastra.kvdtechnical.domain.Photos
import com.finastra.kvdtechnical.theme.Magenta700
import com.finastra.kvdtechnical.theme.Shapes
import com.finastra.kvdtechnical.util.convertLongToTime
import com.finastra.kvdtechnical.util.formatDate
import com.finastra.kvdtechnical.util.getCurrentDate
import com.finastra.kvdtechnical.util.isInternetAvailable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PhotosScreen(
    navController: NavHostController,
    state: STATE = STATE.SUCCESS,
    photos: List<Photos> = listOf(),
    viewModel: PhotosViewModel,
    onEvent: (PhotoEvent) -> Unit,
    lastPicReached: Boolean
) {

    val uiState = viewModel.uiState

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        content = {
            PhotoList(
                viewModel = viewModel,
                photos = photos,
                onEvent = onEvent,
                lastPicReached = lastPicReached,
                scope = scope,
                snackbarHostState = snackbarHostState
            )
        }
    )


}

@Composable
fun LoadingIndicator() {
    Column(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally,
    ) {    Card(
            modifier = Modifier
                .alpha(0.8f)
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(100.dp)
                    .padding(16.dp)
            )
        }
    }
}


@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PhotoList(
    viewModel: PhotosViewModel,
    photos: List<Photos>,
    onEvent: (PhotoEvent) -> Unit,
    lastPicReached: Boolean,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState
){

    // pull to refresh
    val refreshing = viewModel.isRefreshing
    val refreshScope = rememberCoroutineScope()
    fun refresh() = refreshScope.launch {
//        process here
        viewModel.refreshPhotos()
    }
    val state = rememberPullRefreshState(refreshing, ::refresh)

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
        if (isItemReachEndScroll && !lastPicReached && !refreshing) {
                Timber.d("Load more photos")
//            viewModel.loadMorePhotos()
                onEvent(PhotoEvent.LoadMorePhotos)
                snackbarHostState.showSnackbar("Loading more photos...")
        } else if (isItemReachEndScroll) {
            snackbarHostState.showSnackbar("Last page of photo query reached")
        }
    })

//    date picker

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
                       var date = "no selection"
                       if(datePickerState.selectedDateMillis != null){
//                           date = Tools.convertLongToTime(datePickerState.selectedDateMillis!!)
                            date = convertLongToTime(datePickerState.selectedDateMillis!!)
                       }
                       viewModel.selectedDate = date
//                       onEvent(PhotoEvent.SelectDate)
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
                        0.0f to Magenta700,
                        0.2f to MaterialTheme.colorScheme.background,
                        0.8f to MaterialTheme.colorScheme.background,
                        1f to MaterialTheme.colorScheme.surface,
                    )
                )
            )
    ){
        Column {
            Spacer(modifier = Modifier.height(50.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
//                number of photos
                Text(text = "Number of photos: ${photos.size}",
                    color = MaterialTheme.colorScheme.onBackground,)
                Text(text = "Today is ${getCurrentDate()}",
                    color = MaterialTheme.colorScheme.onBackground,)
                Text(
                    text = "Mars Rover Photos",
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedButton(
                    modifier = Modifier
                        .size(200.dp, 50.dp),
                    onClick = {
                        openDialog.value = true
                    }
                ) {
                    Text(viewModel.selectedDate ?: "Select Date")
                }
//                dropdownMenu(
                DropDown(viewModel, onEvent)
            }
            Box(modifier = Modifier
                .fillMaxSize()
                .pullRefresh(state = state)) {

                Column {

                    Spacer(modifier = Modifier.height(16.dp))
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(space = 8.dp),
                        state = scrollState
                    ) {
                        if (!refreshing) {
                            items(photos) { photo ->
                                PhotoItem(item = photo)
                            }
                        }
                    }
                }
                PullRefreshIndicator(
                    modifier = Modifier.align(alignment = Alignment.TopCenter),
                    refreshing = refreshing,
                    state = state,
                )

            }
        }
    }

}

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DropDown(viewModel: PhotosViewModel, onEvent: (PhotoEvent) -> Unit){
    
    val list = viewModel.roverList
    
    var isExpanded by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ExposedDropdownMenuBox(
            expanded = isExpanded,
            onExpandedChange = {
                isExpanded = !isExpanded
            }
        ) {
           TextField(
               modifier = Modifier.fillMaxWidth(),
               value = viewModel.selectedRover,
               onValueChange = {
                   viewModel.selectedRover = it
                   onEvent(PhotoEvent.SelectRover)
               },
               readOnly = true,
               trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
               label = { Text(text = "Rover") },

               )
            
            ExposedDropdownMenu(
                expanded = isExpanded,
                onDismissRequest = { isExpanded = false },
                modifier = Modifier.background(MaterialTheme.colorScheme.surface),
            ) {
                list.forEachIndexed { index, item ->
                    DropdownMenuItem(
                        text = { Text(text = item) },
                        onClick = {
                            viewModel.selectedRover = list[index]
                            isExpanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }


        }
        Text(
            text = "Selected: ${viewModel.selectedRover}",
            color = MaterialTheme.colorScheme.onBackground

        )
    }

}

@Composable
fun PhotoItem(item: Photos) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 4.dp)
                .height(300.dp),
            shape = Shapes.large,
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        ) {
            Surface {
                Box(modifier = Modifier.fillMaxSize()) {
                    AsyncImage(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentScale = ContentScale.FillBounds,
//            convert model link to https if it is http
                        model = if (item.img_src.startsWith("http:")) {
                            item.img_src.replace("http", "https")
                        } else {
                            item.img_src
                        },
                        contentDescription = null,
                    )

                    Column (
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Bottom,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ){
                        Row (
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp)
                                .background(MaterialTheme.colorScheme.surface),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                        )
                        {
                            Text(
                                modifier = Modifier.padding(start = 16.dp),
                                text = item.camera_full_name,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                modifier = Modifier.padding(start = 16.dp),
                                text = item.roverName,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                modifier = Modifier.padding(start = 16.dp),
                                text = item.earth_date,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }

                }
            }

        }
    }

}