package com.example.weedmapschallenge.UI.SearchBusinesses.Model

import com.example.weedmapschallenge.YelpDataModels.Location
import com.example.weedmapschallenge.YelpDataModels.YelpBusinessReview

data class BusinessesModel (var businessId: String,
                            var businessName: String,
                            var businessImage: String,
                            var businessPhoneNumber: String,
                            var businessLocation: Location,
                            var businessReviews: List<YelpBusinessReview>) {
}
