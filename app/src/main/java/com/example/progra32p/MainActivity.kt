package com.example.progra32p

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.progra32p.ui.ExploradorApp
import com.example.progra32p.ui.ExploradorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ExploradorTheme { ExploradorApp() }
        }
    }
}
