package com.SPAccounting.HealthX.ui.mainFragments.stats

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.SPAccounting.HealthX.base.BaseViewModel
import com.SPAccounting.HealthX.model.HealthData
import com.SPAccounting.HealthX.model.TestResult
import com.SPAccounting.HealthX.model.User
import com.SPAccounting.HealthX.utils.Constants
import com.SPAccounting.HealthX.utils.DateTimeExtension
import com.SPAccounting.HealthX.utils.Logger
import com.SPAccounting.HealthX.utils.SharedPrefsExtension.getUserFromSharedPrefs
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.TreeMap

class AddStatsDataViewModel(application: Application) : BaseViewModel(application) {

    var healthData = MutableLiveData<HealthData>()
    var testName = MutableLiveData<String>()
    var healthId = MutableLiveData<String>()
    var testResult = MutableLiveData<String>()
//    var statsTreeMap = MutableLiveData<TreeMap<String, TestResult>>()
    var statsList = MutableLiveData<MutableList<TestResult>>()
    var enableButton = MutableLiveData<Boolean>()
    var isDataSaved = MutableLiveData<Boolean>()
    var userLiveData = MutableLiveData<User>()
    var errorLiveData = MutableLiveData<String>()

    init {
        healthData.value = HealthData()
    }

    fun setHealthData(healthData: HealthData) {
        //When value from args is passed
        this.healthData.value = healthData
        this.healthId.value = healthData.healthId
        this.testName.value = healthData.name
        this.statsList.value = healthData.tests as MutableList<TestResult>
    }

    fun setHealthId() {
        this.healthId.value = DateTimeExtension.getTimeStamp()
        updateButtonState()
    }

    fun setTestResult(testResult: String) {
        this.testResult.value = testResult
        updateButtonState()
    }

    fun setTestName(testName: String) {
        this.testName.value = testName
        updateButtonState()
    }

    fun saveDataInFirebase() {
        val timeStamp = DateTimeExtension.getTimeStamp()
        val result = TestResult(testResult.value, timeStamp)

        if (statsList.value == null) {
            statsList.value = mutableListOf()
        }

        statsList.value!!.let {
            it.add(result)
            if (it.size > 28) {
                it.removeAt(0)
            }
        }

        Logger.debugLog("StatsList ${statsList.value}")

        val healthData = HealthData(
            testName.value,
            statsList.value,
            healthId.value
        )

        this.healthData.value = healthData
        Logger.debugLog("HealthData $healthData")

        pushIntoFirebase()
    }

    private fun pushIntoFirebase() = viewModelScope.launch(Dispatchers.IO) {
        val userId = userLiveData.value?.UID
        FirebaseDatabase.getInstance().reference.child(Constants.Users).child(userId!!)
            .child(Constants.HealthData).child(healthId.value!!).setValue(healthData.value)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Logger.debugLog("Data saved successfully")
                    isDataSaved.postValue(true)
                } else {
                    Logger.debugLog("Data not saved")
                    errorLiveData.postValue("Data not saved")
                }
            }.addOnFailureListener {
                Logger.debugLog("Data not saved, called on Failure")
                errorLiveData.postValue("Data not saved, called on Failure")
            }
    }


    private fun updateButtonState() {
        val requiredField =
            testResult.value.isNullOrEmpty() ||
            testName.value.isNullOrEmpty() ||
                    healthId.value.isNullOrEmpty()
        enableButton.value = requiredField.not()
    }

    fun saveDataFromSharedPreferences(sharedPreferences: SharedPreferences) =
        viewModelScope.launch(Dispatchers.IO) {
            userLiveData.postValue(sharedPreferences.getUserFromSharedPrefs())
        }

}