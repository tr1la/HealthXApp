package com.SPAccounting.HealthX.ui.mainFragments.home.appointment_booking

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.SPAccounting.HealthX.R
import com.SPAccounting.HealthX.databinding.FragmentBookingSummaryBinding


class BookingSummaryFragment : Fragment() {

    private lateinit var _binding: FragmentBookingSummaryBinding
    private val binding get() = _binding
    private val args: BookingSummaryFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookingSummaryBinding.inflate(inflater, container, false)

        initView()

        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        binding.apply {
            summaryDoctorName.text = getString(R.string.doctor_name_format, args.summary.doctorName)
            summaryDoctorSpeciality.text = getString(R.string.doctor_speciality_format, args.summary.doctorSpeciality)
//            summaryDoctorEmail.text = getString(R.string.doctor_email_format, args.summary.doctorEmail)
//            summaryDoctorPhoneNumber.text = getString(R.string.doctor_phone_format, args.summary.doctorPhone)
            summaryDate.text = getString(R.string.appointment_date_format, args.summary.appointmentDate)
            summaryTime.text = getString(R.string.appointment_time_format, args.summary.appointmentTime)
            btnHome.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

}