package com.eddiez.plantirrigsys.view.activity

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.eddiez.plantirrigsys.R
import com.eddiez.plantirrigsys.base.BaseActivity
import com.eddiez.plantirrigsys.databinding.ActivityMapsBinding
import com.eddiez.plantirrigsys.utilities.AppConstants
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import java.util.Locale

class MapsActivity : BaseActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val LOCATION_PERMISSION_REQUEST_CODE = 1
    private var latitude: Double = AppConstants.LATITUDE_DEFAULT
    private var longitude: Double = AppConstants.LONGITUDE_DEFAULT


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the PlacesClient
//        val placesClient: PlacesClient = Places.createClient(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val autocompleteSupportFragment = (supportFragmentManager
            .findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment).setPlaceFields(
            listOf(Place.Field.LAT_LNG, Place.Field.NAME)
        )

        autocompleteSupportFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                Log.i("MapsActivity", "Place: ${place.name}, ${place.latLng}")
                mMap.clear()
                mMap.addMarker(MarkerOptions().position(place.latLng!!).title(place.name))
                place.latLng?.let { CameraUpdateFactory.newLatLngZoom(it, 15f) }
                    ?.let { mMap.moveCamera(it) }

                place.latLng?.let { getAddress(it.latitude, it.longitude) }
            }

            override fun onError(status: Status) {
                Log.i("MapsActivity", "An error occurred: $status")
            }
        })

        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }

        binding.fabGetCurrentLocation.setOnClickListener {
            getCurrentLocation(mMap)
        }

        binding.btnConfirm.setOnClickListener {
            val intent = intent
            intent.putExtra(AppConstants.SCHEDULE_LATITUDE, latitude)
            intent.putExtra(AppConstants.SCHEDULE_LONGITUDE, longitude)
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add this code
        mMap.setOnMapClickListener { latLng ->
            // Clear the existing marker
            mMap.clear()

            // Add a marker at the clicked location
            mMap.addMarker(MarkerOptions().position(latLng).title("Selected Location"))

            // Move the camera to the clicked location
//            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))

            // Get the address of the clicked location
            getAddress(latLng.latitude, latLng.longitude)
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
//            getCurrentLocation(mMap)

            mMap.isMyLocationEnabled = true

            // get data
            val scheduleName = intent.getStringExtra(AppConstants.SCHEDULE_NAME)
            val scheduleLatitude = intent.getDoubleExtra(AppConstants.SCHEDULE_LATITUDE, 0.0)
            val scheduleLongitude = intent.getDoubleExtra(AppConstants.SCHEDULE_LONGITUDE, 0.0)

            binding.tvScheduleName.text = scheduleName

            if (scheduleLatitude == AppConstants.LATITUDE_DEFAULT && scheduleLongitude == AppConstants.LONGITUDE_DEFAULT) {
                getCurrentLocation(mMap)
            } else {
                mMap.clear()
                val currentLatLng = LatLng(scheduleLatitude, scheduleLongitude)
                mMap.addMarker(
                    MarkerOptions().position(currentLatLng).title("${scheduleName}'s Location")
                )
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))

                getAddress(scheduleLatitude, scheduleLongitude)
            }

        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    getCurrentLocation(mMap)
                } else {
                    // Permission denied, show a toast message
                    Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
                }
                return
            }

            else -> {
                // Ignore all other requests
            }
        }
    }

    private fun getCurrentLocation(mMap: GoogleMap) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                // Use the location object to get latitude and longitude
                location?.let {
                    mMap.clear()
                    val currentLatLng = LatLng(it.latitude, it.longitude)
                    mMap.addMarker(
                        MarkerOptions().position(currentLatLng).title("Current Location")
                    )
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))


                    getAddress(it.latitude, it.longitude)
                }
            }
            .addOnFailureListener {
                // Handle failure
                Log.e("MapsActivity", it.message.toString())
            }
    }

    private fun setInformation(address: Address) {
        // update local value
        latitude = address.latitude
        longitude = address.longitude

        binding.tvLongitudeValue.text = String.format("%.3f", address.longitude)
        binding.tvLatitudeValue.text = String.format("%.3f", address.latitude)

        binding.tvAddress.text = address.getAddressLine(0)
    }

    private fun getAddress(latitude: Double, longitude: Double) {
        val geocoder = Geocoder(this, Locale.getDefault())
        var address: Address? = null

        try {
            //Fetch address from location
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocation(
                    latitude,
                    longitude,
                    1,
                    object : Geocoder.GeocodeListener {
                        override fun onGeocode(addresses: MutableList<Address>) {
                            address = addresses[0]

                            setInformation(address!!)
                        }

                        override fun onError(errorMessage: String?) {
                            super.onError(errorMessage)

                        }

                    })
            } else {
                address =
                    geocoder.getFromLocation(latitude, longitude, 1)!![0]

                setInformation(address!!)
            }
        } catch (e: Exception) {
            Log.e("MapsActivity", e.message.toString())
            Toast.makeText(this, "Error when get location. Try again", Toast.LENGTH_SHORT).show()
        }

        Log.d("MapsActivity", "Address: ${address?.getAddressLine(0)}")
    }
}