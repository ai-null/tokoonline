package com.example.tokoonline.view.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.AdapterView
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.example.tokoonline.core.base.BaseActivity
import com.example.tokoonline.core.util.getFormattedTimeMidtrans
import com.example.tokoonline.data.model.firebase.Produk
import com.example.tokoonline.databinding.ActivityTambahProdukBinding
import com.example.tokoonline.view.viewmodel.TambahProdukViewModel
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

class TambahProdukActivity : BaseActivity() {
    private lateinit var binding: ActivityTambahProdukBinding
    private val viewModel: TambahProdukViewModel by viewModels()
    private lateinit var storageReference: StorageReference
    private var selectedImageUri: Uri? = null
    private var kategori: String = ""

    private fun String.toEditable() : Editable {
        return Editable.Factory.getInstance().newEditable(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTambahProdukBinding.inflate(layoutInflater)
        setContentView(binding.root)
        storageReference = Firebase.storage.reference.child("images/produk")
        binding.toolbar.binding.leftIcon.setOnClickListener {
            finish()
        }

        val produk = intent.getParcelableExtra<Produk>("produk")

        if (produk !== null) {
            fillFormWithProductData(produk)
        } else {
            initListener()
        }

        binding.kategoriSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
//                        Log.d("Spinner", "Selected item: ${p0?.getItemAtPosition(p2)}")
                    // Get the selected item as a String
                    kategori = p0?.getItemAtPosition(p2).toString()
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                }
            }


        binding.buttonTambahGambarProduk.setOnClickListener {
            //membuka galeri untuk memilih gambar
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_SELECT_IMAGE)
        }
    }

    private fun initListener() = with(binding) {
        btnSbmitProduk.setOnClickListener {
            showProgressDialog()
            if (selectedImageUri != null) {
                val imageReference = storageReference.child(selectedImageUri!!.lastPathSegment!!)
                // Upload the selected image to Firebase Storage
                imageReference.putFile(selectedImageUri!!)
                    .addOnSuccessListener {
                        uploadProduct(imageReference)
                    }.addOnFailureListener {
                        dismissProgressDialog()
                        showToast("Gambar gagal diunggah")
                    }
            } else {
                dismissProgressDialog()
                showToast("Pilih gambar terlebih dahulu")
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun fillFormWithProductData(produk: Produk) {
        with(binding) {
            Glide.with(this@TambahProdukActivity)
                .load(Uri.parse(produk.image))
                .into(gambarImageView)


            etNamaProduk.setText(produk.nama)
            etHargaProduk.setText(produk.harga.toString())
            etDeskProduk.setText(produk.deskripsi)

            val beratProduk = produk.beratProduk.toString()
            etBeratProduk.text = beratProduk.toEditable()

            etStok.setText(produk.stok.toString())

//            val kategoriArray = resources.getStringArray(R.array.kategori_array)
//            val index = kategoriArray.indexOf(produk.kategori)
//
//            if (index != -1) {
//                kategoriSpinner.setSelection(index)
//            } else {
//                // Handle the case when the category is not found in the array
//            }


            binding.btnSbmitProduk.text = "Update Data Produk"
            binding.btnSbmitProduk.setOnClickListener {
                showProgressDialog()
                if (selectedImageUri != null) {
                    val imageReference = storageReference.child(selectedImageUri!!.lastPathSegment!!)
                    // Upload the selected image to Firebase Storage
                    imageReference.putFile(selectedImageUri!!)
                        .addOnSuccessListener {
                            imageReference.downloadUrl.addOnSuccessListener { uri ->
                                updateUserProduct(uri.toString() , produk.id)
                            }
                        }
                        .addOnFailureListener {
                            dismissProgressDialog()
                            showToast("Gambar gagal diunggah")
                        }
                } else {
                    val existingImageUrl = produk.image
                    updateUserProduct(existingImageUrl, produk.id)
                }
            }

        }
    }

    private fun uploadProduct(imageReference: StorageReference) {
        with(binding) {
            imageReference.downloadUrl.addOnSuccessListener { uri ->
                userRepository.getTokoID(userRepository.uid ?: "") { isSuccess, idToko ->
                    if (isSuccess) {
                        val dataProdukNew = Produk(
                            image = uri.toString(),
                            nama = etNamaProduk.text.toString(),
                            harga = etHargaProduk.text.toString().toLong(),
                            deskripsi = etDeskProduk.text.toString(),
                            keyword = etNamaProduk.text.toString().lowercase(),
                            idToko = idToko ?: "", // Use the idToko here
                            idSeller = userRepository.uid,
                            beratProduk = etBeratProduk.text.toString().toDouble(),
                            stok = this.etStok.text.toString().toInt(),
                            createdAt = getFormattedTimeMidtrans(System.currentTimeMillis()),
                            kategori = kategori,
                        )
                        viewModel.addData(dataProdukNew) { isSuccess ->
                            dismissProgressDialog()
                            if (isSuccess) {
                                showToast("Successfully Saved")
                                finish()
//                              showProgressDialog()
                            } else showToast("Failed")
                        }
                    } else {
                        dismissProgressDialog()
                        showToast("Failed to retrieve idToko")
                    }
                }
            }
        }
    }

    private fun updateUserProduct(imageUrl: String, produkID : String) {
        with(binding) {
            userRepository.getTokoID(userRepository.uid ?: "") { isSuccess, idToko ->
                if (isSuccess) {
                    val dataProdukNew = Produk(
                        id = produkID,
                        image = imageUrl,
                        nama = etNamaProduk.text.toString(),
                        harga = etHargaProduk.text.toString().toLong(),
                        deskripsi = etDeskProduk.text.toString(),
                        keyword = etNamaProduk.text.toString().lowercase(),
                        idToko = idToko ?: "",
                        idSeller = userRepository.uid,
                        beratProduk = etBeratProduk.text.toString().toDouble(),
                        stok = etStok.text.toString().toInt(),
                        createdAt = getFormattedTimeMidtrans(System.currentTimeMillis()),
                        kategori = kategori
                    )
                    viewModel.updateProduk(dataProdukNew) { isSuccess ->
                        dismissProgressDialog()
                        if (isSuccess) {
                            showToast("Successfully Updated")
                            finish()
                        } else {
                            showToast("Failed to update product")
                        }
                    }
                } else {
                    dismissProgressDialog()
                    showToast("Failed to retrieve idToko")
                }
            }
        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_SELECT_IMAGE && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.data
            binding.gambarImageView.setImageURI(selectedImageUri)
        }
    }
    companion object {
        private const val REQUEST_SELECT_IMAGE = 100
    }
}

//private fun Spinner.setSelection(toString: String) {
//    val adapter = adapter
//    if (adapter != null) {
//        for (i in 0 until adapter.count) {
//            if (adapter.getItem(i).toString() == value) {
//                setSelection(i)
//                break
//            }
//        }
//    }
//
//}
