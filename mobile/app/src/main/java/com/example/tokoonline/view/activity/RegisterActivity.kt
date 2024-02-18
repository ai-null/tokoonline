package com.example.tokoonline.view.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.tokoonline.core.base.BaseAuthActivity
import com.example.tokoonline.core.util.gone
import com.example.tokoonline.core.util.visible
import com.example.tokoonline.databinding.ActivityRegisterBinding
import com.example.tokoonline.data.model.firebase.User
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch



class RegisterActivity : BaseAuthActivity() {
    private lateinit var viewModel: PasswordViewModel
    private lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this).get(PasswordViewModel::class.java)

         initListener()
        observePasswordLength()
    }

    private fun initListener() = with(binding){
        buttonBack.setOnClickListener {
            finish()
        }

        edtPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.setPasswordLength(s?.length ?: 0)
            }
        })



        btnRegister.setOnClickListener{
            if (binding.tvKurang.visibility == View.GONE) {
                val email = edtEmail.text.toString()
                val password = edtPassword.text.toString()
                val nama = edtNama.text.toString()
                val notelp = edtPhone.text.toString()
                val role = "pembeli"
                val userDomain = User(
                    email = email,
                    password = password,
                    nama = nama,
                    noTelepon = notelp,
                    role = role
                )

                showProgressDialog()
                register(userDomain = userDomain) {isSuccess ->
                    dismissProgressDialog()
                    if (isSuccess) onSuccess()
                    else showToast("Register gagal")
                }
            }
        }
        btnLoggiinnn.setOnClickListener{
            finish()
        }
    }
    private fun onSuccess() {
        lifecycleScope.launch {
            showToast("Register Berhasil")
            delay(500)
            goToLoginActivity()
        }
    }
    private fun observePasswordLength() {
        viewModel.passwordLength.observe(this, { length ->
            if (length < 8) {
                binding.tvKurang.visible()
            } else {
                binding.tvKurang.gone()
            }
        })
    }
}