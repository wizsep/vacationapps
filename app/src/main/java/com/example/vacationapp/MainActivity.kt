package com.example.vacationapp

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.example.vacationapp.viewmodel.VacationSpot
import com.example.vacationapp.viewmodel.VacationViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

import org.osmdroid.util.GeoPoint


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: VacationViewModel = viewModel()
            NavGraph(viewModel)
        }
    }

    private fun viewModel(): VacationViewModel {
        TODO("Not yet implemented")
    }
}

@Composable
fun MyApp(viewModel: VacationViewModel = viewModel()) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Registro de Vacaciones") })
        },
        content = { paddingValues ->
            MainContent(Modifier.padding(paddingValues), viewModel)
        }
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainContent(modifier: Modifier, viewModel: VacationViewModel, navController: NavHostController) {
    val context = LocalContext.current
    val load = android.content.res.Configuration.getInstance()
        .load(context, androidx.preference.PreferenceManager.getDefaultSharedPreferences(context))

    var name by remember { mutableStateOf("") }
    var photos by remember { mutableStateOf(listOf<String>()) }
    val locationState = remember { mutableStateOf(GeoPoint(0.0, 0.0)) }

    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    )

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uriList ->
        photos = uriList.map { it.toString() }
    }

    Column(
        modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre del Lugar") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { launcher.launch("image/*") }) {
            Text("Agregar Fotos")
        }

        Spacer(modifier = Modifier.height(16.dp))

        photos.forEach { photo ->
            Image(
                painter = rememberImagePainter(photo),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clickable {
                        // Mostrar foto en pantalla completa
                    }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            if (permissionsState.allPermissionsGranted) {
                // Obtener la ubicación simulada
                locationState.value = GeoPoint(37.7749, -122.4194) // Ejemplo de coordenadas
                viewModel.addVacationSpot(VacationSpot(name, photos, locationState.value.latitude, locationState.value.longitude))
            } else {
                permissionsState.launchMultiplePermissionRequest()
            }
        }) {
            Text("Guardar")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            navController.navigate("map")
        }) {
            Text("Ver Mapa")
        }
    }
}

class ExperimentalPermissionsApi {

}

