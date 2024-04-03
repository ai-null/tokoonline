package com.example.tokoonline.view.activity

import android.os.Bundle
import android.text.Editable
import android.view.View
import androidx.activity.viewModels
import com.example.tokoonline.core.base.BaseActivity
import com.example.tokoonline.core.util.gone
import com.example.tokoonline.data.model.firebase.Alamat
import com.example.tokoonline.databinding.ActivityAlamatFormBinding
import com.example.tokoonline.view.viewmodel.AlamatViewModel

class AlamatFormActivity : BaseActivity() {
    private lateinit var binding: ActivityAlamatFormBinding
    private val viewModel : AlamatViewModel by viewModels()

    private fun String.toEditable() : Editable {
        return Editable.Factory.getInstance().newEditable(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlamatFormBinding.inflate(layoutInflater)

        binding.toolbar.binding.leftIcon.setOnClickListener {
            finish()
        }
        setContentView(binding.root)

        val selectedAlamatId = intent.getStringExtra("selectedAlamatId")
        val userId = userRepository.uid.toString()

        if (selectedAlamatId != null) {
            viewModel.getAlamatById(selectedAlamatId, userId) { selectedAlamat ->
                if (selectedAlamat != null) {
                    selectedAlamat.id
                    binding.tvLabelAlamat.text = selectedAlamat.label.toEditable()
                    binding.tvAlamatPenerima.text = selectedAlamat.alamat.toEditable()
                    binding.tvCatatanAlamat.text = selectedAlamat.catatan.toEditable()
//                    binding.btnHapusAlamat.visibility = View.VISIBLE
//                    binding.btnHapusAlamat.setOnClickListener {
//                        viewModel.deleteAlamatById(selectedAlamatId) { isSuccess ->
//                            if (isSuccess) {
//                                showToast("Alamat berhasil dihapus")
//                                goToHomepage()
//                            } else {
//                                showToast("Deletion failed")
//                            }
//                        }
//                    }
                } else {
                    showToast("Failed to retrieve selected address")
                }
            }
        }else{
            binding.btnHapusAlamat.gone()
        }

        binding.btnHapusAlamat.setOnClickListener {
            if (selectedAlamatId != null) {
                viewModel.deleteAlamatById(selectedAlamatId ,userId) { isSuccess ->
                    if (isSuccess) {
                        showToast("Alamat berhasil dihapus")
                        finish()
                    } else {
                        showToast("Deletion failed")
                    }
                }
            }
        }
        binding.btnSimpanAlamat.setOnClickListener{
            initListener(selectedAlamatId)
        }

    }
    private fun initListener(selectedAlamatId:String?) = with(binding) {
        showProgressDialog()

        val newDataAlamat = Alamat(
            label = tvLabelAlamat.text.toString(),
            alamat = tvAlamatPenerima.text.toString(),
            catatan = tvCatatanAlamat.text.toString(),
            default = false,
            id_users = userRepository.uid.toString(),

        )

        if (selectedAlamatId != null) {
            // Update the existing address
            val userId = userRepository.uid.toString()
            newDataAlamat.id = selectedAlamatId // Update the id field of the existing object
            viewModel.updateAlamat(userId, newDataAlamat) { isSuccess ->
                dismissProgressDialog()
                if (isSuccess) {
                    showToast("Alamat berhasil diupdate")
                } else {
                    showToast("Alamat tidak berhasil diupdate")
                }

                finish()
            }
    } else {
            // Add a new address
            viewModel.addAlamat(newDataAlamat,userUid = userRepository.uid.toString() ) { isSuccess ->
                dismissProgressDialog()
                if (isSuccess) {
                    showToast("Alamat berhasil ditambahkan")
                } else {
                    showToast("Alamat gagal untuk ditambahkan")
                }

                finish()
            }
        }
    }

}
