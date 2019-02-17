package imei.mywings.com.bustrackingapp

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import imei.mywings.com.bustrackingapp.routes.JsonUtil
import imei.mywings.com.bustrackingapp.update.OnUpdateListener
import imei.mywings.com.bustrackingapp.update.UpdateLocationAsync
import kotlinx.android.synthetic.main.content_tracker_dashboard.*
import org.json.JSONObject
import java.util.*

class SettingActivity : AppCompatActivity(), OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener, LocationListener, OnUpdateListener {


    private var mMap: GoogleMap? = null
    private val SHOW_ICON_IN_MAP = 49
    private var mGoogleApiClient: GoogleApiClient? = null
    private var mLocationRequest: LocationRequest? = null
    private var latLng: LatLng = LatLng(18.515665, 73.924090)
    private var locationManager: LocationManager? = null

    private var newValue: Int = 0


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            setupMap()
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                SHOW_ICON_IN_MAP
            )
        }
    }

    @SuppressLint("MissingPermission")
    override fun onConnected(p0: Bundle?) {
        LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(
            mLocationRequest, locationCallback,
            Looper.myLooper()
        );
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            locationResult ?: return
            latLng = LatLng(locationResult.locations[0].latitude, locationResult.locations[0].longitude)
            /*initUpdate(
                newValue,
                locationResult.locations[0].latitude.toString(),
                locationResult.locations[0].longitude.toString()*/
            // )

        }
    }

    private fun fetch() {
        Timer().schedule(timerTask, 5000, 20000)
    }

    private val timerTask = object : TimerTask() {
        override fun run() {
            initUpdate(
                newValue,
                latLng.latitude.toString(),
                latLng.longitude.toString()
            )
        }
    }

    private fun initUpdate(id: Int, lat: String, lng: String) {
        val updateLocationAsync = UpdateLocationAsync()
        var request = JSONObject()
        var param = JSONObject()
        param.put("Id", id)
        param.put("Latitude", lat)
        param.put("Longitude", lng)
        request.put("request", param)
        updateLocationAsync.setOnUpdateListener(this, request)
    }

    override fun onConnectionSuspended(p0: Int) {
        Log.e("test", "error")
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        Log.e("test", "error")
    }

    override fun onLocationChanged(location: Location?) {
        Log.e("test", "error")

    }

    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {

    }

    override fun onProviderEnabled(p0: String?) {

    }

    override fun onProviderDisabled(p0: String?) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        var frame = activity_place_map as SupportMapFragment

        frame.getMapAsync(this)
    }

    private fun setupMap() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val enabled = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)

        if (enabled) {
            var location = LocationUtil.getBestLastKnownLocation(this)

            latLng = LatLng(location.latitude, location.longitude)
        }

        mMap!!.uiSettings.isMyLocationButtonEnabled = false


        mGoogleApiClient = GoogleApiClient.Builder(this!!)

            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build()



        mLocationRequest = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval((10 * 1000).toLong())
            .setFastestInterval((1 * 1000).toLong())

        mGoogleApiClient!!.connect()

        val strokeColor = ContextCompat.getColor(this, R.color.map_circle_stroke)
        val shadeColor = ContextCompat.getColor(this, R.color.map_circle_shade)
        val latLng = this.latLng
        mMap!!.addCircle(
            CircleOptions()
                .center(latLng)
                .radius(5.0)
                .fillColor(shadeColor)
                .strokeColor(strokeColor)
                .strokeWidth(2f)
        )
        mMap!!.addMarker(MarkerOptions().position(latLng))
        val cameraPos = CameraPosition.Builder().tilt(60f).target(latLng).zoom(20f).build()
        mMap!!.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPos), 1000, null)


        val intent = Intent(this@SettingActivity, SelectBusActivity::class.java)
        startActivityForResult(intent, 1001)


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1001) {
                newValue = data!!.getIntExtra("id", 0)
                fetch()
            }
        }

    }

    override fun onUpdateSuccess(result: String) {

    }


}
