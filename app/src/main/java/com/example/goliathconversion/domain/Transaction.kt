package com.example.goliathconversion.domain

import com.google.gson.annotations.SerializedName

object CurrencyTypes {
    const val EUR = "EUR"
    const val USD = "USD"
    const val CAD = "CAD"
    const val AUD = "AUD"
}

data class Transaction(
    @SerializedName("sku") val sku: String,
    @SerializedName("amount") val amount: Double,
    @SerializedName("currency") val currency: String
) {
    override fun equals(other: Any?): Boolean {
        if (other == null || other !is Transaction) {
            return false
        }

        return other.sku == sku &&
                other.amount == amount &&
                other.currency == currency
    }

    override fun hashCode(): Int {
        var result = sku.hashCode()
        result = 31 * result + amount.hashCode()
        result = 31 * result + currency.hashCode()
        return result
    }
}
