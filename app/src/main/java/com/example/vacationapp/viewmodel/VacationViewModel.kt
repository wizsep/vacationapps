package com.example.vacationapp.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

data class VacationSpot(
    val name: String,
    val photos: List<String>, // Lista de rutas de fotos
    val latitude: Double,
    val longitude: Double
)

class VacationViewModel : ViewModel() {
    val vacationSpots = mutableStateListOf<VacationSpot>()

    fun addVacationSpot(spot: VacationSpot) {
        vacationSpots.add(spot)
    }
}
