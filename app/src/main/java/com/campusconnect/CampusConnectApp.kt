package com.campusconnect

import android.app.Application
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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

        val context = LocalContext.current.applicationContext
        val database = CampusDatabase.getDatabase(context)

        val taskRepo = remember { TaskRepository(database.taskDao()) }
        val eventRepo = remember { EventRepository(database.eventDao()) }
        val timetableRepo = remember { TimetableRepository(database.timetableDao()) }

        val taskViewModel = remember {
            TaskViewModel(application = context as Application, repository = taskRepo)
        }
        val eventViewModel = remember { EventViewModel(eventRepo) }
        val timetableViewModel = remember { TimetableViewModel(timetableRepo) }

        val tasks by taskViewModel.tasks.collectAsState()
        val pendingCount = tasks.count { !it.isDone }

        Scaffold(
            bottomBar = {
                BottomNavigationBar(
                    navController = navController,
                    pendingTaskCount = pendingCount
                )
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
                        eventViewModel = eventViewModel,
                        timetableViewModel = timetableViewModel,
                        onNavigateToTasks = { navController.navigate("tasks") },
                        onNavigateToEvents = { navController.navigate("events") },
                        onNavigateToCalendar = { navController.navigate("calendar") }
                    )
                }
                composable("tasks") {
                    TasksScreen(taskViewModel = taskViewModel)
                }
                composable("events") {
                    EventsScreen(eventViewModel = eventViewModel)
                }
                composable("calendar") {
                    CalendarScreen(viewModel = timetableViewModel)
                }
            }
        }
    }
}