package com.hadijoe.projectandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.hadijoe.projectandroid.ui.navigation.AppNavigation
import com.hadijoe.projectandroid.ui.theme.ProjectAndroidTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProjectAndroidTheme {
                AppNavigation()
            }
        }
    }
}
