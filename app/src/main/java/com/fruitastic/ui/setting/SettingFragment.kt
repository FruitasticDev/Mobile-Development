package com.fruitastic.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.fruitastic.R
import com.fruitastic.data.pref.UserModel
import com.fruitastic.databinding.FragmentSettingBinding
import com.fruitastic.setLocale
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class SettingFragment : Fragment() {

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingViewModel by viewModels {
        com.fruitastic.data.ViewModelFactory.getInstance(requireActivity())
    }
    private lateinit var selectedLanguage: String;

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val switchTheme = binding.switchTheme
        val switchAutoSave = binding.switchAutoSave

        viewModel.getSession().observe(viewLifecycleOwner) { userModel: UserModel? ->
            userModel?.let {
                binding.accountName.text = it.name
                binding.accountEmail.text = it.email
            }
        }

        viewModel.getLanguageSetting().observe(viewLifecycleOwner) { isLanguage: String ->
            if (isLanguage == "id") {
                binding.tvLanguageValue.text = getString(R.string.language_indonesia)
            } else if(isLanguage == "en") {
                binding.tvLanguageValue.text = getString(R.string.language_english)
            }
        }

        viewModel.getThemeSettings().observe(viewLifecycleOwner) { isDarkModeActive: Boolean ->
            if (isDarkModeActive) {
                switchTheme.isChecked = true
                binding.tvValueTheme.text = getString(R.string.dark)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                switchTheme.isChecked = false
                binding.tvValueTheme.text = getString(R.string.light)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        viewModel.getAutoSaveSetting().observe(viewLifecycleOwner) { isAutoSaveActive: Boolean ->
            if (isAutoSaveActive) {
                switchAutoSave.isChecked = true
                binding.tvValueSave.text = getString(R.string.active)
            } else {
                switchAutoSave.isChecked = false
                binding.tvValueSave.text = getString(R.string.inactive)
            }
        }

        binding.languageGroup.setOnClickListener {
            context?.let { fragmentContext ->
                viewModel.getLanguageSetting().observe(viewLifecycleOwner) { currentLanguage ->
                    val currentLanguageIndex = if (currentLanguage == "id") 0 else 1
                    selectedLanguage = currentLanguage
                    MaterialAlertDialogBuilder(fragmentContext, R.style.CustomAlertDialogTheme).apply {
                        setTitle(getString(R.string.title_alert_language))
                        setSingleChoiceItems(
                            arrayOf(
                                getString(R.string.language_indonesia),
                                getString(R.string.language_english)
                            ),
                            currentLanguageIndex
                        ) { _, which ->
                            selectedLanguage = if (which == 0) "id" else "en"
                        }
                        setPositiveButton(getString(R.string.ok)) { _, _ ->
                            viewModel.saveLanguageSetting(selectedLanguage)

                            requireActivity().let { activity ->
                                val updatedContext = setLocale(activity, selectedLanguage)
                                activity.resources.updateConfiguration(
                                    updatedContext.resources.configuration,
                                    updatedContext.resources.displayMetrics
                                )
                                activity.recreate()
                            }
                        }
                        setNegativeButton(getString(R.string.cancel)) { dialog, _ -> dialog.dismiss() }
                        create()
                        show()
                    }
                }
            }
        }

        binding.logoutGroup.setOnClickListener {
            context?.let { fragmentContext ->
                MaterialAlertDialogBuilder(fragmentContext, R.style.CustomAlertDialogTheme).apply {
                    setTitle(getString(R.string.title_alert_logout))
                    setMessage(getString(R.string.message_alert_logout))
                    setPositiveButton(getString(R.string.logout)) { _, _ ->
                        viewModel.logout()
                    }
                    setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                        dialog.dismiss()
                    }
                    create()
                    show()
                }
            }
        }

        switchTheme.setOnCheckedChangeListener { _, isChecked ->
            viewModel.saveThemeSetting(isChecked)
        }
        switchAutoSave.setOnCheckedChangeListener { _, isChecked ->
            viewModel.saveAutoSaveSetting(isChecked)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
