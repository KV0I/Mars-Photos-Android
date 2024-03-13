package com.finastra.kvdtechnical.ui.photos

import android.util.Log
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.geometry.Size
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
import com.finastra.kvdtechnical.util.getCurrentDate
import com.finastra.kvdtechnical.util.isInternetAvailable
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

@Composable
fun PhotosScreen(navController: NavHostController) {

    val viewModel = hiltViewModel<PhotosViewModel>()
    val uiState = viewModel.uiState


    if (uiState.loading) {
        // Loading
        println("Loading...")
    } else if (isInternetAvailable(LocalContext.current).not() && uiState.photos.isEmpty()){
        // Offline
        Timber.tag("PhotosScreen").d("Offline...")
        NoNetwork()
    } else if (uiState.error.isNotEmpty()) {
        // Error
        println("Error...")
    } else {
        // Success
        PhotoList(viewModel)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PhotoList( viewModel: PhotosViewModel) {

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
                    scrollState.layoutInfo.totalItemsCount - 1
        }
    }

    LaunchedEffect(key1 = isItemReachEndScroll, block = {
        if (isItemReachEndScroll) {
//            viewModel.loadMorePhotos()
        }
    })

    Box(modifier = Modifier
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
//            .background(MaterialTheme.colorScheme.background)
        .pullRefresh(state = state)) {
        Column {
            Spacer(modifier = Modifier.height(50.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = "Today is ${getCurrentDate()}",
                    color = MaterialTheme.colorScheme.onBackground,)
                Text(
                    text = "Mars Rover Photos",
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(space = 8.dp),
            ) {
                if (!refreshing) {
                    items(viewModel.uiState.photos) { photo ->
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

@Composable
fun PhotoItem(item: Photos) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp, 4.dp)
            .height(300.dp),
        shape = RoundedCornerShape(8.dp),
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