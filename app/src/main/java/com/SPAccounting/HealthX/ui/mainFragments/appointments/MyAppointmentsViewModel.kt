package com.SPAccounting.HealthX.ui.mainFragments.appointments

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import com.SPAccounting.HealthX.base.BaseViewModel
import com.SPAccounting.HealthX.model.PatientAppointment
import com.SPAccounting.HealthX.model.User
import com.SPAccounting.HealthX.utils.Constants
import com.SPAccounting.HealthX.utils.Logger
import com.SPAccounting.HealthX.utils.SharedPrefsExtension.getUserFromSharedPrefs
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class MyAppointmentsViewModel(application: Application) : BaseViewModel(application) {

    val userLiveData = MutableLiveData<User>()
    private val databaseReference = FirebaseDatabase.getInstance().reference.child(Constants.Users)
    val myAppointmentList = MutableLiveData<List<PatientAppointment>>()

    fun getAppointmentsForTheDate(date: String) {
        myAppointmentList.value = ArrayList()
        val helperAppointmentList = mutableListOf<PatientAppointment>()
        val userID = userLiveData.value!!.UID!!
        databaseReference.child(userID).child("PatientsAppointments")
            .child(date).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (appointmentSnapshot in snapshot.children) {
                            val appointment =
                                appointmentSnapshot.getValue(PatientAppointment::class.java)
                            helperAppointmentList.add(appointment!!)
                        }
                        myAppointmentList.postValue(helperAppointmentList as ArrayList<PatientAppointment>?)
                    } else {
                        Logger.debugLog("No appointments found for the date: $date")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Logger.debugLog("Error in getting appointments for the date: $date is: ${error.message}")
                }
            })
    }


    fun getUserDetails(sharedPreference: SharedPreferences) {
        userLiveData.postValue(sharedPreference.getUserFromSharedPrefs())
    }
}