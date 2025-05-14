package com.SPAccounting.HealthX.ui.mainFragments.home.appointment_booking

import android.app.Application
import com.SPAccounting.HealthX.base.BaseViewModel
import com.SPAccounting.HealthX.model.User

class DoctorDetailsViewModel(application: Application) : BaseViewModel(application) {

    private var doctor: User = User()

    fun initDoctor(doctorDetails: User) {
        doctor = doctorDetails
    }

    fun getDoctor(): User {
        return doctor
    }

}
