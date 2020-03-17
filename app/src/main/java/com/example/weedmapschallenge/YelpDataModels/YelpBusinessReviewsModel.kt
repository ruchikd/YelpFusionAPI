package com.example.weedmapschallenge.YelpDataModels

data class YelpBusinessReviewsModel(val reviews: List<YelpBusinessReview>,
                           val total: Int,
                           val possible_languages: List<String>)

data class YelpBusinessReview(val id: String,
                   val url: String,
                   val text: String,
                   val rating: Int,
                   val time_created: String,
                   val user: YelpUser)

//Review Model
data class YelpUser(val id: String,
                val profile_id: String,
                val image_url: String,
                val name: String)
