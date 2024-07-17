package com.example.vacationapp

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.vacationapp.viewmodel.VacationViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun MapScreen(viewModel: VacationViewModel = viewModel()) {
    val context = LocalContext.current
    Configuration.getInstance().load(context, androidx.preference.PreferenceManager.getDefaultSharedPreferences(context))

    AndroidView(factory = { ctx ->
        MapView(ctx).apply {
            setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.DEFAULT_TILE_SOURCE)
            setMultiTouchControls(true)
            val mapController = controller
            mapController.setZoom(15)
            val startPoint = GeoPoint(0.0, 0.0) 
            mapController.setCenter(startPoint)

            val marker = Marker(this)
            marker.position = startPoint
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            overlays.add(marker)
        }
    }, update = { mapView ->
        val spot = viewModel.vacationSpots.lastOrNull()
        spot?.let {
            val startPoint = GeoPoint(it.latitude, it.longitude)
            mapView.controller.setCenter(startPoint)

            val marker = Marker(mapView)
            marker.position = startPoint
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            mapView.overlays.clear()
            mapView.overlays.add(marker)
        }
    })
}

