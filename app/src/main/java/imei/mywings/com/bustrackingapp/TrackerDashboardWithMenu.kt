package imei.mywings.com.bustrackingapp

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.support.annotation.RequiresApi
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
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
import imei.mywings.com.bustrackingapp.routes.DirectionsJSONParser
import imei.mywings.com.bustrackingapp.routes.JsonUtil
import imei.mywings.com.bustrackingapp.update.GetUpdateLocationAsync
import imei.mywings.com.bustrackingapp.update.OnLocationUpdateListener
import kotlinx.android.synthetic.main.activity_tracker_dashboard_with_menu.*
import kotlinx.android.synthetic.main.app_bar_tracker_dashboard_with_menu.*
import kotlinx.android.synthetic.main.content_tracker_dashboard.*
import kotlinx.android.synthetic.main.layout_info.view.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class TrackerDashboardWithMenu : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,

    OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener, LocationListener, OnBusListener, OnRouteListener,
    OnLocationUpdateListener {


    private var mMap: GoogleMap? = null
    private val SHOW_ICON_IN_MAP = 49
    private var mGoogleApiClient: GoogleApiClient? = null
    private var mLocationRequest: LocationRequest? = null
    private var latLng: LatLng = LatLng(18.515665, 73.924090)
    private var locationManager: LocationManager? = null
    private lateinit var cPosition: Marker
    private var cNewPosition: Marker? = null

    private var newValue: Int = 0

    private lateinit var progressDialog: ProgressDialog

    private lateinit var jsonUtil: JsonUtil

    private lateinit var nsource: String

    private lateinit var ndest: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tracker_dashboard_with_menu)
        setSupportActionBar(toolbar)

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Please wait...")
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)

        jsonUtil = JsonUtil()

        var frame = activity_place_map as SupportMapFragment

        frame.getMapAsync(this)


        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        menuInflater.inflate(R.menu.tracker_dashboard_with_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.action_settings -> {
                val intent = Intent(this@TrackerDashboardWithMenu, SettingActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.nav_camera -> {
                val intent = Intent(this@TrackerDashboardWithMenu, ProfileActivity::class.java)
                startActivity(intent)
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.nav_manage -> {

                val intent = Intent(this@TrackerDashboardWithMenu, SettingActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.nav_camera -> {
                val intent = Intent(this@TrackerDashboardWithMenu, ProfileActivity::class.java)
                startActivity(intent)
                return true
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

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

    private fun setupMap() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val enabled = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)

        if (enabled) {
            var location = LocationUtil.getBestLastKnownLocation(this)

            latLng = LatLng(location.latitude, location.longitude)
        }

        mMap!!.uiSettings.isMyLocationButtonEnabled = false
        // mMap!!.uiSettings.isMyLocationButtonEnabled = true

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

        mMap!!.setInfoWindowAdapter(infoWindowAdapter)

        mMap!!.setOnInfoWindowClickListener(infoClick)

        getBuses()
    }

    override fun onLocationChanged(location: Location?) {

    }

    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {

    }

    override fun onProviderEnabled(p0: String?) {

    }

    override fun onProviderDisabled(p0: String?) {

    }

    override fun onConnectionFailed(p0: ConnectionResult) {

    }

    @SuppressLint("MissingPermission")
    override fun onConnected(p0: Bundle?) {
        LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(
            mLocationRequest, locationCallback,
            Looper.myLooper()
        )
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            /* locationResult ?: return
             latLng = LatLng(locationResult.locations[0].latitude, locationResult.locations[0].longitude)
             mMap!!.addMarker(MarkerOptions().position(latLng))
             val cameraPos = CameraPosition.Builder().tilt(60f).target(latLng).zoom(20f).build()
             mMap!!.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPos), 1000, null)*/
        }
    }

    override fun onConnectionSuspended(p0: Int) {

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            SHOW_ICON_IN_MAP ->
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    setupMap()
        }
    }

    override fun onBusSuccess(result: List<Bus>) {
        if (null != result && result.isNotEmpty()) {
            for (i in result.indices) {
                val node = result[i]
                cPosition = mMap!!.addMarker(
                    MarkerOptions().position(LatLng(node.clat.toDouble(), node.clng.toDouble()))
                        .icon(
                            BitmapDescriptorFactory
                                .fromResource(R.mipmap.ic_launcher)
                        ).title("Bus : ${node.name} ")
                        .snippet("${node.name} # ${node.drivename} # ${node.driverphone} # ${node.rid} #${node.id}")
                )
            }
        }
    }

    private fun getBuses() {
        val getVehicles = GetBus()
        getVehicles.setBusListener(this, JSONObject())
    }


    private val infoWindowAdapter = object : GoogleMap.InfoWindowAdapter {

        override fun getInfoContents(marker: Marker?): View? {

            if (marker!!.tag == null) {

                var view: View? = null
                var values: List<String>
                try {
                    view = layoutInflater.inflate(R.layout.layout_info, null)
                    values = marker!!.snippet.toString().split("#")
                    view!!.lblInfo.text =
                        "Bus Name : ${values[0]}\nDriver Name : ${values[1]}\nDriver Phone : ${values[2]}"
                } catch (e: Exception) {
                    view!!.lblInfo.text = "Your Location"
                }


                return view;
            }
            return null
        }

        override fun getInfoWindow(marker: Marker?): View? {
            return null
        }

    }

    private val infoClick = GoogleMap.OnInfoWindowClickListener {
        try {
            val value = it.snippet.toString().split("#")[3]
            newValue = it.snippet.toString().split("#")[4].toString().trim().toInt()
            getRoute(value.trim().toInt())
            progressDialog.show()
        } catch (e: Exception) {
        }
    }
    //internal var key = "&key=AIzaSyA1fYR6-7DIhgORWbFju3nGi3BDojCILp8"
    internal var key = "&key=AIzaSyClCN7T0VPX7MIoOJEMA3W9JLXhV_S7yx4"

    private fun getDirectionsUrl(origin: LatLng, dest: LatLng): String {
        val strOrigin = ("origin=" + origin.latitude + ","
                + origin.longitude)
        val strDest = "destination=" + dest.latitude + "," + dest.longitude
        val sensor = "sensor=false"
        // val key = "&key=" + mKey
        val parameters = "$strOrigin&$strDest&$sensor$key"
        val output = "json"
        return ("https://maps.googleapis.com/maps/api/directions/"
                + output + "?" + parameters)
    }

    private fun getRoute(id: Int) {
        var jFirst = JSONObject()
        var jRoute = JSONObject()
        jRoute.put("rid", id)
        jFirst.put("findRoute", jRoute)
        val findRoute = GetRoute()
        findRoute.setRouteListener(this, jFirst)
    }

    private var destlat: Double = 0.0

    private var destlng: Double = 0.0

    private var srctlat: Double = 0.0

    private var srclng: Double = 0.0


    override fun onRouteSuccess(result: ArrayList<Route>) {

        if (result.isNotEmpty()) {

            destlat = result[0].destlat.trim().toDouble()

            destlng = result[0].destlng.trim().toDouble()

            srctlat = result[0].srclat.trim().toDouble()

            srclng = result[0].srclng.trim().toDouble()


            nsource = result[0].source

            ndest = result[0].destination


            val source = LatLng(srctlat, srclng)

            val dest = LatLng(destlat, destlng)

            val nav = DownloadTask()
            nav.execute(getDirectionsUrl(source, dest))

        }

    }

    private inner class DownloadTask : AsyncTask<String, Void, String>() {

        // Downloading data in non-ui thread
        override fun doInBackground(vararg url: String): String {

            // For storing data from web service
            var data = ""

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0])
            } catch (e: Exception) {
                Log.d("Background Task", e.toString())
            }

            return data
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        override fun onPostExecute(result: String) {
            super.onPostExecute(result)

            val parserTask = ParserTask(mMap!!)

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result)

        }
    }

    /** A method to download json data from url  */
    @Throws(IOException::class)
    private fun downloadUrl(strUrl: String): String {
        var data = ""
        var iStream: InputStream? = null
        var urlConnection: HttpURLConnection? = null
        try {
            val url = URL(strUrl)

            // Creating an http connection to communicate with url
            urlConnection = url.openConnection() as HttpURLConnection

            // Connecting to url
            urlConnection.connect()

            // Reading data from url
            iStream = urlConnection.inputStream

            /* val br = BufferedReader(
                 InputStreamReader(
                     iStream!!
                 )
             )*/
            data = jsonUtil.convertStreamToString(iStream)
            // br.close()
        } catch (e: Exception) {

        } finally {
            iStream!!.close()
            urlConnection!!.disconnect()
        }
        return data
    }

    /** A class to parse the Google Places in JSON format  */
    private inner class ParserTask(internal var map: GoogleMap?) :
        AsyncTask<String, Int, List<List<HashMap<String, String>>>>() {

        // Parsing the data in non-ui thread
        override fun doInBackground(
            vararg jsonData: String
        ): List<List<HashMap<String, String>>>? {

            val jObject: JSONObject
            var jArray: JSONArray
            var routes: List<List<HashMap<String, String>>>? = null

            try {
                jObject = JSONObject(jsonData[0])
                val parser = DirectionsJSONParser()
                routes = parser.parse(jObject)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return routes
        }

        // Executes in UI thread, after the parsing process
        override fun onPostExecute(result: List<List<HashMap<String, String>>>) {

            progressDialog.dismiss()

            var points: java.util.ArrayList<LatLng>? = null

            var lineOptions: PolylineOptions? = null

            // MarkerOptions markerOptions = new MarkerOptions();

            // Traversing through all the routes
            for (i in result.indices) {
                points = java.util.ArrayList()
                lineOptions = PolylineOptions()
                // Fetching i-th route
                val path = result[i]
                // Fetching all the points in i-th route
                for (j in path.indices) {
                    // lineOptions = new PolylineOptions();
                    val point = path[j]
                    val lat = java.lang.Double.parseDouble(point[Constants.LAT]!!)
                    val lng = java.lang.Double.parseDouble(point[Constants.LNG]!!)
                    val position = LatLng(lat, lng)
                    points.add(position)
                }
                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points)
                lineOptions.width(9f)
                lineOptions.color(Color.RED)
            }
            // Drawing polyline in the Google Map for the i-th route
            map!!.clear()

            if (null != lineOptions) {
                map!!.addPolyline(lineOptions)
                setStartPosition(srctlat, srclng)
                setDestPosition(destlat, destlng)
                if (map != null) {
                    fixZoom(lineOptions.points)
                }

                fetch();

            } else {
                Toast.makeText(
                    this@TrackerDashboardWithMenu,
                    "Enable to draw routes, Please try again",
                    Toast.LENGTH_LONG
                ).show()
            }

        }
    }

    /**
     * @param lat
     * @param lng
     */
    private fun setStartPosition(lat: Double, lng: Double) {
        var startmark = mMap!!.addMarker(
            MarkerOptions()
                .position(LatLng(lat, lng))
                .title(nsource)
                .snippet("")
        )
        startmark.tag = 1
    }

    /**
     * @param lat
     * @param lng
     */
    private fun setDestPosition(lat: Double, lng: Double) {
        var destmark = mMap!!.addMarker(
            MarkerOptions()
                .position(LatLng(lat, lng))
                .title(ndest)
                .snippet("")
        )

        destmark.tag = 1
    }


    private fun fixZoom(points: List<LatLng>) {
        val bc = LatLngBounds.Builder()
        for (item in points) {
            bc.include(item)
        }
        mMap!!.moveCamera(CameraUpdateFactory.newLatLngBounds(bc.build(), 30))
    }


    private fun initGetUpdateLocation() {
        val getUpdateLocationAsync = GetUpdateLocationAsync()
        getUpdateLocationAsync.setOnLocationUpdateListener(this, newValue)
    }

    override fun onUpdateLocationSuccess(find: Bus) {
        if (null != find) {
            if (null != cNewPosition) {
                cNewPosition!!.remove()
            }
            val latLnt = LatLng(find.clat.toDouble(), find.clng.toDouble())
            cNewPosition = mMap!!.addMarker(
                MarkerOptions().position(latLnt).title("${find.name}").icon(
                    BitmapDescriptorFactory
                        .fromResource(R.mipmap.ic_launcher)
                )
            )
            cNewPosition!!.tag = 1
        }
    }

    private fun fetch() {
        Timer().schedule(timerTask, 5000, 10000)
    }

    private val timerTask = object : TimerTask() {
        override fun run() {
            initGetUpdateLocation()
        }
    }
}
