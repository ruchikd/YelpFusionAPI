package com.example.weedmapschallenge.UI.SearchBusinesses.View.Fragment

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.weedmapschallenge.YelpDataModels.Business
import com.example.weedmapschallenge.R
import com.example.weedmapschallenge.UI.SearchBusinesses.Model.BusinessesModel
import com.example.weedmapschallenge.UI.SearchBusinesses.View.ViewAdapter.ExploreBusinessRecyclerViewAdapter
import com.example.weedmapschallenge.UI.SearchBusinesses.ViewModel.BusinessSearchViewModel
import com.example.weedmapschallenge.Utils.AppData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

/**
 * ExploreBusinessFragment contains below functionalities:
 * List all businesses based on current location
 *
 * Display business image, business name, business address, business phone number and top 2 reviews
 */

class ExploreBusinessFragment : Fragment() {
    private val TAG: String = this::class.java.simpleName

    private lateinit var rootView: View
    private lateinit var exploreBusinessRecyclerView: RecyclerView

    private lateinit var businessSearchViewModel:BusinessSearchViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    lateinit var exploreBusinesshRecyclerViewAdapter: ExploreBusinessRecyclerViewAdapter
    lateinit var exploreBusinessLayoutManager: RecyclerView.LayoutManager
    lateinit var searchText: TextView

    var searchTerm: String = ""
    var businessList: MutableList<Business> = mutableListOf()

    private var businessesModelList : MutableList<BusinessesModel> = mutableListOf()


    var latitude: Double? = AppData.IRVINE_LATITUDE
    var longitude: Double? = AppData.IRVINE_LONGITUDE

    private var offset: Int = 1
    private var queriedBusinessOnCreate = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_explore_business, container, false)

        this.businessList.clear()
        initializeCurrentLocation()
        queriedBusinessOnCreate = true

        initializeUI()

        businessSearchViewModel = ViewModelProviders.of(this).get(BusinessSearchViewModel::class.java)
        observeBusinessModel(rootView.context)
        updateRecyclerView()

        return rootView
    }

    override fun onResume() {
        super.onResume()
        this.businessList.clear()

        if (queriedBusinessOnCreate == true){
            queriedBusinessOnCreate = false
        } else {
            initializeCurrentLocation()
        }

        businessSearchViewModel = ViewModelProviders.of(this).get(BusinessSearchViewModel::class.java)

        updateRecyclerView()
    }

    /**
     * Call for new business data once new location is set
     * reset offset value to 1 and clear existing list of all BusinessModel
     */
    fun requestBusinessDataForNewLocation(){
        //this.businessList.clear()
        this.businessesModelList.clear()
        this.offset = 1
        this.businessSearchViewModel.searchBusinessesData(this.searchTerm, this.latitude, this.longitude, AppData.MAX_YELP_BUSINESS_QUERY_LIMIT, this.offset)
        this.swipeRefreshLayout.isRefreshing = true
    }

    /**
     * Call for more business data if user swiped
     * Update the offset to + 1
     */
    fun refreshBusinessData(){
        this.offset += 1
        this.businessSearchViewModel.searchBusinessesData(this.searchTerm, this.latitude, this.longitude, AppData.MAX_YELP_BUSINESS_QUERY_LIMIT, this.offset)
        this.swipeRefreshLayout.isRefreshing = true
    }

    fun initializeUI(){
        this.exploreBusinessRecyclerView = rootView.findViewById(R.id.explore_business_list_recycler_view)
        this.swipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh_layout)
        this.searchText = rootView.findViewById(R.id.explore_text_view) as AutoCompleteTextView

        registerAllListeners()
    }

    /**
     * Register all listerners like
     * Swipe refresh layout
     * Search Text
     */
    fun registerAllListeners(){
        this.swipeRefreshLayout.setOnRefreshListener {
            refreshBusinessData()
        }

        this.searchText.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //Search for businesses with search text containing few characters
                if (s != null && s.length > 1){
                    searchTerm = s.toString()
                    Log.i(TAG, "Changed text to " + searchTerm)
                    requestBusinessDataForNewLocation()
                }
            }
        })
    }

    /**
     * Get current location
     */
    fun initializeCurrentLocation(){
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!.applicationContext)
        getLastLocation()
    }

    fun updateRecyclerView(){
        exploreBusinessRecyclerView.setLayoutManager(GridLayoutManager(activity!!.applicationContext, 1))
        exploreBusinesshRecyclerViewAdapter =
            ExploreBusinessRecyclerViewAdapter(
                this.activity!!.applicationContext,
                businessesModelList
            )
        exploreBusinessRecyclerView.setAdapter(exploreBusinesshRecyclerViewAdapter)
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation(){
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    this.latitude = location?.latitude
                    this.longitude = location?.longitude
                    Log.i(TAG, "################################## Got new Location : " + this.latitude + " & " + this.longitude)
                    requestBusinessDataForNewLocation()
                }
            }
    }

    /**
     * Observe liveBusinessModelMap. Update businessModelList by iterating through liveBusinessModelMap HashMap
     * Finally notify recyclerview to update
     */
    fun observeBusinessModel(context: Context) {
        businessSearchViewModel.liveBusinessModelMap.observe(this, androidx.lifecycle.Observer { businessModelMap ->
            businessesModelList.clear()
            Log.i(TAG, "businessModelMap size = " + businessModelMap.size.toString() + " businessModelList size = " + businessesModelList.size)
            for ((businessId, business) in businessModelMap) {
                businessesModelList.add(business)
                Log.i(TAG, "Adding " + business.businessName + " id : " + business.businessId + " total : " + businessesModelList.size.toString())
            }
            if (this.swipeRefreshLayout.isRefreshing) {
                this.swipeRefreshLayout.setRefreshing(false)
            }
            exploreBusinesshRecyclerViewAdapter.notifyDataSetChanged()
        })
    }

}