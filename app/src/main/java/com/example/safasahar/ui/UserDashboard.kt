package com.example.safasahar.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import api.ServiceBuilder
import com.example.safasahar.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import model.LocationModel
import repository.LocationRepository

class UserDashboard : AppCompatActivity(), OnMapReadyCallback {

    private val permissions = arrayOf(
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )

    private var lat: String? = ""
    private var lng: String? = ""
    lateinit var btnMyLocation: Button
    lateinit var btnSendRequest: Button
    lateinit var btnProfile: Button
    private var gMap: GoogleMap? = null
    lateinit var map: MapView
    private var MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey"
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_dashboard)

        if (!hasPermission()) {
            requestPermission()
        }

        var mapViewBundle: Bundle? = null
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY)
        }

        map = findViewById(R.id.map)
        map.onCreate(mapViewBundle)
        map.getMapAsync(this@UserDashboard)
        btnMyLocation = findViewById(R.id.btnMyLocation)
        btnSendRequest = findViewById(R.id.btnSendRequest)
        btnProfile = findViewById(R.id.btnProfile)

        if (lat == "" && lng == "") {
            btnSendRequest.visibility = View.GONE;
            btnMyLocation.visibility = View.VISIBLE;
        }

        btnProfile.setOnClickListener {
            Toast.makeText(this@UserDashboard, "My profile", Toast.LENGTH_SHORT).show()
        }

        btnMyLocation.setOnClickListener {
            getlocation()
            btnMyLocation.visibility = View.GONE;
            btnSendRequest.visibility = View.VISIBLE;
        }

        btnSendRequest.setOnClickListener {
            AlertDialog.Builder(this@UserDashboard)
                .setTitle("Please check the information!")
                .setMessage("Name :" +
                            "\n\nAddress:" +
                            "\n\nPhone Number:"
                )
                .setPositiveButton("Proceed") { _, _ ->
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val location = LocationModel(latitude = lat, longitude = lng)
                            val repository = LocationRepository()
                            val response = repository.addLocation(location)

                            if (response.success == true) {
                                withContext(Dispatchers.Main) {
                                    AlertDialog.Builder(this@UserDashboard)
                                        .setTitle("Success!")
                                        .setMessage(
                                            "Your request has been sent."
                                        )
                                        .setPositiveButton("Ok") { _, _ ->

                                        }.show()
                                }

                                //showHighPriorityNotification()
                                //finish()
                            }

                        } catch (ex: java.lang.Exception) {
                            withContext(Dispatchers.Main) {
                                Log.d("Error", ex.toString())

                                AlertDialog.Builder(this@UserDashboard)
                                    .setTitle("Error!")
                                    .setMessage("Unable to send request!")
                                    .setPositiveButton("Ok") { _, _ ->

                                    }.show()
                            }
                        }
                    }
                }
                .setNegativeButton("Cancel"){_, _ ->

                }
                .show()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map.onResume()
        gMap = googleMap

        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        gMap!!.isMyLocationEnabled = true
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            permissions, 1434
        )
    }

    private fun hasPermission(): Boolean {
        var hasPermission = true
        for (permission in permissions) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                hasPermission = false
            }
        }
        return hasPermission
    }

    private fun getlocation() {
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this)

        try {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                return
            }

            val location = fusedLocationProviderClient!!.lastLocation

            location.addOnCompleteListener { p0 ->
                if (p0.isSuccessful) {

                    val currentLocation = p0.result
                    if (currentLocation != null) {
                        lat = currentLocation.latitude.toString()
                        lng = currentLocation.longitude.toString()
                        moveCamera(
                            LatLng(currentLocation.latitude, currentLocation.longitude), 15f
                        )
                    } else {
                        Toast.makeText(this, "Unable to find current location!", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }

        } catch (ex: Exception) {
            Log.e("location", "Location error!")
        }
    }

    private fun moveCamera(latLng: LatLng, zoom: Float) {
        //gMap!!.addMarker(MarkerOptions().position(latLng).title("yoyo"))
        gMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom))
    }

    override fun onBackPressed() {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Logout")
        builder.setMessage("Do you want to logout?")
        builder.setPositiveButton("YES") { _, _ ->
            logout()
        }
        builder.setNegativeButton("No") { _, _ ->

        }
        val alert: android.app.AlertDialog = builder.create()
        alert.setCancelable(true)
        alert.show()
    }


    private fun logout() {
        ServiceBuilder.token.equals("")
        deleteSharedPref()
        startActivity(
            Intent(
                this@UserDashboard,
                LoginActivity::class.java
            )
        )
        super.onBackPressed()
        //showHighPriorityNotification()
    }

    private fun deleteSharedPref() {
        val sharedPref = getSharedPreferences("user", MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.clear()
        editor.apply()
    }
}