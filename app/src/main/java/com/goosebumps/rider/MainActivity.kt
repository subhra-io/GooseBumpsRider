package com.goosebumps.rider

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.goosebumps.rider.ui.navigation.RiderNavGraph
import com.goosebumps.rider.ui.theme.GoosebumpsRiderTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GoosebumpsRiderTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    RiderNavGraph()
                }
            }
        }
    }
}
