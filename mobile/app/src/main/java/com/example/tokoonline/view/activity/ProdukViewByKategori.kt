package com.example.tokoonline.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tokoonline.R
import com.example.tokoonline.core.base.BaseActivity
import com.example.tokoonline.core.util.OnItemClick
import com.example.tokoonline.data.model.firebase.Produk
import com.example.tokoonline.databinding.ActivityProdukViewByKategoriBinding
import com.example.tokoonline.databinding.ActivityProdukViewByTokoBinding
import com.example.tokoonline.view.adapter.AdapterProdukAll
import com.example.tokoonline.view.viewmodel.AlamatViewModel
import com.example.tokoonline.view.viewmodel.ProdukViewModel
import com.example.tokoonline.view.viewmodel.TokoViewModel

class ProdukViewByKategori : BaseActivity(), OnItemClick {

    private lateinit var binding: ActivityProdukViewByKategoriBinding
    private lateinit var productAdapterAll: AdapterProdukAll
    private lateinit var viewModel : ProdukViewModel
    private lateinit var viewModelToko : TokoViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProdukViewByKategoriBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this).get(ProdukViewModel::class.java)
        viewModelToko = ViewModelProvider(this).get(TokoViewModel::class.java)

        productAdapterAll = AdapterProdukAll(this)
        val recyclerView: RecyclerView = binding.rvProdukAll
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = productAdapterAll

        getData()

        binding.navbar.binding.leftIcon.setOnClickListener{
            finish()
        }

        setContentView(binding.root)

    }

    private fun getData(){
        showProgressDialog()

        val kategoriID = intent.getStringExtra("kategoriID").toString()

        viewModel.loadProdukByKategori(kategoriID){
            binding.tvTextKategori.text = kategoriID
        }

        loadProdukByKategori(kategoriID)
    }

    private fun loadProdukByKategori(kategoriID: String) {
        viewModel.loadProdukByKategori(kategoriID){produkList ->
            dismissProgressDialog()
            productAdapterAll.submitList(produkList)
        }
    }

    override fun onClick(data: Any, position: Int) {
        startActivity(
            DetailProductActivity.createIntent(
                this@ProdukViewByKategori,
                data as Produk
            )
        )
    }
}