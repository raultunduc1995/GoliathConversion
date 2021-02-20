package com.example.goliathconversion.repository.api

import com.example.goliathconversion.domain.Rate
import com.example.goliathconversion.domain.Transaction
import kotlinx.coroutines.Deferred
import retrofit2.http.GET

interface TransactionsApi {
    @GET("rates.json")
    fun getRatesAsync(): Deferred<List<Rate>>

    @GET("transactions.json")
    fun getTransactionAsync(): Deferred<List<Transaction>>
}