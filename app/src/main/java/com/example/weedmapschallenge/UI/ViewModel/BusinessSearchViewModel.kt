package com.example.weedmapschallenge.UI.SearchBusinesses.ViewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weedmapschallenge.YelpDataModels.*
import com.example.weedmapschallenge.Network.YelpApis
import com.example.weedmapschallenge.UI.SearchBusinesses.Model.BusinessesModel

/**
 * @author: Ruchik Samir Dave : ruchikd@gmail.com
 *
 * ViewModel to interact with Yelp Fusion api's
 * Fragments can observe on liveData as:
 *      liveBusinessModelMap
 * liveBusinessModelMap is HashMap with below details:
 * Key: BusinessID
 * Value: BusinessesModel - contains both Business Details with its Reviews
 *
 * Serving Fragments to get :
 *  1. Autocomplete data,
 *  2. Business details based on Business ID
 *  3. Business Reviews based on Business ID
 *  4. Search List of businesses based on term & location
 */

class BusinessSearchViewModel(): ViewModel()  {
    private val TAG: String = this::class.java.simpleName
    private val yelpApis: YelpApis = YelpApis().invoke()

    var businessModelMap: HashMap<String, BusinessesModel> = HashMap()
    var liveBusinessModelMap: MutableLiveData<HashMap<String, BusinessesModel>> = MutableLiveData()

    /**
     * Performs autocomplete search based on text input
     * Calls YelpApis to receive list of business ID
     * Based on list of business ID received calls searchBusinessData to received details of all ID's
     *
     * @param text, latitude, longitude
     *
     */
    fun businessSearchAutoComplete(text: String, latitude: Double?, longitude: Double?){
        yelpApis.searchAutocompleteBusinesses(text, latitude, longitude, {
            for (business in it.businesses){
                val businessId = business.id
                businessModelMap.clear()
                liveBusinessModelMap.value?.clear()
                searchBusinessData(businessId, true)
            }
        }, {
            Log.e(TAG, "Error in requesting autocomplete data for text : " + text + " " + it.localizedMessage)
        })
    }

    /**
     * Performs search on business based on Business ID
     * Updates local businessesModel HashMap with business details as value and business ID as key
     * Calls business reviews for each business ID, if they are requested using boolean param
     *
     * @param businessId, getBusinessReviews
     */
    fun searchBusinessData(businessId: String, getBusinessReviews: Boolean){
        yelpApis.getBusiness(businessId, {
            var businessReviews: List<YelpBusinessReview> = listOf()

            val businessesModel: BusinessesModel = BusinessesModel(it.id, it.name, it.image_url, it.display_phone, it.location, businessReviews)
            if (!businessModelMap.containsKey(it.id)){
                businessModelMap.put(it.id, businessesModel)
            }

            if (getBusinessReviews == true){
                getBusinessReviews(businessId)
            }
        }, {
            Log.e(TAG, "Error in requesting search Business data for business ID : " + businessId + " " + it.localizedMessage)
        })
    }

    /**
     * Performs search on business reviews based on business ID
     * Updates local businessModel HashMap by adding reviews to existing HashMap values with key as BusinessID
     * Then Updates liveBusinessModelMap and posts value for all observers to receive the update
     *
     * @param businessId
     */
    fun getBusinessReviews(businessId: String) {
        yelpApis.getBusinessReview(businessId, {
            val businessModel: BusinessesModel? = businessModelMap.get(businessId)

            if (businessModel != null){
                businessModel.businessReviews = it.reviews
            }
            //liveBusinessModelMap is updated so call post value for all observers to receive data
            liveBusinessModelMap.postValue(businessModelMap)
        }, {
            Log.e(TAG, "Error in requesting search Business reviews for business ID : " + businessId + " " + it.localizedMessage)
        })
    }

    /**
     * Performs search for receiving list of all businesses
     * Updated local BusinessModelMap DS with key as businessID and value as businessModel
     * Call to received reviews for all the businesses received
     *
     * @param term (search text), latitude, longitude, limit (defined in AppData), offset (for pagination)
     */
    fun searchBusinessesData(term: String, latitude: Double?, longitude: Double?, limit: Int, offset: Int){
        yelpApis.searchBusinesses(term, latitude, longitude, limit, offset, {

            //If limit is 1 then the request is for fresh new location
            //So erase all existing data
            if (limit == 1){
                businessModelMap.clear()
                liveBusinessModelMap.value?.clear()
            }

            //For all business in business update businessModelMap
            for (business in it){
                val businessId = business.id
                var businessReviews: List<YelpBusinessReview> = listOf()
                val businessesModel: BusinessesModel = BusinessesModel(business.id, business.name, business.image_url, business.display_phone, business.location, businessReviews)

                if (!businessModelMap.containsKey(businessId)){
                    businessModelMap.put(businessId, businessesModel)
                }

                //For all business in business get reviews and update businessModelMap
                getBusinessReviews(businessId)
            }
        }, {
            Log.e(TAG, "Error in requesting search Business data for term : " + term + " " + it.localizedMessage)
        })
    }
}