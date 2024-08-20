package com.harsh.mapapp

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.health.connect.datatypes.ExerciseRoute.Location
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.tasks.Task
import com.harsh.mapapp.databinding.ActivityMapsBinding


class MapsActivity : AppCompatActivity(), OnMapReadyCallback,LocationListener {

    private lateinit var mMap: GoogleMap
    private val FINE_PERMISSION: Int = 1
    private var location : Location? = null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        val mapFragment = supportFragmentManager
//            .findFragmentById(R.id.map) as SupportMapFragment
//        mapFragment.getMapAsync(this)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        getLastLocation()

        var mapFragment : SupportMapFragment? = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?

        mapFragment?.getMapAsync(this)

    }





    override fun onMapReady(p0: GoogleMap) {
        mMap = p0

        getLastLocation()


    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == FINE_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation()
            } else {
                Toast.makeText(this,"Permission Denied",Toast.LENGTH_SHORT).show()
            }
        }
    }


    fun getLastLocation(){


        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),FINE_PERMISSION)
            return
        }

        var task : Task<android.location.Location> = fusedLocationProviderClient.getLastLocation()

        task.addOnSuccessListener(){
                location?.let {
                    this.location = it

                }

                if (it!= null){
                    var jdpLatLng : LatLng = LatLng(it!!.latitude,it!!.longitude)
                    var ajmerLatLng : LatLng = LatLng(26.475809,74.654603)
                    var markerOptions : MarkerOptions = MarkerOptions().position(jdpLatLng).title("Current Location")
                    var markerOptionsajmer : MarkerOptions = MarkerOptions().position(ajmerLatLng).title("Current Location")
                    mMap.addMarker(markerOptionsajmer)
                    mMap.addMarker(markerOptions)
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(jdpLatLng))
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(jdpLatLng,16f))

                    val options = PolylineOptions()
                    options.color(Color.RED)
                    options.width(5f)

                    val url = getURL(jdpLatLng, ajmerLatLng)

                    

//                    val TamWorth = LatLng(-31.083332, 150.916672)
//                    val NewCastle = LatLng(-32.916668, 151.750000)
//                    val Brisbane = LatLng(-27.470125, 153.021072)
//
//                    mMap.addPolyline(
//                        PolylineOptions().add( TamWorth, Brisbane)
//                            .width // below line is use to specify the width of poly line.
//                                (5f) // below line is use to add color to our poly line.
//                            .color(Color.RED) // below line is to make our poly line geodesic.
//                            .geodesic(true)
//                    )
//
//
//                    // on below line we will be starting the drawing of polyline.
//                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Brisbane, 13f))
                }

        }

    }
    var mCurrLocationMarker: Marker? = null
    var latLng: LatLng? = null

    override fun onLocationChanged(p0: android.location.Location) {
        latLng = LatLng(location!!.latitude, location!!.longitude)
        val markerOptions = MarkerOptions()
        markerOptions.position(latLng!!)
        markerOptions.title("My Location")
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))

        if (mCurrLocationMarker == null) { // Add marker and move camera on first time
            mCurrLocationMarker = mMap.addMarker(markerOptions)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng!!, 13f))
        } else { // Update existing marker position and move camera if required
            mCurrLocationMarker!!.position = latLng!!
            //        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
        }
    }

    private fun getURL(from : LatLng, to : LatLng) : String {
        val origin = "origin=" + from.latitude + "," + from.longitude
        val dest = "destination=" + to.latitude + "," + to.longitude
        val sensor = "sensor=false"
        val params = "$origin&$dest&$sensor"
        return "https://maps.googleapis.com/maps/api/directions/json?$params"
    }

}

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
//    override fun onMapReady(googleMap: GoogleMap) {
//        mMap = googleMap
//
//        // Add a marker in Sydney and move the camera
//        val sydney = LatLng(-34.0, 151.0)
//        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
//    }
