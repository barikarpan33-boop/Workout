package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.data.NexusDatabase
import com.example.data.NexusRepository
import com.example.ui.NexusViewModel
import com.example.ui.screens.MainApp
import com.example.ui.theme.NexusLevelUpTheme
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MainActivity : ComponentActivity() {
    private lateinit var db: NexusDatabase
    private lateinit var repository: NexusRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        db = Room.databaseBuilder(
            applicationContext,
            NexusDatabase::class.java, "nexus_db"
        ).build()

        repository = NexusRepository(db.nexusDao())

        setContent {
            NexusLevelUpTheme {
                val factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return NexusViewModel(repository) as T
                    }
                }
                
                val viewModel: NexusViewModel = viewModel(factory = factory)
                
                MainApp(viewModel, this)
            }
        }
    }
}
