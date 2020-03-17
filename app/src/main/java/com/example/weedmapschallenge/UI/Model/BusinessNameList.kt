package com.example.weedmapschallenge.UI.Model

data class BusinessNameList(val id: String, val name: String){
    override fun toString(): String {
        return this.name
    }
}