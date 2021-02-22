package com.example.goliathconversion.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.goliathconversion.repository.TransactionsRepository
import com.example.goliathconversion.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val transactionsRepository: TransactionsRepository
) : ViewModel() {

    val skus = liveData<Resource<List<String>>> {
        val loadDistinctSkus: suspend () -> Unit = {
            emit(Resource.Loading())
            transactionsRepository.loadTransactionDetails()
            emit(
                Resource.Success(transactionsRepository.getDistinctSkus())
            )
        }

        try {
            loadDistinctSkus()
        } catch (error: Exception) {
            emit(
                Resource.Error(error.message!!)
            )
        }
    }
}
