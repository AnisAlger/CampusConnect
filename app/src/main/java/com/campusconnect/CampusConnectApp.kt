package com.campusconnect

import android.app.Application
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.*
import com.campusconnect.ui.components.FloatingCampusBot
import com.campusconnect.ui.screens.*
import com.campusconnect.ui.theme.CampusConnectTheme
import com.campusconnect.data.repository.*
import com.campusconnect.viewmodel.*

@Composable
fun CampusConnectApp() {
    CampusConnectTheme {
        val navController = rememberNavController()

        val context = LocalContext.current.applicationContext
        val database = CampusDatabase.getDatabase(context)

        val taskRepo = remember { TaskRepository(database.taskDao()) }
        val eventRepo = remember { EventRepository(database.eventDao()) }
        val timetableRepo = remember { TimetableRepository(database.timetableDao()) }
        val dataStoreManager = remember { DataStoreManager(context) }

        val taskViewModel = remember {
            TaskViewModel(application = context as Application, repository = taskRepo)
        }
        val eventViewModel = remember { EventViewModel(eventRepo) }
        val timetableViewModel = remember { TimetableViewModel(timetableRepo) }
        val campusBotViewModel = remember { CampusBotViewModel(dataStoreManager = dataStoreManager) }

        val tasks by taskViewModel.tasks.collectAsState()
        val pendingCount = tasks.count { !it.isDone }

        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        Scaffold(
            bottomBar = {
                BottomNavigationBar(
                    navController = navController,
                    pendingTaskCount = pendingCount
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                NavHost(
                    navController = navController,
                    startDestination = "home",
                    modifier = Modifier.fillMaxSize()
                ) {
                    composable("home") {
                        HomeScreen(
                            taskViewModel = taskViewModel,
                            eventViewModel = eventViewModel,
                            timetableViewModel = timetableViewModel,
                            onNavigateToTasks = { navController.navigate("tasks") },
                            onNavigateToEvents = { navController.navigate("events") },
                            onNavigateToCalendar = { navController.navigate("calendar") },
                            onNavigateToBot = { navController.navigate("campusbot") }
                        )
                    }
                    composable("tasks") {
                        TasksScreen(taskViewModel = taskViewModel)
                    }
                    composable("campusbot") {
                        CampusBotScreen(viewModel = campusBotViewModel)
                    }
                    composable("events") {
                        EventsScreen(eventViewModel = eventViewModel)
                    }
                    composable("calendar") {
                        CalendarScreen(viewModel = timetableViewModel)
                    }
                }

                // Floating Chatbot button (visible when not already in full chat)
                if (currentRoute == "home" || currentRoute == "calendar") {
                    FloatingCampusBot(
                        viewModel = campusBotViewModel,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(bottom = 12.dp)
                    )
                }
            }
        }
    }
}