@file:Suppress("DEPRECATION")

package com.example.weedmapschallenge.UI.SearchBusinesses.View.Fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weedmapschallenge.R
import com.example.weedmapschallenge.UI.SearchBusinesses.Model.BusinessesModel
import com.example.weedmapschallenge.UI.SearchBusinesses.View.ViewAdapter.BusinessSearchRecyclerViewAdapter
import com.example.weedmapschallenge.UI.SearchBusinesses.ViewModel.BusinessSearchViewModel
import com.example.weedmapschallenge.Utils.AppData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import java.util.*

/**
 * BusinessSearchFragment contains below functionsalities:
 * Find city to search
 * Once city is selected, search businesses with search text
 * List all businesses in selected city and search text
 *
 * Display business image, business name, business address, business phone number and top 2 reviews
 */
class BusinessSearchFragment : Fragment()  {
    val TAG: String = this::class.java.simpleName
    private lateinit var rootView: View

    private var businessesModelList : MutableList<BusinessesModel> = mutableListOf()

    private lateinit var searchBusinessSearchViewModel: BusinessSearchViewModel

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var latitude: Double? = null
    var longitude: Double? = null

    lateinit var autoSearchCityButton: Button
    lateinit var autoSearchBusinessTextView: TextView

    lateinit var businessSearchRecyclerView: RecyclerView
    lateinit var businessSearchRecyclerViewAdapter: BusinessSearchRecyclerViewAdapter

    lateinit var placesClient: PlacesClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_search_business, container, false)

        initializeUI(rootView)
        registerListeners()
        initializeCurrentLocation(rootView.context)
        initializeGooglePlaces(rootView.context)

        searchBusinessSearchViewModel = ViewModelProviders.of(this).get(BusinessSearchViewModel::class.java)
        observeBusinessModel(rootView.context)
        updateRecyclerView()

        return rootView
    }

    override fun onResume() {
        super.onResume()
        searchBusinessSearchViewModel = ViewModelProviders.of(this).get(BusinessSearchViewModel::class.java)
        updateRecyclerView()
    }

    /**
     * Inializes all UI components
     *
     */
    fun initializeUI(rootView: View){
        this.autoSearchCityButton = rootView.findViewById(R.id.search_business_city_autocomplete_button)
        this.autoSearchBusinessTextView = rootView.findViewById(R.id.search_business_autocomplete_text_view) as AutoCompleteTextView
        this.businessSearchRecyclerView = rootView.findViewById(R.id.business_list_recycler_view)

        //Set visibility to INVISIBLE until the city is selected
        this.autoSearchBusinessTextView.setVisibility(View.GONE)
    }

    /**
     * Initialize to receive current location
     *
     */
    fun initializeCurrentLocation(context: Context){
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        getLastLocation()
    }

    fun initializeGooglePlaces(context: Context){
        Places.initialize(context, AppData.GOOGLE_PLACES_API_KEY)
        this.placesClient = Places.createClient(context)
    }

    /**
     * Register all listeners like :
     * autocomplete search buttons
     * text for business
     */
    fun registerListeners(){
        this.autoSearchCityButton.setOnClickListener{
            val fields: List<Place.Field> = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS, Place.Field.ADDRESS_COMPONENTS)
            val autoCompleteIntent: Intent = Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields)
                .build(rootView.context)
            startActivityForResult(autoCompleteIntent, AppData.GOOGLE_AUTOSEARCH_CITY_REQUEST_CODE)
        }

        this.autoSearchBusinessTextView.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //Start searching once the input characters are available
                if (s != null && s.length > 0){
                    //Log.i(TAG, "onTextChanged : Text changed  " + s.toString())
                    searchBusinessAutocompleteData(s.toString())
                    businessesModelList.clear()
                    businessSearchRecyclerViewAdapter.notifyDataSetChanged()
                }
            }
        })
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation(){
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                this.latitude = location?.latitude ?: AppData.IRVINE_LATITUDE
                this.longitude = location?.longitude ?: AppData.IRVINE_LONGITUDE
            }
    }

    fun searchBusinessAutocompleteData(text: String) {
        searchBusinessSearchViewModel.businessSearchAutoComplete(text, this.latitude, this.longitude)
    }

    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        if (requestCode == AppData.GOOGLE_AUTOSEARCH_CITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val place = Autocomplete.getPlaceFromIntent(data!!)

                //If location parameters are not correct then default to Irvine location (in AppData.kt)
                this.autoSearchCityButton.text = place.address
                this.latitude = place.latLng?.latitude ?: AppData.IRVINE_LATITUDE
                this.longitude = place.latLng?.longitude ?: AppData.IRVINE_LONGITUDE

                //Set visibility to VISIBLE once the city is selected
                this.autoSearchBusinessTextView.setVisibility(View.VISIBLE)
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                val status = Autocomplete.getStatusFromIntent(data!!)
                Log.i(TAG, status.statusMessage)
            } else if (resultCode == Activity.RESULT_CANCELED) { // The user canceled the operation.
            }
        }
    }

    /**
     * Update recycler view based on adapter set as list of businesses with details as:
     * List of BusinessesModel
     */
    fun updateRecyclerView(){
        businessSearchRecyclerView.setLayoutManager(GridLayoutManager(activity!!.applicationContext, 1))
        businessSearchRecyclerViewAdapter =
            BusinessSearchRecyclerViewAdapter(
                activity!!.applicationContext,
                businessesModelList
            )
        businessSearchRecyclerView.setAdapter(businessSearchRecyclerViewAdapter)
    }

    /**
     * Observe liveBusinessModelMap and if updated update businessModelList by iterating through liveBusinessModelMap HashMap
     * Finally notify recyclerview to update
     */
    fun observeBusinessModel(context: Context) {
        searchBusinessSearchViewModel.liveBusinessModelMap.observe(this, androidx.lifecycle.Observer { businessModelMap ->
            businessesModelList.clear()
            for ((businessId, business) in businessModelMap) {
                businessesModelList.add(business)
                Log.i(TAG, "Adding " + business.businessName + " id : " + business.businessId + " again : " + businessId + " total : " + businessesModelList.size.toString())
            }
            businessSearchRecyclerViewAdapter.notifyDataSetChanged()
        })
    }
}