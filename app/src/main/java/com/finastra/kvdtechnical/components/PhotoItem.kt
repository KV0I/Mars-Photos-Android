package com.finastra.kvdtechnical.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.finastra.kvdtechnical.domain.Photos
import com.finastra.kvdtechnical.theme.Shapes


@Composable
fun PhotoItem(item: Photos, isPortrait: Boolean) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val photoItemModifier = if (isPortrait) {
        Modifier.fillMaxWidth().fillMaxHeight()
    } else {
        Modifier.size(screenWidth / 2, 200.dp)
    }
    Box(
        modifier = photoItemModifier
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
                        model = if (item.img_src.startsWith("http:")) {
                            item.img_src.replace("http", "https")
                        } else {
                            item.img_src
                        },
                        contentDescription = null,
                    )

                    Column (
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ){
                        Row (
                            modifier = Modifier
                                .fillMaxWidth()
                                .size(35.dp)
                                // semi transparent
                                .background(
                                    MaterialTheme.colorScheme.background
                                ),
//                                .padding(vertical = 10.dp)
//                                .background(MaterialTheme.colorScheme.surface),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                        )
                        {
                            Text(
                                modifier = Modifier.padding(start = 16.dp),
                                text = item.id.toString(),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                modifier = Modifier.padding(start = 16.dp),
                                text = item.camera_full_name,
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 14.sp
                            )
//                            Text(
//                                modifier = Modifier.padding(start = 16.dp),
//                                text = item.roverName,
//                                color = MaterialTheme.colorScheme.onBackground
//                            )
                            Text(
                                modifier = Modifier.padding(start = 16.dp),
                                text = item.earth_date,
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 14.sp
                            )
                        }
                    }

                }
            }

        }
    }

}