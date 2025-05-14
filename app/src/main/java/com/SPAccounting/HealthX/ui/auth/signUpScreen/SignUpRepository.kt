package com.SPAccounting.HealthX.ui.auth.signUpScreen

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.SPAccounting.HealthX.utils.Logger
import kotlinx.coroutines.tasks.await


class SignUpRepository {
    var errorLiveData = MutableLiveData<String>()

    suspend fun registerUser(email: String, password: String): FirebaseUser? {
        return try {
            Logger.debugLog("Attempting to create user with email: $email")
            val authResult = FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).await()
            Logger.debugLog("User created successfully: ${authResult.user?.uid}")
            authResult.user
        } catch (e: FirebaseAuthUserCollisionException) {
            Logger.debugLog("Email already exists: $email")
            errorLiveData.postValue("Email này đã được sử dụng. Vui lòng sử dụng email khác.")
            null
        } catch (e: Exception) {
            Logger.debugLog("Error creating user: ${e.message}")
            e.printStackTrace()
            when {
                e.message?.contains("network") == true -> {
                    errorLiveData.postValue("Lỗi kết nối mạng. Vui lòng kiểm tra lại kết nối của bạn.")
                }
                e.message?.contains("password") == true -> {
                    errorLiveData.postValue("Mật khẩu không hợp lệ. Mật khẩu phải có ít nhất 6 ký tự.")
                }
                e.message?.contains("email") == true -> {
                    errorLiveData.postValue("Email không hợp lệ. Vui lòng kiểm tra lại định dạng email.")
                }
                else -> {
                    errorLiveData.postValue("Đã xảy ra lỗi: ${e.message}")
                }
            }
            null
        }
    }

}