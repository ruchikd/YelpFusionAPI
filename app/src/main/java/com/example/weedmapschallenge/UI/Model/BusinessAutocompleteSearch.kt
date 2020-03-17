package com.example.weedmapschallenge.UI.SearchBusinesses.Model

import com.example.weedmapschallenge.UI.Model.BusinessNameList
import com.example.weedmapschallenge.UI.Model.BusinessTerms
import com.example.weedmapschallenge.YelpDataModels.Categories

data class BusinessAutocompleteSearch (val categories: List<Categories>,
                                       val businesses: List<BusinessNameList>,
                                       val terms: List<BusinessTerms>)