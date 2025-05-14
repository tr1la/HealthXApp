package com.SPAccounting.HealthX.ui.profile

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.SPAccounting.HealthX.R
import com.SPAccounting.HealthX.databinding.ActivityProfileBinding
import com.SPAccounting.HealthX.utils.Constants
import com.SPAccounting.HealthX.utils.SharedPrefsExtension.getUserFromSharedPrefs
import com.google.firebase.database.*

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var sharedPreference : SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreference = baseContext.getSharedPreferences(Constants.UserData, Context.MODE_PRIVATE)

        getUserData()

        binding.ProfileToEdit.setOnClickListener {
            startActivity(Intent(baseContext, EditProfileActivity::class.java))
        }
    }
    
    // Refresh data when returning from EditProfileActivity
    override fun onResume() {
        super.onResume()
        getUserData()
    }

    @SuppressLint("SetTextI18n")
    private fun getUserData() {
        val user = sharedPreference.getUserFromSharedPrefs()
        binding.name.text = getString(R.string.profile_name_display, user.Name)
        binding.age.text = getString(R.string.profile_age_display, user.Age.toString())
        binding.email.text = getString(R.string.profile_email_display, user.Email)
        binding.phone.text = getString(R.string.profile_phone_display, user.Phone)
    }
}