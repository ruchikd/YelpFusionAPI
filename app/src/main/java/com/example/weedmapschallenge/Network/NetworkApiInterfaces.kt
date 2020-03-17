package com.example.weedmapschallenge.Network

import com.example.weedmapschallenge.YelpDataModels.*
import com.example.weedmapschallenge.UI.SearchBusinesses.Model.BusinessAutocompleteSearch
import com.github.kittinunf.fuel.core.FuelError


interface NetworkApiInterfaces {
    fun getBusiness(businessId: String, onSuccess:(YelpBusinessDataModel) -> Unit, onError:(FuelError) -> Unit)
    fun searchBusinesses(term: String, latitude: Double?, longitude: Double?, limit: Int?, offset: Int?, onSuccess:(List<Business>) -> Unit, onError:(FuelError) -> Unit)
    fun searchAutocompleteBusinesses(text: String, latitude: Double?, longitude: Double?, onSuccess:(BusinessAutocompleteSearch) -> Unit, onError:(FuelError) -> Unit)
    fun getBusinessReview(businessId: String, onSuccess:(YelpBusinessReviewsModel) -> Unit, onError:(FuelError) -> Unit)
    fun searchTransactions(transactionType: String, onSuccess:(BusinessesSearch) -> Unit, onError:(FuelError) -> Unit)
    fun searchBusinessByPhoneNumbers(phoneNumber: String, onSuccess:(BusinessesSearch) -> Unit, onError:(FuelError) -> Unit)

    fun cleared()



}