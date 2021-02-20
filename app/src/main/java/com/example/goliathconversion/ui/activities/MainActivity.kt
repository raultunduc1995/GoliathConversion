package com.example.goliathconversion.ui.activities

import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.*
import com.example.goliathconversion.App
import com.example.goliathconversion.R
import com.example.goliathconversion.ui.viewmodels.MainViewModel
import com.example.goliathconversion.utils.Resource
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_sku_item.view.*
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    companion object {
        private val TAG by lazy { MainActivity::class.java.simpleName }

        private const val PROGRESS_VIEW_Y_TRANSLATION = -300f
        private const val PROGRESS_VIEW_TRANSLATION_DURATION = 1500L
    }

    @Inject
    lateinit var mainViewModel: MainViewModel

    private lateinit var adapter: SkusAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        (applicationContext as App).appComponent.inject(this)
        super.onCreate(savedInstanceState)

        val onClickListener = object : OnItemClickListener {
            override fun onClick(sku: String) {
                startActivity(
                    TransactionDetailsActivity.newIntent(this@MainActivity, sku)
                )
            }
        }
        val initSkusList: () -> Unit = {
            val itemDecoration = DividerItemDecoration(
                skusList.context,
                DividerItemDecoration.VERTICAL
            )

            skusList.adapter = adapter
            skusList.layoutManager = LinearLayoutManager(this)
            skusList.addItemDecoration(itemDecoration)
        }
        val initContent: () -> Unit = {
            setContentView(R.layout.activity_main)
            adapter = SkusAdapter(onClickListener)
            initSkusList()
        }

        if (savedInstanceState == null) {
            initContent()
        }
    }

    override fun onStart() {
        super.onStart()

        val hideLoading: () -> Unit = {
            ObjectAnimator.ofFloat(progressView, "translationY", PROGRESS_VIEW_Y_TRANSLATION)
                .apply {
                    duration = PROGRESS_VIEW_TRANSLATION_DURATION
                    start()
                }
        }
        val onSuccess: (skus: List<String>) -> Unit = { skus ->
            hideLoading()
            adapter.submitList(skus)
        }
        val onError: (errorMessage: String?) -> Unit = { errorMessage ->
            hideLoading()
            Log.e(TAG, "$errorMessage")
        }
        val skusFlowObserver = Observer<Resource<List<String>>> { resource ->
            when (resource) {
                is Resource.Loading -> {
                }
                is Resource.Success -> onSuccess(resource.data!!)
                is Resource.Error -> onError(resource.message)
            }
        }

        mainViewModel.skus.observe(this, skusFlowObserver)
    }
}

@FunctionalInterface
interface OnItemClickListener {
    fun onClick(sku: String)
}

class SkusAdapter(private val onClickListener: OnItemClickListener) :
    ListAdapter<String, RecyclerView.ViewHolder>(DIFF_CALLBACK) {
    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean =
                oldItem == newItem

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean =
                oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.layout_sku_item, parent, false)
        return SkuViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is SkuViewHolder) {
            holder.bind(getItem(position))
        }
    }

    inner class SkuViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(data: String) {
            view.skuButton.text = data
            view.skuButton.setOnClickListener {
                onClickListener.onClick(data)
            }
        }
    }
}
