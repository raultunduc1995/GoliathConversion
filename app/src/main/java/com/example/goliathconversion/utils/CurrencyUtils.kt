package com.example.goliathconversion.utils

import com.example.goliathconversion.domain.CurrencyTypes
import com.example.goliathconversion.domain.Rate

fun mapAudToEur(rates: List<Rate>, amount: Double): Double {
    val rate = rates.first { it.from == CurrencyTypes.AUD && it.to == CurrencyTypes.EUR }

    return (amount * rate.rate).roundToHalfEvent()
}

fun mapUsdToEuro(rates: List<Rate>, amount: Double): Double {
    val rate = rates.first { it.from == CurrencyTypes.USD && it.to == CurrencyTypes.AUD }
    val usdToAudAmount = (amount * rate.rate).roundToHalfEvent()

    return mapAudToEur(rates, usdToAudAmount)
}

fun mapCadToEuro(rates: List<Rate>, amount: Double): Double {
    val rate = rates.first { it.from == CurrencyTypes.CAD && it.to == CurrencyTypes.USD }
    val cadToUsdAmount = (amount * rate.rate).roundToHalfEvent()

    return mapUsdToEuro(rates, cadToUsdAmount)
}

fun computeEuroAmount(rates: List<Rate>, currency: String, amount: Double): Double {
    return when (currency) {
        CurrencyTypes.EUR -> amount.roundToHalfEvent()
        CurrencyTypes.AUD -> mapAudToEur(rates, amount)
        CurrencyTypes.USD -> mapUsdToEuro(rates, amount)
        CurrencyTypes.CAD -> mapCadToEuro(rates, amount)
        else -> amount.roundToHalfEvent()
    }
}
