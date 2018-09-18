package com.helloworld.mapbox.mapbox_helloworld

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import com.mapbox.android.core.location.LocationEngine
import com.navisens.motiondnaapi.MotionDna
import com.navisens.motiondnaapi.MotionDnaApplication
import com.navisens.motiondnaapi.MotionDnaInterface

class MotionDnaDataSource : MotionDnaInterface, LocationEngine {

    override fun activate() {
    }

    override fun removeLocationUpdates() {
        app.stop()
    }

    override fun isConnected(): Boolean {
        return true
    }

    override fun getLastLocation(): Location {
        return last_location
    }

    override fun deactivate() {
    }

    override fun obtainType(): Type {
        return Type.valueOf("NAVISENS")
    }

    override fun requestLocationUpdates() {
    }

    var app : MotionDnaApplication
    var ctx : Context
    var pkg : PackageManager
    var last_location : Location
    var devKey : String


    // Constructor with context and packagemanger for our SDK internal usage.
    constructor(ctx: Context, pkg: PackageManager, devKey: String):super(){
        this.ctx = ctx
        this.pkg = pkg
        this.last_location=Location("NAVISENS_LOCATION_PROVIDER")
        this.devKey = devKey
        // Instantiating core
        app = MotionDnaApplication(this)

        // Enabling GPS receivers within SDK.
        app.setExternalPositioningState(MotionDna.ExternalPositioningState.HIGH_ACCURACY)

        // Instantiating inertial engine
        app.runMotionDna(devKey)

        // Trigger inertial engine to run with global positional corrections.
        app.setLocationNavisens()

        // Trigger inertial engine to run in pure inertial from given lat lon and heading.
        // app.setLocationLatitudeLongitudeAndHeadingInDegrees(37.787742, -122.396859, 315.0)
    }


    override fun getAppContext(): Context {
        return this.ctx
    }

    override fun receiveNetworkData(p0: MotionDna?) {
    }

    override fun receiveNetworkData(p0: MotionDna.NetworkCode?, p1: MutableMap<String, out Any>?) {
    }

    override fun receiveMotionDna(motionDna: MotionDna?) {
        var location = Location("NAVISENS_LOCATION_PROVIDER")


        // MotionDna to android.location object conversion
        location.latitude=motionDna!!.location.globalLocation.latitude
        location.longitude=motionDna!!.location.globalLocation.longitude
        location.bearing=motionDna!!.location.heading.toFloat() // Bearing doesn't seemed to be used in COMPASS rendering however it is used in GPS render mode.
        location.bearingAccuracyDegrees=1.0.toFloat()
        location.altitude=motionDna!!.location.absoluteAltitude
        location.accuracy=motionDna!!.location.uncertainty.x.toFloat()
        location.time=motionDna!!.timestamp.toLong()
        location.speed=motionDna!!.motion.stepFrequency.toFloat()
        location.verticalAccuracyMeters=motionDna!!.location.absoluteAltitudeUncertainty.toFloat()

        var i = 0
        while (i < locationListeners.size) {
            locationListeners[i].onLocationChanged(location)
            ++i
        }
    }

    override fun reportError(errorCode: MotionDna.ErrorCode?, s: String?) {
        when (errorCode) {
            MotionDna.ErrorCode.ERROR_AUTHENTICATION_FAILED -> println("Error: authentication failed $s")
            MotionDna.ErrorCode.ERROR_SDK_EXPIRED -> println("Error: SDK expired $s")
            MotionDna.ErrorCode.ERROR_WRONG_FLOOR_INPUT -> println("Error: wrong floor input $s")
            MotionDna.ErrorCode.ERROR_PERMISSIONS -> println("Error: permissions not granted $s")
            MotionDna.ErrorCode.ERROR_SENSOR_MISSING -> println("Error: sensor missing $s")
            MotionDna.ErrorCode.ERROR_SENSOR_TIMING -> println("Error: sensor timing $s")
        }
    }

    override fun getPkgManager(): PackageManager {
        return this.pkg
    }
}