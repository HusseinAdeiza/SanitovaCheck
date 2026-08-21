package com.sanitova.sanitovacheck

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.sanitova.sanitovacheck.ui.theme.SanitovaCheckTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SanitovaCheckTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    SanitovaCheckNavHost()
                }
            }
        }
    }
}
