package com.SPAccounting.HealthX.ui.mainFragments.appointments

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.SPAccounting.HealthX.R
import com.SPAccounting.HealthX.base.ViewModelFactory
import com.SPAccounting.HealthX.databinding.FragmentMyAppointmentBinding
import com.SPAccounting.HealthX.model.Doctor
import com.SPAccounting.HealthX.ui.adapter.PatientAppointmentAdapter
import com.SPAccounting.HealthX.utils.Constants
import com.SPAccounting.HealthX.utils.DateTimeExtension
import com.SPAccounting.HealthX.utils.Utils.show
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*


class MyAppointmentFragment : Fragment() {

    private var _binding: FragmentMyAppointmentBinding? = null
    private val binding get() = _binding!!
    private val myAppointmentsViewModel by viewModels<MyAppointmentsViewModel> { ViewModelFactory() }
    private lateinit var recyclerview: RecyclerView
    private lateinit var appointmentAdapter: PatientAppointmentAdapter
    private lateinit var sharedPreference: SharedPreferences

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyAppointmentBinding.inflate(inflater, container, false)
        sharedPreference =
            requireActivity().getSharedPreferences(Constants.UserData, Context.MODE_PRIVATE)

        initObservers()
        initViews()

        return binding.root
    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun initViews() {
        binding.run {
            appointmentAdapter = PatientAppointmentAdapter {
                navigateToPatientQueue(it.DoctorUID!!, it.Date, true)
            }
            recyclerview = binding.appointmentRecyclerview
            recyclerview.adapter = appointmentAdapter
            recyclerview.layoutManager = LinearLayoutManager(requireActivity())
            recyclerview.setHasFixedSize(true)

            binding.toPatientList.setOnClickListener {
                val user = myAppointmentsViewModel.userLiveData.value
                navigateToPatientQueue(user!!.UID!!, DateTimeExtension.getCurrentDateAsString(), false)
            }

            binding.selectDate.setOnClickListener {
                val datePicker = MaterialDatePicker.Builder.datePicker().build()
                requireActivity().supportFragmentManager.let {
                    datePicker.show(
                        it,
                        getString(R.string.date_picker)
                    )
                }
                datePicker.addOnPositiveButtonClickListener {
                    val dateFormatter = SimpleDateFormat(Constants.dateFormat)
                    val tempDate = dateFormatter.format(Date(it))
                    binding.selectedDateText.text = getString(R.string.selected_date, tempDate)
                    myAppointmentsViewModel.getAppointmentsForTheDate(tempDate)
                }
            }
        }
    }

    private fun navigateToPatientQueue(doctorUID: String, selectedDate: String?, hideCalendarView: Boolean) {
        val action = MyAppointmentFragmentDirections.actionAppointmentToPatientQueueFragment(
            selectedDate!!,
            doctorUID
        )
        findNavController().navigate(action)
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun initObservers() {
        myAppointmentsViewModel.run {
            getUserDetails(sharedPreference)
            userLiveData.observe(viewLifecycleOwner) {
                val dateNow = DateTimeExtension.getCurrentDateAsString()
                getAppointmentsForTheDate(dateNow)
                binding.selectedDateText.text = getString(R.string.selected_date, dateNow)
                if (it.isDoctor == Doctor.IS_DOCTOR.toItemString())
                    binding.toPatientList.show()
            }
            myAppointmentList.observe(viewLifecycleOwner) {
                appointmentAdapter.setData(it)
            }
        }
    }
}

