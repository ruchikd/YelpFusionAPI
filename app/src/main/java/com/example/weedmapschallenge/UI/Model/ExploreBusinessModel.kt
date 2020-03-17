package com.example.weedmapschallenge.UI.SearchBusinesses.Model

import com.example.weedmapschallenge.YelpDataModels.Business
import com.example.weedmapschallenge.YelpDataModels.Coordinates

data class ExploreBusinessModel (val businesses: MutableList<Business>,
                                 val total: Int,
                                 val region: Coordinates) {
}