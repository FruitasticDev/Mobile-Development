package com.fruitastic.ui.signup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.widget.doOnTextChanged
import com.fruitastic.BaseActivity
import com.fruitastic.R
import com.fruitastic.data.ViewModelFactory
import com.fruitastic.databinding.ActivitySignupBinding
import com.fruitastic.ui.login.LoginActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class SignupActivity : BaseActivity() {

    private lateinit var binding:ActivitySignupBinding
    private val viewModel by viewModels<SignupViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAction()
        playAnimation()
        observeRegisterResponse()
    }

    private fun setupAction() {
        binding.nameEditText.doOnTextChanged{ text, start, before, count ->
            if (text!!.isEmpty()) {
                binding.nameEditTextLayout.error = getString(R.string.error_empty_name)
            } else {
                binding.nameEditTextLayout.error = null
            }
        }

        binding.addressEditText.doOnTextChanged{ text, start, before, count ->
            if (text!!.isEmpty()) {
                binding.addressEditTextLayout.error = getString(R.string.error_empty_address)
            } else {
                binding.addressEditTextLayout.error = null
            }
        }

        binding.emailEditText.doOnTextChanged{ text, start, before, count ->
            if (!Patterns.EMAIL_ADDRESS.matcher(text.toString()).matches()) {
                binding.emailEditTextLayout.error = getString(R.string.error_invalid_email)
            } else {
                binding.emailEditTextLayout.error = null
            }
        }

        binding.passwordEditText.doOnTextChanged{ text, start, before, count ->
            if (text!!.length < 8) {
                binding.passwordEditTextLayout.error = getString(R.string.error_password_short)
            } else {
                binding.passwordEditTextLayout.error = null
            }
        }

        binding.signupButton.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            val address = binding.addressEditText.text.toString()
            val isEmailValid = Patterns.EMAIL_ADDRESS.matcher(email).matches()
            val isPasswordValid = password.length >= 8

            if (name.isEmpty()){
                showToast(getString(R.string.error_empty_name))
            } else if (email.isEmpty() || !isEmailValid) {
                showToast(getString(R.string.error_invalid_email))
            } else if (password.isEmpty() || !isPasswordValid) {
                showToast(getString(R.string.error_password_short))
            } else {
                viewModel.register(name, email, password)
            }
        }
    }

    private fun observeRegisterResponse() {
        viewModel.registerResponse.observe(this) { loginResponse ->
            if (loginResponse.error == true) {
                showToast(loginResponse.message ?: getString(R.string.register_failed))
            } else {
                showSuccessDialog(binding.emailEditText.text.toString())
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }
    }

    private fun showSuccessDialog(email: String) {
        MaterialAlertDialogBuilder(this@SignupActivity, R.style.CustomAlertDialogTheme).apply {
            setTitle(getString(R.string.signup_success_title))
            setMessage(getString(R.string.signup_success_message, email))
            setPositiveButton(getString(R.string.next_button)) { _, _ ->
                val intent = Intent(this@SignupActivity, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
            create()
            show()
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(100)
        val nameEdit = ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val addressEdit = ObjectAnimator.ofFloat(binding.addressEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val emailEdit = ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val passwordEdit = ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val signup = ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(
                title,
                nameEdit,
                addressEdit,
                emailEdit,
                passwordEdit,
                signup
            )
            startDelay = 100
        }.start()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}