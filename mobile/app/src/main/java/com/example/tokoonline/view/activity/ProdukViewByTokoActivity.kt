package com.example.tokoonline.view.activity

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tokoonline.core.base.BaseActivity
import com.example.tokoonline.core.util.OnItemClick
import com.example.tokoonline.data.model.firebase.Produk
import com.example.tokoonline.databinding.ActivityProdukViewByTokoBinding
import com.example.tokoonline.view.adapter.AdapterProdukAll
import com.example.tokoonline.view.viewmodel.AlamatViewModel
import com.example.tokoonline.view.viewmodel.ProdukViewModel
import com.example.tokoonline.view.viewmodel.TokoViewModel

class ProdukViewByTokoActivity : BaseActivity(), OnItemClick {

    private lateinit var binding: ActivityProdukViewByTokoBinding
    private lateinit var productAdapterAll: AdapterProdukAll
    private lateinit var viewModel : ProdukViewModel
    private lateinit var viewModelToko : TokoViewModel
    private lateinit var viewModelAlamat : AlamatViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProdukViewByTokoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this).get(ProdukViewModel::class.java)
        viewModelToko = ViewModelProvider(this).get(TokoViewModel::class.java)
        viewModelAlamat = ViewModelProvider(this).get(AlamatViewModel::class.java)


        productAdapterAll = AdapterProdukAll(this)
        val recyclerView: RecyclerView = binding.rvProdukAll
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = productAdapterAll

        getData()

        binding.navbar.binding.leftIcon.setOnClickListener {
            finish()
        }
    }

    private fun getData() {
        showProgressDialog()

        val tokoID = intent.getStringExtra("tokoID").toString()
        val sellerID = intent.getStringExtra("sellerID").toString()

        viewModelToko.getTokoById(tokoID, sellerID){tokoData ->
            binding.tvNamaToko.text = tokoData?.nama
            binding.tvAlamatToko.text = tokoData?.alamat?.alamat ?: ""
        }

        loadProdukbyTokoID(tokoID)
    }


    private fun loadProdukbyTokoID(tokoID: String) {
        viewModel.loadProdukbyIDToko(tokoID) { produkList ->
            dismissProgressDialog()
            productAdapterAll.submitList(produkList) // Submitting the list of products to the adapter
        }
    }




    override fun onClick(data: Any, position: Int) {
        startActivity(
            DetailProductActivity.createIntent(
                this@ProdukViewByTokoActivity,
                data as Produk
            )
        )
    }
}
