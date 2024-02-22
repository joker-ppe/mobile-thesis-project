package com.eddiez.plantirrigsys.view.fragment

import android.Manifest
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.eddiez.plantirrigsys.R
import com.eddiez.plantirrigsys.base.BaseFragment
import com.eddiez.plantirrigsys.dataModel.ScheduleDataModel
import com.eddiez.plantirrigsys.databinding.FragmentMapScheduleBinding
import com.eddiez.plantirrigsys.utilities.AppConstants
import com.eddiez.plantirrigsys.view.activity.ViewScheduleActivity
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener

/**
 * A simple [Fragment] subclass.
 * Use the [MapScheduleFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MapScheduleFragment : BaseFragment(), OnMapReadyCallback {
    private lateinit var binding: FragmentMapScheduleBinding
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapScheduleBinding.inflate(layoutInflater)

        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                if (isGranted) {
                    getCurrentLocation(mMap)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Location permission denied",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val autocompleteSupportFragment = (childFragmentManager
            .findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment).setPlaceFields(
            listOf(Place.Field.LAT_LNG, Place.Field.NAME)
        )

        autocompleteSupportFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                Log.i("MapScheduleFragment", "Place: ${place.name}, ${place.latLng}")
                place.latLng?.let { CameraUpdateFactory.newLatLngZoom(it, 15f) }
                    ?.let { mMap.moveCamera(it) }
            }

            override fun onError(status: Status) {
                Log.i("MapScheduleFragment", "An error occurred: $status")
            }
        })

        binding.fabGetCurrentLocation.setOnClickListener {
            getCurrentLocation(mMap)
        }

        // observeData()
    }

    private fun drawMarker(listSchedule: List<ScheduleDataModel>) {
        for (schedule in listSchedule) {
            val latLng = LatLng(schedule.latitude!!, schedule.longitude!!)

            val markerView =
                (requireActivity().getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater)
                    .inflate(R.layout.marker_schedule_layout, null)
            val cardView = markerView.findViewById<CardView>(R.id.markerCardView)
            val tvScheduleName = markerView.findViewById<TextView>(R.id.tvScheduleName)
            val imgSchedule = markerView.findViewById<ImageView>(R.id.imgSchedule)

            tvScheduleName.text = schedule.title

            Glide.with(binding.root)
                .asBitmap()
                .load(schedule.imageData)
                .placeholder(R.drawable.image_default)
                .error(R.drawable.image_default)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgSchedule)

            val bitmap = Bitmap.createScaledBitmap(
                viewToBitmap(cardView)!!,
                cardView.width,
                cardView.height,
                false
            )

            val markerIcon = BitmapDescriptorFactory.fromBitmap(bitmap)

            val markerOptions = MarkerOptions().position(latLng).icon(markerIcon)
            val marker = mMap.addMarker(markerOptions)
            if (marker != null) {
                marker.tag = schedule
            }
        }
    }

    private fun viewToBitmap(view: View): Bitmap? {
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val bitmap =
            Bitmap.createBitmap(view.measuredWidth, view.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
        view.draw(canvas)
        return bitmap
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        // You can now interact with the map
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
//            getCurrentLocation(mMap)

            mMap.isMyLocationEnabled = true


            // Set a listener for marker click.
            mMap.setOnMarkerClickListener { marker ->
                // Retrieve the data from the marker
                val schedule = marker.tag as ScheduleDataModel

                val intent = Intent(requireContext(), ViewScheduleActivity::class.java)
                intent.putExtra(AppConstants.SCHEDULE, schedule)
                startActivity(intent)

                true // Return true to indicate that we have consumed the event and that we do not wish for the default behavior to occur (which is for the camera to move such that the marker is centered and for the marker's info window to open, if it has one).
            }

            getCurrentLocation(mMap)

            // get data from server
            userViewModel.accessToken.observe(viewLifecycleOwner) { token ->
                if (token.isNotEmpty()) {
                    scheduleViewModel.getPublicSchedule(token)
                }
            }

            scheduleViewModel.publicSchedules.observe(viewLifecycleOwner) {
                if (it != null) {
                    drawMarker(it)
                }
            }

        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun getCurrentLocation(mMap: GoogleMap) {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
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
                    val currentLatLng = LatLng(it.latitude, it.longitude)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                }
            }
            .addOnFailureListener {
                // Handle failure
                Log.e("MapScheduleFragment", it.message.toString())
            }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MapScheduleFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            MapScheduleFragment()
    }
}