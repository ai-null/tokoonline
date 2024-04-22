package com.example.tokoonline.view.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.tokoonline.core.base.BaseActivity
import com.example.tokoonline.core.util.gone
import com.example.tokoonline.core.util.visible
import com.example.tokoonline.data.model.firebase.Alamat
import com.example.tokoonline.data.model.firebase.Toko
import com.example.tokoonline.databinding.ActivityTokoSettingBinding
import com.example.tokoonline.view.viewmodel.AlamatViewModel
import com.example.tokoonline.view.viewmodel.TokoViewModel

class TokoSettingActivity : BaseActivity() {

    private lateinit var binding: ActivityTokoSettingBinding
    private lateinit var viewModelAlamat : AlamatViewModel
    private lateinit var  viewModelToko : TokoViewModel
    private fun String.toEditable() : Editable {
        return Editable.Factory.getInstance().newEditable(this)
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTokoSettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userId = userRepository.uid.toString()
        val tokoId = intent.getStringExtra("tokoID")
        viewModelAlamat = ViewModelProvider(this).get(AlamatViewModel::class.java)
        viewModelToko = ViewModelProvider(this).get(TokoViewModel::class.java)

        binding.toolbar.binding.leftIcon.setOnClickListener {
            finish()
        }

        if (tokoId != null){
            viewModelToko.getTokoData(userId){toko ->
                val namaToko = toko?.nama ?: ""
                val alamatToko = toko?.alamat?.alamat ?: ""
                val catatanAlamat = toko?.alamat?.catatan ?: ""
                binding.edtNama.text = namaToko.toEditable()
                binding.tvCatatanAlamat.setText(catatanAlamat)
                binding.tvAlamat.setText(alamatToko)
                viewModelAlamat.getAlamatDefault(userId){alamatDefault->

                    binding.btnSimpanToko.setOnClickListener {
                        val newToko = Toko(
                            id = tokoId,
                            id_users = userRepository.uid.toString(),
                            alamat = Alamat(
                                nama = namaToko,
                                id_users = userRepository.uid.toString(),
                                label = "Toko",
                                alamat = binding.tvAlamat.text.toString(),
                                catatan = binding.tvCatatanAlamat.text.toString(),
                                phone = userRepository.phone.toString(),
                            ),
                            nama = binding.edtNama.text.toString(),
                        )
                        showProgressDialog()
                        viewModelToko.updateToko(userId, newToko){isSuccess->
                            if (isSuccess){
                                showToast("Toko Berhasil di Update")
                                dismissProgressDialog()
                                finish()
                            }else{
                                // handle failed to update toko
                            }
                        }
                    }
                }
            }
        }else{
            val intent = Intent(this, TambahTokoBaruActivity::class.java)
            startActivity(intent)
        }


    }

    private var idAlamat = ""

//    private fun initListener(tokoId : String){
//        val userId = userRepository.uid.toString()
//        if (tokoId != null){
//            viewModelToko.getTokoById(tokoId, userId){toko->
//                binding.tvNamaToko.text = toko?.nama.toString().toEditable()
//
//            }
//        }else{
//
//        }
//
////        viewModelAlamat.getAlamatDefault(userRepository.uid.toString()) { alamatDefault ->
////            if (alamatDefault != null) {
////                val newToko = Toko(
////                    id_users = userRepository.uid.toString(),
////                    id_alamat = alamatDefault.id.toString(),
////                    nama = binding.tvNamaToko.text.toString(),
////                )
////                viewModelToko.addToko(newToko,userUid = userRepository.uid.toString() ) { isSuccess ->
////                    dismissProgressDialog()
////                    if (isSuccess) {
////                        showToast("Toko berhasil ditambahkan")
////                        goToAlamatSetting()
////                    } else {
////                        showToast("Toko gagal untuk ditambahkan")
////                    }
////                }
////            } else {
////                // Handle if there no alamat default set
////            }
////        }
//    }
}