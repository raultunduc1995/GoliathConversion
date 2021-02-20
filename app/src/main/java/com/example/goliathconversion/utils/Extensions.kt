package com.example.goliathconversion.utils

import java.math.BigDecimal
import java.math.RoundingMode

fun Double.roundToHalfEvent(): Double {
    return BigDecimal(this).setScale(2, RoundingMode.HALF_EVEN).toDouble()
}
