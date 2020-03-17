package com.example.weedmapschallenge.YelpDataModels

data class BusinessesSearch (val businesses: List<Business>,
                              val total: Int)


data class Business (val id: String,
                     val alias: String,
                     val name: String,
                     val image_url: String,
                     val is_closed: Boolean,
                     val url: String,
                     val review_count: Int,
                     val categories: List<Categories>,
                     val rating: Float,
                     val coordinates: Coordinates,
                     val transactions: List<String>,
                     val price: String,
                     val location: Location,
                     val phone: String,
                     val display_phone: String,
                     val distance: Float){
    override fun toString(): String {
        return this.name
    }
}


data class Coordinates(val latitude: String,
                       val longitude: String)

data class Categories(val alias: String,
                      val title: String)

data class Location(val address1: String,
                    val address2: String,
                    val address3: String,
                    val city: String,
                    val zip_code: String,
                    val country: String,
                    val state: String,
                    val display_address: List<String>,
                    val cross_streets: String)

data class Open(val is_overnight: Boolean, val start: String, val end: String, val day: String)

