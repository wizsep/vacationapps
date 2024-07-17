package com.example.vacationapp

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.vacationapp.viewmodel.VacationViewModel
import java.lang.reflect.Modifier

@Composable
fun NavGraph(viewModel: VacationViewModel) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "main") {
        composable("main") { MainContent(Modifier, viewModel, navController) }
        composable("map") { MapScreen(viewModel) }
    }
}
