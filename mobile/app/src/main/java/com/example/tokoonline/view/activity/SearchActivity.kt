package com.example.tokoonline.view.activity

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView.OnEditorActionListener
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tokoonline.R
import com.example.tokoonline.core.base.BaseActivity
import com.example.tokoonline.core.util.OnItemClick
import com.example.tokoonline.core.util.changeStatusBar
import com.example.tokoonline.core.util.gone
import com.example.tokoonline.core.util.visible
import com.example.tokoonline.data.model.firebase.Produk
import com.example.tokoonline.data.repository.firebase.ProdukRepository
import com.example.tokoonline.databinding.ActivitySearchBinding
import com.example.tokoonline.view.adapter.SearchHistoryAdapter
import com.example.tokoonline.view.adapter.SearchResultAdapter

class SearchActivity : BaseActivity() {

    private val produkRepository: ProdukRepository by lazy {
        ProdukRepository.getInstance()
    }

    private lateinit var binding: ActivitySearchBinding
    private val adapter: SearchResultAdapter by lazy {
        SearchResultAdapter(object : OnItemClick {
            override fun onClick(data: Any, position: Int) {
                startActivity(
                    DetailProductActivity.createIntent(
                        this@SearchActivity,
                        data as Produk
                    )
                )
            }
        })
    }
    private var searchableKeyword = ""
//    private val KEY_SEARCH_HISTORY = "history_idUser"

    companion object {
        private const val KEY_SEARCH_HISTORY = "history_idUser"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        changeStatusBar(R.color.white)

        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        initListener()
    }

    private fun initView() {

        binding.rvSearchResult.adapter = adapter

        val searchHistoryAdapter = SearchHistoryAdapter { selectedSearch ->
            binding.searchbar.setText(selectedSearch)
            searchProduct(selectedSearch)
        }
        binding.rvSearchHistory.adapter = searchHistoryAdapter
        binding.rvSearchHistory.layoutManager = LinearLayoutManager(this)

        // Load and display search history
        val userId = userRepository.uid
        val searchHistory = userId?.let { getSearchHistory(it).toList() }
        searchHistoryAdapter.submitList(searchHistory)

        if (searchHistory != null) {
            binding.btnClearHistory.visibility = if (searchHistory.isNotEmpty()) View.VISIBLE else View.GONE
        }

        binding.btnClearHistory.setOnClickListener {
            if (userId != null) {
                clearSearchHistory(userId)
            }
            searchHistoryAdapter.submitList(emptyList())
            binding.btnClearHistory.visibility = View.GONE
        }
    }

    private fun initListener() = with(binding) {
        btnBack.setOnClickListener {
            finish()
        }

        searchbar.addTextChangedListener {
            searchableKeyword = it.toString()
        }

        searchbar.setOnEditorActionListener(OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchProduct(searchableKeyword)
                return@OnEditorActionListener true
            }
            false
        })

        btnSearch.setOnClickListener {
            searchProduct(searchableKeyword)
        }
    }

    private fun saveSearchHistory(searchableKeyword: String, userId: String) {
        // Get existing search history for the current user
        val existingHistory = getSearchHistory(userId)

        // Add the new search term
        existingHistory.add(searchableKeyword)

        // Save the updated search history back to SharedPreferences
        val key = "${KEY_SEARCH_HISTORY}_$userId"
        val editor = getPreferences(MODE_PRIVATE).edit()
        editor.putStringSet(key, existingHistory)
        editor.apply()
    }

    private fun getSearchHistory(userId: String): MutableSet<String> {
        val sharedPreferences = getPreferences(MODE_PRIVATE)
        val key = "${KEY_SEARCH_HISTORY}_$userId"
        return sharedPreferences.getStringSet(key, HashSet())?.toMutableSet() ?: mutableSetOf()
    }


    private fun searchProduct(searchableKeyword: String) {
        if (searchableKeyword.isEmpty()) return

        val userId = userRepository.uid

        // Save search history with user ID
        if (userId != null) {
            saveSearchHistory(searchableKeyword, userId)
        }

        showProgressDialog()
        produkRepository.searchProduct(searchableKeyword.lowercase()) { isSuccess, data ->
            try {
                if (isSuccess && data?.isNotEmpty() == true) {
                    // Filter out products with idSeller equal to userRepository.uid
                    val filteredData = data.filterNotNull().filter { produk ->
                        produk.idSeller != userRepository.uid
                    }
                    binding.btnClearHistory.gone()
                    adapter.submitData(filteredData)

                    if (binding.containerSearchNotFound.isVisible) {
                        binding.containerSearchNotFound.gone()
                    }
                } else throw Exception("data is null")
            } catch (e: Exception) {
                binding.containerSearchNotFound.visible()
                binding.btnClearHistory.gone()

            }
            dismissProgressDialog()
        }
    }


    private fun clearSearchHistory(userId: String) {
        val key = "${KEY_SEARCH_HISTORY}_$userId"
        val editor = getPreferences(MODE_PRIVATE).edit()
        editor.remove(key)
        editor.apply()
    }

//    override fun onPause() {
//        super.onPause()
//        if (progressDialog != null && progressDialog.isShowing) {
//            progressDialog.dismiss()
//        }
//    }


}