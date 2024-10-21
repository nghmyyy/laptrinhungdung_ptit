package com.map.nguyen_ha_my.bt2_lancuoi.model

data class Category(
    val id: Int,
    val name: String,
    val idParent: Int,
    val icon: Int, //lay ID tu drawable
    val note: String,
    val inOut: Int
)
