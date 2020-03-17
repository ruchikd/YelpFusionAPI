package com.example.weedmapschallenge.Network

import android.util.Log
import com.example.weedmapschallenge.YelpDataModels.*
import com.example.weedmapschallenge.UI.SearchBusinesses.Model.ExploreBusinessModel
import com.example.weedmapschallenge.UI.SearchBusinesses.Model.BusinessAutocompleteSearch
import com.example.weedmapschallenge.Utils.AppData
import com.example.weedmapschallenge.Utils.debugPrintStackTrace
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.extensions.authentication
import com.github.kittinunf.fuel.gson.responseObject

/**
 * @author: Ruchik Samir Dave : ruchikd@gmail.com
 *
 * API's to interact with Yelp Fusion api's
 * Created object class for making it singleton to maintain valid state
 * Used Fuel to implement rest api calls
 * Implemented all interfaces for Yelp's Fusion API @see <a href="https://www.yelp.com/developers/documentation/v3">link</a>:
 * 1. Business details based on its ID
 * 2. Get reviews of business based on its ID
 * 3. Search business based on
 *      a. lalitude, longitude (default Irvine, CA)
 *      b. limit (Currently defined in AppData as max 20)
 *      c. offset based on refreshed data
 *      d. term as text to search for in businesses - Need to be more than 2 valid character
 * 4. Search business based on phone number
 * 5. Search business based on transaction type : delivery
 * 6. Autocomplete business search based on:
 *      a. text - Need to be more than 2 valid characters
 *      b. lalitude, longitude (default Irvine, CA)
 */
object YelpApis : NetworkApiInterfaces {
    private val TAG: String = this::class.java.simpleName

    operator fun invoke(): YelpApis {
        return this
    }

    /**
     * Get Business details based on its ID
     *
     * @param BusinessID, completion block, error block
     * @return YelpBusinessDataModel using block
     */
    override fun getBusiness(
        businessId: String,
        onSuccess: (YelpBusinessDataModel) -> Unit,
        onError: (FuelError) -> Unit
    ) {
        Fuel.get(AppData.BUSINESS_DETAILS_URL + businessId)
            .authentication()
            .bearer(AppData.YELP_BEARER_TOKEN)
            .responseObject<YelpBusinessDataModel> { _, _, result ->
                result.fold({
                    onSuccess(it)
                }, {
                    Log.e(TAG, it.localizedMessage)
                    onError(it)
                    debugPrintStackTrace(it)
                })
            }
    }

    /**
     * Get reviews of business based on its ID
     *
     * @param BusinessID, completion block, error block
     * @return YelpBusinessReviewsModel using block
     */
    override fun getBusinessReview(
        businessId: String,
        onSuccess: (YelpBusinessReviewsModel) -> Unit,
        onError: (FuelError) -> Unit
    ) {
        Fuel.get(AppData.BUSINESS_REVIEW_URL + businessId + "/reviews")
            .authentication()
            .bearer(AppData.YELP_BEARER_TOKEN)
            .responseObject<YelpBusinessReviewsModel> { _, _, result ->
                result.fold({
                    onSuccess(it)
                }, {
                    Log.e(TAG, it.localizedMessage)
                    onError(it)
                    debugPrintStackTrace(it)
                })
            }
    }

    /**
     * Search business based on
     *
     * @param term, lalitude, longitude, limit, offset, completion block, error block
     * @return List<Business> using block
     */
    override fun searchBusinesses(
        term: String,
        latitude: Double?,
        longitude: Double?,
        limit: Int?,
        offset: Int?,
        onSuccess:(List<Business>) -> Unit,
        onError:(FuelError) -> Unit
    ) {
        Log.i(TAG, "searchBusinesses = Calling " + AppData.BUSINESS_SEARCH_URL + " tern : " + term + " latitude : " + latitude.toString() + ", longitude : " + longitude.toString())

        Fuel.get(AppData.BUSINESS_SEARCH_URL, listOf("term" to term, "latitude" to latitude, "longitude" to longitude, "limit" to limit, "offset" to offset))
            .authentication()
            .bearer(AppData.YELP_BEARER_TOKEN)
            .responseObject<ExploreBusinessModel> { _, _, result ->
                result.fold({
                    onSuccess(it.businesses)
                }, {
                    Log.e(TAG, it.localizedMessage)
                    onError(it)
                    debugPrintStackTrace(it)
                })
            }
    }

    /**
     * Search business based on phone number
     *
     * @param phoneNumber, completion block, error block
     * @return BusinessSearch using block
     */
    override fun searchBusinessByPhoneNumbers(
        phoneNumber: String,
        onSuccess: (BusinessesSearch) -> Unit,
        onError: (FuelError) -> Unit
    ) {
        Fuel.get(AppData.BUSINESS_SEARCH_PHONE_URL)
            .authentication()
            .bearer(AppData.YELP_BEARER_TOKEN)
            .responseObject<BusinessesSearch> { _, _, result ->
                result.fold({
                    onSuccess(it)
                }, {
                    Log.e(TAG, it.localizedMessage)
                    onError(it)
                    debugPrintStackTrace(it)
                })
            }
    }

    /**
     * Search business based on transaction type
     *
     * @param transactionType, completion block, error block
     * @return BusinessesSearch using block
     */
    override fun searchTransactions(
        transactionType: String,
        onSuccess: (BusinessesSearch) -> Unit,
        onError: (FuelError) -> Unit
    ) {
        Fuel.get(AppData.BUSINESS_SEARCH_TRANSACTIONS_URL, listOf("transaction_type" to transactionType))
            .authentication()
            .bearer(AppData.YELP_BEARER_TOKEN)
            .responseObject<BusinessesSearch> { _, _, result ->
                result.fold({
                    onSuccess(it)
                }, {
                    Log.e(TAG, it.localizedMessage)
                    onError(it)
                    debugPrintStackTrace(it)
                })
            }
    }

    /**
     * Autocomplete business search based on:
     *
     * @param text, lalitude, longitude, completion block, error block
     * @return BusinessAutocompleteSearch as block
     */
    override fun searchAutocompleteBusinesses(
        text: String,
        latitude: Double?,
        longitude: Double?,
        onSuccess: (BusinessAutocompleteSearch) -> Unit,
        onError: (FuelError) -> Unit
    ) {
        Fuel.get(AppData.AUTOCOMPLETE_URL, listOf("text" to text, "latitude" to latitude, "longitude" to longitude))
            .authentication()
            .bearer(AppData.YELP_BEARER_TOKEN)
            .responseObject<BusinessAutocompleteSearch> { _, _, result ->
                result.fold({
                    onSuccess(it)
                }, {
                    Log.e(TAG, it.localizedMessage)
                    onError(it)
                    debugPrintStackTrace(it)
                })
            }
    }

    override fun cleared() {
    }

}