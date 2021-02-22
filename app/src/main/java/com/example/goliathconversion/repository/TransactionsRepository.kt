package com.example.goliathconversion.repository

import com.example.goliathconversion.domain.Rate
import com.example.goliathconversion.domain.Transaction
import com.example.goliathconversion.repository.api.TransactionsApi
import com.example.goliathconversion.utils.computeEuroAmount
import com.example.goliathconversion.utils.roundToHalfEvent
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import javax.inject.Inject

interface TransactionsRepository {
    suspend fun loadTransactionDetails(): List<Transaction>
    fun getDistinctSkus(): List<String>
    fun getAllTransactionsFor(sku: String): List<Transaction>
    suspend fun getTotalSumInEuroFor(sku: String): Double
}
class TransactionsRepositoryImpl @Inject constructor(private val transactionsApi: TransactionsApi): TransactionsRepository {
    private val rates = mutableListOf<Rate>()
    private val transactions = mutableListOf<Transaction>()

    private suspend fun loadRatesAndTransactions() {
        val responses = listOf(
            transactionsApi.getRatesAsync(),
            transactionsApi.getTransactionAsync()
        ).awaitAll()

        delay(2000)
        rates.addAll(responses[0] as Collection<Rate>)
        transactions.addAll(responses[1] as Collection<Transaction>)
    }

    override suspend fun loadTransactionDetails(): List<Transaction> {
        if (transactions.isEmpty() || rates.isEmpty()) {
            loadRatesAndTransactions()
        }

        return transactions
    }

    override fun getDistinctSkus(): List<String> = transactions
        .distinctBy { it.sku }
        .map { it.sku }

    override fun getAllTransactionsFor(sku: String): List<Transaction> {
        val matchSkuPredicate: (transaction: Transaction) -> Boolean = {
            it.sku == sku
        }

        return transactions.filter(matchSkuPredicate)
    }

    override suspend fun getTotalSumInEuroFor(sku: String): Double {
        val filteredSkus = getAllTransactionsFor(sku)
        var totalEuroSum = 0.0

        delay(1000)
        filteredSkus.forEach { transaction ->
            totalEuroSum += computeEuroAmount(this.rates, transaction.currency, transaction.amount)
        }

        return totalEuroSum.roundToHalfEvent()
    }
}
