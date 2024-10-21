package com.map.nguyen_ha_my.bt2_lancuoi.model

import java.util.Date

data class Transaction(
    val id: Int,
    val name: String,
    val idCatInOut: Int,
    val amount: Double,
    val date: Date,
    val note: String
)
