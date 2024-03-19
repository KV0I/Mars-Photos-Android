package com.finastra.kvdtechnical.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.ComponentActivity
import com.finastra.kvdtechnical.theme.TechProjKarlDiomaroTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TechProjKarlDiomaroTheme {
                Index()
            }
        }
    }
}