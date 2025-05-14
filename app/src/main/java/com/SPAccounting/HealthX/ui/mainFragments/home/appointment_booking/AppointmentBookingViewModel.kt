package com.SPAccounting.HealthX.ui.mainFragments.home.appointment_booking

import android.app.Application
import android.content.SharedPreferences
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.SPAccounting.HealthX.base.BaseViewModel
import com.SPAccounting.HealthX.model.Summary
import com.SPAccounting.HealthX.model.User
import com.SPAccounting.HealthX.utils.Constants
import com.SPAccounting.HealthX.utils.Logger
import com.SPAccounting.HealthX.utils.SharedPrefsExtension.getUserFromSharedPrefs
import com.SPAccounting.HealthX.utils.Utils
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class AppointmentBookingViewModel(application: Application) : BaseViewModel(application) {
    private lateinit var mapOfDiseasesList: HashMap<String, ArrayList<String>>
    private lateinit var diseaseValue: HashMap<String, Float>
    var appointmentDate = MutableLiveData<String>()
    var appointmentTime = MutableLiveData<String>()
    var appointmentDisease = MutableLiveData<String>()
    var appointmentCondition = MutableLiveData<String>()
    var appointmentEnableButtonSlider = MutableLiveData<Boolean>()


    var fireStatusMutableLiveData = MutableLiveData<Boolean>()
    private val _navigateToBookingSummary = MutableLiveData<Summary>()

    private var userLiveData = MutableLiveData<User>()
    val navigateToBookingSummary: LiveData<Summary> get() = _navigateToBookingSummary

    fun setAppointmentDate(date: String) {
        appointmentDate.value = date
        updateButtonState()
    }

    fun setAppointmentTime(time: String) {
        appointmentTime.value = time
        updateButtonState()
    }

    fun setAppointmentDisease(disease: String) {
        appointmentDisease.value = disease
        updateButtonState()
    }

    fun setAppointmentCondition(condition: String) {
        appointmentCondition.value = condition
        updateButtonState()
    }

    fun bookAppointment(
        doctorType: String?,
        doctorUid: String,
        doctorName: String,
        doctorEmail: String,
        doctorPhone: String,
        conditionValue: HashMap<String, Float>
    ) = viewModelScope.launch {

        try {
           
            Logger.debugLog("Starting appointment booking...")
            
            // Hiển thị danh sách bệnh có sẵn
            val availableDiseases = diseaseValue.keys.joinToString("\n")
           
            
            // Kiểm tra chi tiết về disease
            if (appointmentDisease.value.isNullOrEmpty()) {
                Toast.makeText(getApplication(), "Vui lòng chọn bệnh!", Toast.LENGTH_SHORT).show()
                return@launch
            }

            // Kiểm tra chi tiết hơn về bệnh được chọn
            val selectedDisease = appointmentDisease.value.toString().trim()
            
            
            // Kiểm tra xem bệnh có trong danh sách không (case-insensitive)
            val matchingDisease = diseaseValue.keys.find { 
                it.equals(selectedDisease, ignoreCase = true) 
            }
            
            if (matchingDisease == null) {
                Toast.makeText(getApplication(), "Bệnh không hợp lệ!\nBệnh đã chọn: '$selectedDisease'\nDanh sách bệnh:\n$availableDiseases", Toast.LENGTH_LONG).show()
                return@launch
            }

            // Sử dụng bệnh đã match để lấy giá trị
            val diseaseValue = diseaseValue[matchingDisease]
            if (diseaseValue == null) {
                Toast.makeText(getApplication(), "Không tìm thấy thông tin bệnh!", Toast.LENGTH_SHORT).show()
                return@launch
            }
            var temp = diseaseValue
            

            // Get condition value with null safety
            val conditionValue = conditionValue[appointmentCondition.value]
            if (conditionValue == null) {
                Toast.makeText(getApplication(), "Lỗi: Chưa chọn tình trạng!", Toast.LENGTH_SHORT).show()
                Logger.debugLog("Error: Invalid condition selected")
                return@launch
            }
            temp += conditionValue
            Logger.debugLog("temp value after adding condition value: $temp")

            val totalPoint: Int

            val rightNow = Calendar.getInstance()
            val currentHourIn24Format: Int = rightNow.get(Calendar.HOUR_OF_DAY)
            val firstComeFirstServe = 1 + (0.1 * ((currentHourIn24Format / 10) + 1))

            Logger.debugLog("firstComeFirstServe: $firstComeFirstServe")
            Logger.debugLog("diseaseValue: ${diseaseValue}")
            Logger.debugLog("conditionValue: ${conditionValue}")
            Logger.debugLog("currentHourIn24Format: $currentHourIn24Format")

            totalPoint = (temp * firstComeFirstServe).toInt()
            Logger.debugLog("totalPoint: $totalPoint")

            val appointmentD: HashMap<String, String> = HashMap()
            appointmentD["PatientName"] = userLiveData.value?.Name.toString()
            appointmentD["PatientPhone"] = userLiveData.value?.Phone.toString()
            appointmentD["Time"] = appointmentTime.value.toString()
            appointmentD["Date"] = appointmentDate.value.toString()
            appointmentD["Disease"] = appointmentDisease.value.toString()
            appointmentD["PatientCondition"] = appointmentCondition.value.toString()
            appointmentD["Prescription"] = userLiveData.value?.Prescription.toString()
            appointmentD["TotalPoints"] = totalPoint.toString().trim()
            appointmentD["DoctorUID"] = doctorUid
            appointmentD["PatientID"] = userLiveData.value?.UID.toString()

            val appointmentP: HashMap<String, String> = HashMap()
            appointmentP["DoctorUID"] = doctorUid
            appointmentP["DoctorName"] = doctorName
            appointmentP["DoctorPhone"] = doctorPhone
            appointmentP["Date"] = appointmentDate.value.toString()
            appointmentP["Time"] = appointmentTime.value.toString()
            appointmentP["Disease"] = appointmentDisease.value.toString()
            appointmentP["PatientCondition"] = appointmentCondition.value.toString()
            appointmentP["Prescription"] = userLiveData.value?.Prescription.toString()
            appointmentP["PatientID"] = userLiveData.value?.UID.toString()

            val summary = Summary(
                doctorName = doctorName,
                doctorSpeciality = doctorType.toString(),
                doctorEmail = doctorEmail,
                doctorPhone = doctorPhone,
                appointmentDate = appointmentDate.value.toString(),
                appointmentTime = appointmentTime.value.toString(),
                disease = appointmentDisease.value.toString(),
                painLevel = appointmentCondition.value.toString(),
                totalPoint = totalPoint
            )
            _navigateToBookingSummary.value = summary
            Logger.debugLog("summary: $summary")
            Logger.debugLog("appointmentD: $appointmentD")
            Logger.debugLog("appointmentP: $appointmentP")

            val firebaseUploadJob = viewModelScope.launch {
                val appointmentDBUserDoctor = viewModelScope.async(Dispatchers.IO) {
                    updateUserDoctorAppointment(
                        doctorUid,
                        userLiveData.value?.UID.toString(),
                        appointmentD
                    )
                }

                val appointmentDBPatient = viewModelScope.async(Dispatchers.IO) {
                    updateUserPatientAppointment(
                        userLiveData.value?.UID.toString(),
                        doctorUid,
                        appointmentP
                    )
                }
                val success = appointmentDBUserDoctor.await() && appointmentDBPatient.await()
                fireStatusMutableLiveData.postValue(success)
                if (success) {
                    Toast.makeText(getApplication(), "Đặt lịch thành công!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(getApplication(), "Đặt lịch thất bại!", Toast.LENGTH_SHORT).show()
                }
            }

            firebaseUploadJob.join()
        } catch (e: Exception) {
            Toast.makeText(getApplication(), "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
            Logger.debugLog("Exception: ${e.message}")
            e.printStackTrace()
        }
    }

    private suspend fun updateUserPatientAppointment(
        userId: String,
        doctorUid: String,
        appointmentP: HashMap<String, String>
    ): Boolean {

        var res = true
        return withContext(Dispatchers.IO) {

            FirebaseDatabase.getInstance().getReference("Users").child(userId)
                .child("PatientsAppointments").child(appointmentP["Date"]!!).child(doctorUid)
                .setValue(appointmentP)
                .addOnSuccessListener {
                    
                    Logger.debugLog("Successfully updated patient appointment")
                }.addOnFailureListener {
                    
                    Logger.debugLog("Failed to update patient appointment")
                    res = false
                }
            res
        }
    }

    private suspend fun updateUserDoctorAppointment(
        doctorUid: String,
        userId: String,
        appointmentD: HashMap<String, String>
    ): Boolean {

        var result = true
        return withContext(Dispatchers.IO) {

            FirebaseDatabase.getInstance().getReference(Constants.Users).child(doctorUid)
                .child("DoctorsAppointments").child(appointmentD["Date"]!!).child(userId)
                .setValue(appointmentD)
                .addOnSuccessListener {
                    
                    Logger.debugLog("Successfully updated doctor appointment")
                }.addOnFailureListener {
                    
                    Logger.debugLog("Failed to update doctor appointment")
                    result = false
                }
            result
        }
    }

    fun initializeSpecializationWithDiseasesLists() {
        mapOfDiseasesList = Utils.initializeSpecializationWithDiseasesLists()
    }

    fun setDiseaseValues(diseaseValue: HashMap<String, Float>) {
        this.diseaseValue = diseaseValue
    }

    fun getDataFromSharedPref(sharedPreference: SharedPreferences) {
        userLiveData.value = sharedPreference.getUserFromSharedPrefs()
    }

    private fun updateButtonState() {
        val requiredField =
            appointmentDate.value.isNullOrEmpty()
                    || appointmentTime.value.isNullOrEmpty()
                    || appointmentDisease.value.isNullOrEmpty()
                    || appointmentCondition.value.isNullOrEmpty()
        appointmentEnableButtonSlider.value = requiredField.not()
    }

}