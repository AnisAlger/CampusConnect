package com.campusconnect

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.*
import com.campusconnect.ui.screens.*
import com.campusconnect.ui.theme.CampusConnectTheme
import com.campusconnect.data.repository.*
import com.campusconnect.viewmodel.*

@Composable
fun CampusConnectApp() {
    CampusConnectTheme {
        val navController = rememberNavController()

        // Initialisation du contexte et de la base de données
        val context = LocalContext.current.applicationContext
        val database = CampusDatabase.getDatabase(context)

        // Création des Repositorys
        val taskRepo = TaskRepository(database.taskDao())
        val eventRepo = EventRepository(database.eventDao())
        val timetableRepo = TimetableRepository(database.timetableDao())

        // Création des ViewModels
        val taskViewModel = remember { TaskViewModel(taskRepo) }
        val eventViewModel = remember { EventViewModel(eventRepo) }
        val timetableViewModel = remember { TimetableViewModel(timetableRepo) }

        Scaffold(
            bottomBar = {
                BottomNavigationBar(navController = navController)
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "home",
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("home") {
                    HomeScreen(
                        taskViewModel = taskViewModel,
                        eventViewModel = eventViewModel
                    )
                }
                composable("tasks") {
                    TasksScreen()
                }
                composable("events") {
                    EventsScreen()
                }
                composable("calendar") {
                    CalendarScreen(viewModel = timetableViewModel) // ✅ viewModel passé à l'écran calendrier
                }
            }
        }
    }
}
