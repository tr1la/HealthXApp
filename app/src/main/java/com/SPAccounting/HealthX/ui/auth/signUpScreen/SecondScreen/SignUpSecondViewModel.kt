package com.SPAccounting.HealthX.ui.auth.signUpScreen.SecondScreen

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.SPAccounting.HealthX.base.BaseViewModel
import com.SPAccounting.HealthX.model.Doctor
import com.SPAccounting.HealthX.model.HealthData
import com.SPAccounting.HealthX.model.TestResult
import com.SPAccounting.HealthX.model.User
import com.SPAccounting.HealthX.ui.auth.signUpScreen.SignUpRepository
import com.SPAccounting.HealthX.utils.Constants
import com.SPAccounting.HealthX.utils.DateTimeExtension
import com.SPAccounting.HealthX.utils.Logger
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SignUpSecondViewModel(application: Application) : BaseViewModel(application) {

    private var userPassword = MutableLiveData<String>()
    private var userLiveData = MutableLiveData<User>()
    private var userAddress = MutableLiveData<String>()
    var userIsDoctor = MutableLiveData<String>()
    private var userSpecialization = MutableLiveData<String>()
    var userAccountCreationLiveData = MutableLiveData<Boolean>()
    var userDataBaseUpdate = MutableLiveData<Boolean>()
    var errorLiveData = MutableLiveData<String>()
    private val signInRepository: SignUpRepository = SignUpRepository()

    val enableCreateAccountButtonLiveData: MutableLiveData<Boolean> by lazy { MutableLiveData() }

    init {
        userIsDoctor.value = Doctor.IS_NOT_DOCTOR.toItemString()
    }

    fun setUserPassword(password: String) {
        userPassword.value = password
    }

    fun setUpUser(user: User) {
        userLiveData.value = user
    }

    fun createAccount() = viewModelScope.launch(Dispatchers.IO) {
        // Kiểm tra kết nối mạng
        val connectivityManager = getApplication<Application>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        
        if (networkInfo == null || !networkInfo.isConnected) {
            errorLiveData.postValue("Không có kết nối mạng. Vui lòng kiểm tra lại kết nối của bạn.")
            return@launch
        }

        val email = userLiveData.value!!.Email!!
        val password = userPassword.value!!

        val address = userAddress.value
        val isDoctor = userIsDoctor.value
        val specialization = if (isDoctor == Doctor.IS_DOCTOR.toItemString()) userSpecialization.value else null

        userLiveData.value!!.apply {
            Address = address
            if (isDoctor != null) {
                this.isDoctor = isDoctor
            }
            Specialist = specialization
        }

        try {
        val userID = signInRepository.registerUser(email, password)?.uid

        Logger.debugLog("User ID after account creation is $userID")

        if (userID == null) {
                // Lấy thông báo lỗi từ Repository
                val errorMessage = signInRepository.errorLiveData.value
                if (!errorMessage.isNullOrEmpty()) {
                    errorLiveData.postValue(errorMessage)
                } else {
                    errorLiveData.postValue("Đăng ký thất bại. Vui lòng thử lại.")
                }
            return@launch
        }
            
        userLiveData.value!!.UID = userID
        FirebaseDatabase.getInstance().reference.child(Constants.Users).child(userID)
            .setValue(userLiveData.value).addOnSuccessListener {
                Logger.debugLog("User database created successfully and userID is $userID")
                    createSampleHealthMetrics(userID)
                userAccountCreationLiveData.value = true
            }.addOnFailureListener {
                Logger.debugLog("Exception caught at creating user database: ${it.message.toString()}")
                    errorLiveData.postValue("Không thể tạo dữ liệu người dùng: ${it.message}")
                }
        } catch (e: Exception) {
            Logger.debugLog("Exception during account creation: ${e.message}")
            errorLiveData.postValue("Đăng ký thất bại: ${e.message}")
            }
    }

    private fun createSampleHealthMetrics(userId: String) {
        val sampleMetrics = listOf(
            Pair("Cân nặng", "65"),
            Pair("Chiều cao", "170"),
            Pair("Huyết áp", "120"),
            Pair("Nhịp tim", "75"),
            Pair("Đường huyết", "5"),
            Pair("Nhiệt độ cơ thể", "37"),
            Pair("SpO2", "98"),
            Pair("Cholesterol", "5")
        )

        sampleMetrics.forEach { (name, value) ->
            val healthId = DateTimeExtension.getTimeStamp()
            val result = TestResult(value, healthId)
            val healthData = HealthData(
                name = name,
                tests = listOf(result),
                healthId = healthId
            )

            FirebaseDatabase.getInstance().reference
                .child(Constants.Users)
                .child(userId)
                .child(Constants.HealthData)
                .child(healthId)
                .setValue(healthData)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        Logger.debugLog("Sample metric $name saved successfully")
                    } else {
                        Logger.debugLog("Failed to save sample metric $name")
                        errorLiveData.postValue("Failed to save sample metric $name")
                    }
                }
        }
    }

    fun createUserDatabase() = viewModelScope.launch(Dispatchers.IO) {
        val firebaseAuth = FirebaseAuth.getInstance()
        val userId = firebaseAuth.currentUser?.uid.toString()
        FirebaseDatabase.getInstance().reference.child(Constants.Users).child(userId)
            .setValue(userLiveData.value).addOnSuccessListener {
                Logger.debugLog("User database created successfully and userID is $userId")
                userDataBaseUpdate.value = true
            }.addOnFailureListener {
                Logger.debugLog("Exception caught at creating user database: ${it.message.toString()}")
                userDataBaseUpdate.value = false
            }
        if (userLiveData.value!!.isDoctor == Doctor.IS_DOCTOR.toItemString()) {
            FirebaseDatabase.getInstance().reference.child(Constants.Doctor).child(userId)
                .setValue(userLiveData.value).addOnSuccessListener {
                    Logger.debugLog("Doctor database created successfully")
                }.addOnFailureListener {
                    Logger.debugLog("Exception caught at creating doctor database: ${it.message.toString()}")
                }
        }
    }

    fun setUserAddress(address: String) {
        userAddress.value = address
        updateButtonState()
    }

    fun setUserIsDoctor(isDoctor: String) {
        userIsDoctor.value = isDoctor
        updateButtonState()
    }

    fun setUserSpecialization(specialization: String) {
        userSpecialization.value = specialization
        updateButtonState()
    }

    private fun updateButtonState() {
        val requiredField =
            userAddress.value.isNullOrEmpty() || if (userIsDoctor.value == Doctor.IS_DOCTOR.toItemString()) {
                userSpecialization.value.isNullOrEmpty()
            } else {
                false
            }
        enableCreateAccountButtonLiveData.value = requiredField.not()
    }

}