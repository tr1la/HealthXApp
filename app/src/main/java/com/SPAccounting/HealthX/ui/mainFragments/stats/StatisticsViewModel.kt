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
import com.SPAccounting.HealthX.utils.Logger
import com.SPAccounting.HealthX.utils.SharedPrefsExtension.getUserFromSharedPrefs
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class StatisticsViewModel(application: Application) : BaseViewModel(application) {

    var statsList = MutableLiveData<List<HealthData>>()
    var userLiveData = MutableLiveData<User>()
    val errorLiveData = MutableLiveData<String>()

    fun setStatsList() = viewModelScope.launch(Dispatchers.IO) {
        val userId = userLiveData.value?.UID
        val tempStatsList = mutableListOf<HealthData>()
        FirebaseDatabase.getInstance().reference.child(Constants.Users).child(userId!!)
            .child(Constants.HealthData).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach { healthDataSnapshot ->
                        try {
                            val healthData = healthDataSnapshot.getValue(HealthData::class.java)
                            val testsList = mutableListOf<TestResult>()
                            Logger.debugLog("healthData: $healthData")
                            healthData?.tests?.let { tests ->
                                testsList.addAll(tests)
                            }
                            Logger.debugLog("testsList: $testsList")
                            healthData?.tests = testsList
                            tempStatsList.add(healthData!!)
                            statsList.postValue(tempStatsList)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Logger.debugLog("Error in StatisticsViewModel: ${e.message}")
                            errorLiveData.postValue(e.message)
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            })
    }

    fun getDataFromSharedPreference(sharedPreference: SharedPreferences) {
        userLiveData.postValue(sharedPreference.getUserFromSharedPrefs())
    }

}