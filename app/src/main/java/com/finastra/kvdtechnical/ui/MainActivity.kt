package com.finastra.kvdtechnical.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import com.finastra.kvdtechnical.theme.TechProjKarlDiomaroTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            // This app is only ever in dark mode, so hard code detectDarkMode to true.
//            SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT, detectDarkMode = { true })
            statusBarStyle = SystemBarStyle.auto(android.graphics.Color.GREEN, android.graphics.Color.RED, detectDarkMode = { true }),
        )
        setContent {
            TechProjKarlDiomaroTheme {
                Index()
            }
        }
    }
}