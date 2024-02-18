package com.example.tokoonline.view.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.tokoonline.core.base.BaseAuthActivity
import com.example.tokoonline.core.util.gone
import com.example.tokoonline.core.util.visible
import com.example.tokoonline.databinding.ActivityLoginBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class PasswordViewModel : ViewModel() {
    private val _passwordLength = MutableLiveData<Int>()
    val passwordLength: LiveData<Int>
        get() = _passwordLength

    fun setPasswordLength(length: Int) {
        _passwordLength.value = length
    }

}

class LoginActivity : BaseAuthActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: PasswordViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this).get(PasswordViewModel::class.java)

        initListener()
        observePasswordLength()
    }

    private fun initListener() = with(binding) {
        buttonBack.setOnClickListener {
            finish()
        }

        btnLogin.setOnClickListener{
            if (!edtEmail.text.isNullOrEmpty() && !edtPassword.text.isNullOrEmpty()) {
                showProgressDialog()
                login(edtEmail.text.toString(), edtPassword.text.toString(), doOnFailed = {
                    lifecycleScope.launch {
                        delay(500)
                        dismissProgressDialog()
                        showToast("Akun tidak ditemukan")
                    }
                })
            }
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

        btnDaftarAkun.setOnClickListener{
            goToRegister()
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