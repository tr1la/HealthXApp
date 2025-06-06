package com.SPAccounting.HealthX.ui.auth.signUpScreen.SecondScreen

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.SPAccounting.HealthX.R
import com.SPAccounting.HealthX.base.ViewModelFactory
import com.SPAccounting.HealthX.model.User
import com.SPAccounting.HealthX.databinding.ActivitySignUpBinding
import com.SPAccounting.HealthX.model.Doctor
import com.SPAccounting.HealthX.ui.auth.signInScreen.SignInScreen
import com.SPAccounting.HealthX.utils.Constants
import com.SPAccounting.HealthX.utils.Logger
import com.SPAccounting.HealthX.utils.Utils.getListOfIsDoctor
import com.SPAccounting.HealthX.utils.Utils.getListOfSpecialization

class SignUpSecondScreen : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private val signUpSecondViewModel by viewModels<SignUpSecondViewModel> { ViewModelFactory() }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpIntentValue()
        initObservers()
        initViews()

    }

    @Suppress("DEPRECATION")
    private fun setUpIntentValue() {
        val user = intent.getParcelableExtra<User>(Constants.user)
        val password = intent.extras!!.getString(Constants.password)
        signUpSecondViewModel.apply {
            setUpUser(user!!)
            setUserPassword(password!!)
        }
    }

    private fun initViews() {
        binding.run {
            addressEditText.setUserInputListener {
                signUpSecondViewModel.setUserAddress(it)
            }
            isDoctorSpinnerEditText.getSelectedItemFromDialog {
                signUpSecondViewModel.setUserIsDoctor(Doctor.isDoctor(it).toItemString())
            }
            specializationSpinnerEditText.getSelectedItemFromDialog {
                signUpSecondViewModel.setUserSpecialization(it)
            }
            createAccountButton.setOnClickListener {
                signUpSecondViewModel.createAccount()
            }
        }
    }

    private fun initObservers() {
        setUpIsDoctorDialog()
        setUpSpecializationDialog()
        signUpSecondViewModel.run {
            userIsDoctor.observe(this@SignUpSecondScreen) {
                Logger.debugLog("isDoctor: $it with ${Doctor.IS_DOCTOR.toItemString()} and ${it == Doctor.IS_DOCTOR.toItemString()}")
                binding.specializationSpinnerEditText.visibility =
                    if (it == Doctor.IS_DOCTOR.toItemString()) View.VISIBLE else View.GONE
            }
            userAccountCreationLiveData.observe(this@SignUpSecondScreen) {
                if (it) {
                    Toast.makeText(
                        this@SignUpSecondScreen,
                        getString(R.string.account_created_successfully),
                        Toast.LENGTH_SHORT
                    ).show()

                    val intent = Intent(this@SignUpSecondScreen, SignInScreen::class.java)
                    startActivity(intent)
                    finishAffinity()
                }
            }
            enableCreateAccountButtonLiveData.observe(this@SignUpSecondScreen) {
                binding.createAccountButton.isEnabled = it
                binding.createAccountButton.setButtonEnabled(it)
            }
            errorLiveData.observe(this@SignUpSecondScreen) {
                Toast.makeText(this@SignUpSecondScreen, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setUpIsDoctorDialog() {
        val isDoctorItems = getListOfIsDoctor()
        binding.isDoctorSpinnerEditText.setUpDialogData(
            isDoctorItems,
            getString(R.string.please_select_your_profession),
            null, null, null
        )
    }

    private fun setUpSpecializationDialog() {
        val specializationItems = getListOfSpecialization()
        binding.specializationSpinnerEditText.setUpDialogData(
            specializationItems,
            getString(R.string.please_select_your_specialization),
            null, null, null
        )
    }
}