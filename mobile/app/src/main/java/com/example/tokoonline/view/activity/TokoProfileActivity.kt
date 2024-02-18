package com.example.tokoonline.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.tokoonline.core.base.BaseActivity
import com.example.tokoonline.data.model.firebase.Transaction
import com.example.tokoonline.databinding.ActivityTokoProfileBinding
import com.example.tokoonline.view.activity.DetailPesananTokoActivity.Companion.STATUS_CANCELED
import com.example.tokoonline.view.activity.DetailPesananTokoActivity.Companion.STATUS_PENDING
import com.example.tokoonline.view.activity.DetailPesananTokoActivity.Companion.STATUS_SUCCESS
import com.example.tokoonline.view.activity.toko.pendapatan.PendapatanActivity
import com.example.tokoonline.view.activity.toko.pesanan.StatusPesananActivity
import com.example.tokoonline.view.viewmodel.AlamatViewModel
import com.example.tokoonline.view.viewmodel.TokoViewModel
import com.example.tokoonline.view.viewmodel.TransactionViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TokoProfileActivity : BaseActivity() {
    private lateinit var binding: ActivityTokoProfileBinding
    private lateinit var viewModelToko: TokoViewModel
    private lateinit var viewModelAlamat: AlamatViewModel
    private lateinit var viewModel: TransactionViewModel

    private var userHasToko: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTokoProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        showProgressDialog()
        viewModelToko = ViewModelProvider(this)[TokoViewModel::class.java]
        viewModelAlamat = ViewModelProvider(this)[AlamatViewModel::class.java]
        viewModel = ViewModelProvider(this)[TransactionViewModel::class.java]

        initView()
        initClickListener()
        getStatusTransaction()
    }

    private fun getStatusTransaction() {
        viewModel.getTransactionBySellerId(userRepository.uid ?: "") { transactionList ->
            binding.tvDikirim.text = transactionList.getCountStatus(STATUS_PENDING)
            binding.tvPembatalan.text = transactionList.getCountStatus(STATUS_CANCELED)
            binding.tvSelesai.text = transactionList.getCountStatus(STATUS_SUCCESS)
        }
    }

    private fun List<Transaction>.getCountStatus(status: String): String {
        return filter { it.status.equals(status, ignoreCase = true) }
            .toList().size.toString()
    }

    private fun initClickListener() {
        binding.optionStatusPesanan.setOnClickListener {
            startActivity(Intent(this, StatusPesananActivity::class.java))
        }

        binding.optionKeuangan.setOnClickListener {
            startActivity(Intent(this, PendapatanActivity::class.java))
        }
    }

    private fun initView() {
        val emptyView: LinearLayout = binding.tokoNull
        val viewOn: LinearLayout = binding.viewOnToko

        val userId = userRepository.uid

        if (userId != null) {
            lifecycleScope.launch {
                delay(200)
                dismissProgressDialog()
            }
            viewModelToko.checkUserHasToko(userId) { hasToko ->
                userHasToko = hasToko
                if (userHasToko) {
                    // User has a toko, show toko details
                    emptyView.visibility = View.GONE
                    viewOn.visibility = View.VISIBLE

                    binding.toolbar.binding.leftIcon.setOnClickListener {
                        finish()
                    }
                    getTokoData()
                } else {
                    // User doesn't have a toko, show button to add a new toko
                    emptyView.visibility = View.VISIBLE
                    viewOn.visibility = View.GONE

                    binding.toolbarNull.binding.leftIcon.setOnClickListener {
                        finish()
                    }
                    binding.btnTambahToko.setOnClickListener {
                        // Handle the case for adding a new toko
                        val intent = Intent(this, TambahTokoBaruActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
        } else {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        if (userHasToko) {
            getTokoData()
            getStatusTransaction()
        }
    }

    private fun getTokoData() {
        val userUid = userRepository.uid.toString()

        viewModelToko.getTokoData(userUid) { toko ->
            if (toko != null) {
                val tokoID = toko.id
                binding.tvNamaToko.text = toko.nama
                binding.tvNamaToko1.text = toko.nama
                binding.tvEmail.text = userRepository.email
                binding.tvPhone.text = userRepository.phone
                viewModelAlamat.getAlamatById(toko.id_alamat, userUid) { alamatToko ->
                    if (alamatToko != null) {
                        binding.tvAlamatToko.text = alamatToko.alamat
                    } else {
                        showToast("Gagal mengambil Alamat Toko")
                        viewModelAlamat.getAlamatDefault(userUid){alamat ->
                            binding.tvAlamatToko.text = alamat?.alamat
                        }
                    }
                }
                binding.optionProduk.setOnClickListener{goToProdukSaya(tokoID)}
                binding.optionPengaturan.setOnClickListener {
                    val intent = Intent(this, TokoSettingActivity::class.java)
                    intent.putExtra("tokoID", tokoID)
                    startActivity(intent)
                }
            } else {
                showToast("Gagal mengambil Nama Toko")
            }
        }
    }
}
