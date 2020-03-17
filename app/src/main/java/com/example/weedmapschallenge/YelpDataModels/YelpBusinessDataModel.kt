package com.example.weedmapschallenge.YelpDataModels


data class YelpBusinessDataModel (val id: String,
                            val alias: String,
                            val name: String,
                            val image_url: String,
                            val is_claimed: Boolean,
                            val is_closed: Boolean,
                            val url: String,
                            val phone: String,
                            val display_phone: String,
                            val review_count: Int,
                            val categories: List<Categories>,
                            val rating: Float,
                            val location: Location,
                            val coordinates: com.example.weedmapschallenge.YelpDataModels.Coordinates,
                            val photos: List<String>,
                            val price: String,
                            val hours: List<Open>,
                            val transactions: List<String>)