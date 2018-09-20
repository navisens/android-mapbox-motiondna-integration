package com.helloworld.mapbox.mapbox_helloworld

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.android.core.permissions.PermissionsManager

import android.support.v4.app.ActivityCompat
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.CameraMode
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin
import com.navisens.motiondnaapi.MotionDnaApplication
import android.widget.Toast
import com.mapbox.mapboxsdk.plugins.building.BuildingPlugin
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.RenderMode


class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    lateinit var mapboxMap: MapboxMap
    lateinit var buildingPlugin: BuildingPlugin
    lateinit var motionDnaRuntimeSource: MotionDnaDataSource

    // Your Navisens developer key
    val navisensDevKey = "NAVISENS_DEVELOPER_KEY"

    // Your mapbox token
    val mapBoxToken = "MAPBOX_TOKEN"




    override fun onMapReady(mapboxMap: MapboxMap?) {
//        LocationPluginActivity.this.mapboxMap = mapboxMap;
        this.mapboxMap = mapboxMap!!

        // Request Navisens MotionDna permissions

        ActivityCompat.requestPermissions(this, MotionDnaApplication.needsRequestingPermissions(), REQUEST_MDNA_PERMISSIONS)

        // Enable 3D buildings, why? Because it's cool.
        buildingPlugin = BuildingPlugin(mapView, this.mapboxMap)
        buildingPlugin.setVisibility(true)
    }

    companion object {
        private val REQUEST_MDNA_PERMISSIONS = 1
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        // Method called when permissions have been confirmed

        // Instantiating MotionDnaDataSource, passing in context, packagemanager and Navisens devkey
        motionDnaRuntimeSource = MotionDnaDataSource(applicationContext, packageManager, navisensDevKey)

        // Overridding internal data source with MotionDna data source.
        val locationLayerPlugin = LocationLayerPlugin(mapView, this.mapboxMap, motionDnaRuntimeSource)

        //Follow positioning
        locationLayerPlugin.cameraMode = CameraMode.TRACKING

        // Renders position only not heading.
        locationLayerPlugin.renderMode=RenderMode.NORMAL
        lifecycle.addObserver(locationLayerPlugin)

    }

    lateinit var mapView : MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        Mapbox.getInstance(this, mapBoxToken)

        setContentView(R.layout.activity_main)


        mapView = findViewById(R.id.mapView)

        mapView.onCreate(savedInstanceState)

        // Initiate map ready callback.
        mapView.getMapAsync(this);
    }

    public override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    public override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    public override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    public override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

}
