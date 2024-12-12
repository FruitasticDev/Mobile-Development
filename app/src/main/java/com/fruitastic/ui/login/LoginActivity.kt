package com.fruitastic.ui.login

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
import com.fruitastic.data.pref.UserModel
import com.fruitastic.data.remote.request.LoginRequest
import com.fruitastic.databinding.ActivityLoginBinding
import com.fruitastic.ui.main.MainActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class LoginActivity : BaseActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAction()
        playAnimation()
        observeLoginResponse()
    }

    private fun setupAction() {
        binding.emailEditText.doOnTextChanged{ text, _, _, _ ->
            if (!Patterns.EMAIL_ADDRESS.matcher(text.toString()).matches()) {
                binding.emailEditTextLayout.error = getString(R.string.error_invalid_email)
            } else {
                binding.emailEditTextLayout.error = null
            }
        }

        binding.passwordEditText.doOnTextChanged{ text, _, _, _ ->
            if (text!!.length < 8) {
                binding.passwordEditTextLayout.error = getString(R.string.error_password_short)
            } else {
                binding.passwordEditTextLayout.error = null
            }
        }

        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            val isEmailValid = Patterns.EMAIL_ADDRESS.matcher(email).matches()
            val isPasswordValid = password.length >= 8

            if (email.isEmpty() || !isEmailValid) {
                showToast(getString(R.string.error_invalid_email))
            } else if (password.isEmpty() || !isPasswordValid) {
                showToast(getString(R.string.error_password_short))
            } else {
                viewModel.login(LoginRequest(email, password))
            }
        }
    }

    private fun observeLoginResponse() {
        viewModel.loginResult.observe(this) { loginResponse ->
            if (loginResponse.token.isNullOrEmpty()) {
                showToast(getString(R.string.login_failed))
            } else {
                loginResponse.user. let {
                    viewModel.saveSession(
                        UserModel(
                            name = loginResponse.user.name,
                            email = loginResponse.user.email,
                            token = loginResponse.token
                        )
                    )
                }
                showSuccessDialog(loginResponse.user.name)
            }
        }

        viewModel.errorMessage.observe(this) { errorMessage ->
            showToast(errorMessage)
        }

        viewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }
    }

    private fun showSuccessDialog(name: String) {
        MaterialAlertDialogBuilder(this@LoginActivity, R.style.CustomAlertDialogTheme).apply {
            setTitle(getString(R.string.login_success_title))
            setMessage(getString(R.string.login_success_message, name))
            setPositiveButton(getString(R.string.next_button)) { _, _ ->
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
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
        val message = ObjectAnimator.ofFloat(binding.messageTextView, View.ALPHA, 1f).setDuration(100)
        val emailEdit = ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val passwordEdit = ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val login = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(
                title,
                message,
                emailEdit,
                passwordEdit,
                login
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
