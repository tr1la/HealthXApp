package com.SPAccounting.HealthX.ui.profile

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.SPAccounting.HealthX.R
import com.SPAccounting.HealthX.ui.mainFragments.settings.prescription.AddPrescriptionActivity
import com.SPAccounting.HealthX.databinding.ActivityEditProfileBinding
import com.SPAccounting.HealthX.utils.Constants
import com.SPAccounting.HealthX.utils.SharedPrefsExtension.getUserFromSharedPrefs
import com.SPAccounting.HealthX.utils.SharedPrefsExtension.saveUserToSharedPrefs
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var db : DatabaseReference
    private lateinit var sharedPreference : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreference = baseContext.getSharedPreferences(Constants.UserData, Context.MODE_PRIVATE)
        
        // Set current values as hints
        val currentUser = sharedPreference.getUserFromSharedPrefs()
        binding.editName.hint = currentUser.Name
        binding.editAge.hint = currentUser.Age.toString()
        binding.editPhoneNumber.hint = currentUser.Phone

        binding.confirm.setOnClickListener {
            val name = binding.editName.text.toString().trim()
            val age = binding.editAge.text.toString().trim()
            val phoneno = binding.editPhoneNumber.text.toString().trim()

            updateData(name, age, phoneno)
        }
        binding.updatepres.setOnClickListener {
            startActivity(Intent(baseContext, AddPrescriptionActivity::class.java))
        }
    }

    private fun updateData(name: String, age: String, phoneno: String) {
        // Lấy userID đúng từ SharedPreferences
        val userID = sharedPreference.getUserFromSharedPrefs().UID ?: "Not found"
        
        // Sử dụng đúng đường dẫn Firebase
        db = FirebaseDatabase.getInstance().reference.child(Constants.Users)
        
        // Log để theo dõi quá trình
        android.util.Log.d("EditProfile", "Updating user with ID: $userID")
        android.util.Log.d("EditProfile", "User data: name=$name, age=$age, phone=$phoneno")
        
        // Tạo map giá trị cập nhật - giữ age dưới dạng String để tương thích với Firebase
        val user = mapOf(
            "name" to name,
            "age" to if(age.isNotEmpty()) age else sharedPreference.getUserFromSharedPrefs().Age.toString(),
            "phone" to phoneno
        )

        // Cập nhật Firebase và SharedPreferences
        db.child(userID).updateChildren(user).addOnSuccessListener {
            android.util.Log.d("EditProfile", "Firebase update successful")
            
            // Cập nhật user trong SharedPreferences
            val updatedUser = sharedPreference.getUserFromSharedPrefs()
            updatedUser.apply {
                if(name.isNotEmpty()) Name = name
                if(age.isNotEmpty()) Age = age.toInt()
                if(phoneno.isNotEmpty()) Phone = phoneno
            }
            
            // Lưu user đã cập nhật vào SharedPreferences
            sharedPreference.saveUserToSharedPrefs(updatedUser)
            android.util.Log.d("EditProfile", "SharedPreferences updated successfully")
            
            // Xóa các trường nhập
            binding.editName.text.clear()
            binding.editAge.text.clear()
            binding.editPhoneNumber.text.clear()
            
            Toast.makeText(baseContext, getString(R.string.profile_update_success_toast), Toast.LENGTH_SHORT).show()
            finish() // Quay lại màn hình profile để xem thay đổi
        }.addOnFailureListener { e ->
            android.util.Log.e("EditProfile", "Firebase update failed: ${e.message}")
            Toast.makeText(baseContext, getString(R.string.profile_update_failed_toast), Toast.LENGTH_SHORT).show()
        }
    }
}