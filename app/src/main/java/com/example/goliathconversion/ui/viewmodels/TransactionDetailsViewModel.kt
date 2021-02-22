package com.example.goliathconversion.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.goliathconversion.domain.Transaction
import com.example.goliathconversion.repository.TransactionsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TransactionDetailsViewModel @Inject constructor(
    private val repository: TransactionsRepository
) : ViewModel() {

    fun getSkuTransactions(selectedSku: String) = liveData<List<Transaction>> {
        emit(repository.getAllTransactionsFor(selectedSku))
    }

    fun getTotalSumInEuro(selectedSku: String) = liveData<Double> {
        emit(repository.getTotalSumInEuroFor(selectedSku))
    }
}
