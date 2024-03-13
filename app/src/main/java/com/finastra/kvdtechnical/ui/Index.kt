@file:OptIn(ExperimentalMaterialApi::class)

package com.finastra.kvdtechnical.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.finastra.kvdtechnical.components.NoNetwork
import com.finastra.kvdtechnical.ui.photos.PhotosScreen

@Composable
fun Index() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "listPage"
    ) {
//        produce a hello world
        composable("listPage") {
            ListPage(navController)
        }

    }
}
//
@Composable
fun ListPage(navController: NavHostController) {

//    hero header
    Column {
        
    }

    PhotosScreen(navController)

}
