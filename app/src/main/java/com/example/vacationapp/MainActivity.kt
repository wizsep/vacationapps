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
import androidx.compose.material3.*
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
            MyApp(viewModel)
        }
    }
}

@Composable
fun MyApp(viewModel: VacationViewModel = viewModel()) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Registro de Vacaciones") })
        },
        content = { paddingValues ->
            MainContent(Modifier.padding(paddingValues), viewModel, navController)
        }
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainContent(modifier: Modifier, viewModel: VacationViewModel, navController: NavHostController) {
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var photos by remember { mutableStateOf(listOf<String>()) }
    val locationState = remember { mutableStateOf(GeoPoint(0.0, 0.0)) }

    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    )

    if (permissionsState.shouldShowRationale) {
        AlertDialog(
            onDismissRequest = { /* Rechazo de solicitud */ },
            title = { Text("Permisos necesarios") },
            text = { Text("Para tomar fotos y guardarlas en la galería, se nececita acceso a la cámara y al almacenamiento. Por favor, permita estos permisos para continuar.") },
            confirmButton = {
                Button(onClick = { permissionsState.launchMultiplePermissionRequest() }) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                Button(onClick = {  }) {
                    Text("Cancelar")
                }
            }
        )
    } else if (permissionsState.allPermissionsGranted) {
    } else {
        LaunchedEffect(Unit) {
            permissionsState.launchMultiplePermissionRequest()
        }
    }

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
                    }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            if (permissionsState.allPermissionsGranted) {
                locationState.value = GeoPoint(37.7749, -122.4194) 
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

