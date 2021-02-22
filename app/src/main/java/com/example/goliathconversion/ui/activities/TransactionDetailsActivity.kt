package com.example.goliathconversion.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.*
import com.example.goliathconversion.R
import com.example.goliathconversion.domain.CurrencyTypes
import com.example.goliathconversion.domain.Transaction
import com.example.goliathconversion.ui.viewmodels.TransactionDetailsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_transaction_details.*
import kotlinx.android.synthetic.main.layout_transaction_details.view.*

@AndroidEntryPoint
class TransactionDetailsActivity : AppCompatActivity() {

    companion object {
        private val TAG by lazy { TransactionDetailsViewModel::class.java.simpleName }

        private const val SELECTED_SKU_KEY = "selected_sku_key"

        fun newIntent(activity: AppCompatActivity, selectedSku: String): Intent =
            Intent(activity, TransactionDetailsActivity::class.java).apply {
                putExtra(SELECTED_SKU_KEY, selectedSku)
            }
    }

    private val viewModel: TransactionDetailsViewModel by viewModels()

    private lateinit var adapter: TransactionsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val initTransactionsList: () -> Unit = {
            val itemDecoration = DividerItemDecoration(
                transactionsList.context,
                DividerItemDecoration.VERTICAL
            )

            transactionsList.adapter = adapter
            transactionsList.layoutManager = LinearLayoutManager(this)
            transactionsList.addItemDecoration(itemDecoration)
        }
        val initContent: () -> Unit = {
            setContentView(R.layout.activity_transaction_details)
            totalSumCurrencyText.text = CurrencyTypes.EUR
            adapter = TransactionsAdapter()
            initTransactionsList()
        }

        if (savedInstanceState == null) {
            initContent()
        }
    }

    override fun onStart() {
        super.onStart()

        val getSkuFromIntent: () -> String = {
            val sku = intent.getStringExtra(SELECTED_SKU_KEY)
            if (sku.isNullOrEmpty()) {
                throw IllegalStateException("Sku is empty...")
            }
            sku
        }
        val skuTransactionsObserver = Observer<List<Transaction>> {
            adapter.submitList(it)
        }
        val totalSumObserver = Observer<Double> {
            totalSumProgress.visibility = View.INVISIBLE
            totalSum.text = it.toString()
            totalSum.visibility = View.VISIBLE
        }

        val selectedSku = getSkuFromIntent()
        viewModel.getSkuTransactions(selectedSku)
            .observe(this, skuTransactionsObserver)
        viewModel.getTotalSumInEuro(selectedSku)
            .observe(this, totalSumObserver)
    }
}

class TransactionsAdapter :
    ListAdapter<Transaction, RecyclerView.ViewHolder>(DIFF_CALLBACK) {
    companion object {
        private const val TRANSACTION_VIEW_TYPE = 1001

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Transaction>() {
            override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
                return oldItem.sku == newItem.sku
            }

            override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
                return oldItem.sku == newItem.sku &&
                        oldItem.amount == newItem.amount &&
                        oldItem.currency == newItem.currency
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.layout_transaction_details, parent, false)

        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is TransactionViewHolder) {
            holder.bind(getItem(position))
        }
    }

    inner class TransactionViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(data: Transaction) {
            view.sku.text = data.sku
            view.currency.text = data.currency
            view.amount.text = data.amount.toString()
        }
    }
}
