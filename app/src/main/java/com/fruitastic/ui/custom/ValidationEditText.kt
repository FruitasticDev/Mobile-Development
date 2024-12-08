package com.fruitastic.ui.custom

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import com.fruitastic.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class ValidationEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : TextInputEditText(context, attrs, defStyleAttr) {

    private lateinit var textInputLayout: TextInputLayout

//    init {
//        addTextChangedListener(object : TextWatcher {
//            override fun afterTextChanged(s: Editable?) {}
//
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//
//                val input = s.toString()
//                when (id) {
//                    R.id.ed_login_email, R.id.ed_register_email -> {
//                        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(input).matches() && input.isNotEmpty()) {
//                            setErrorMessage(context.getString(R.string.error_invalid_email))
//                        } else {
//                            removeErrorMessage()
//                        }
//                    }
//                    R.id.ed_login_password, R.id.ed_register_password -> {
//                        if (input.length < 8) {
//                            setErrorMessage(context.getString(R.string.error_password_short))
//                        } else {
//                            removeErrorMessage()
//                        }
//                    }
//                    R.id.ed_register_name -> {
//                        if (input.isEmpty()) {
//                            setErrorMessage(context.getString(R.string.error_invalid_name))
//                        } else {
//                            removeErrorMessage()
//                        }
//                    }
//                }
//            }
//        })
//    }

    fun bindTextInputLayout(textInputLayout: TextInputLayout) {
        this.textInputLayout = textInputLayout
    }

    private fun setErrorMessage(message: String) {
        textInputLayout.error = message
    }

    private fun removeErrorMessage() {
        textInputLayout.error = null
    }
}
