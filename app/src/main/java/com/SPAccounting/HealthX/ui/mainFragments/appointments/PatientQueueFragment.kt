package com.SPAccounting.HealthX.ui.mainFragments.appointments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.SPAccounting.HealthX.base.ViewModelFactory
import com.SPAccounting.HealthX.databinding.FragmentPatientQueueBinding
import com.SPAccounting.HealthX.databinding.RatingModalBinding
import com.SPAccounting.HealthX.model.DoctorAppointment
import com.SPAccounting.HealthX.model.Rating
import com.SPAccounting.HealthX.ui.adapter.PatientQueueAdapter
import com.SPAccounting.HealthX.utils.Constants
import com.SPAccounting.HealthX.utils.DialogUtil.createBottomSheet
import com.SPAccounting.HealthX.utils.DialogUtil.setBottomSheet
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.*


class PatientQueueFragment : Fragment() {

    private var _binding: FragmentPatientQueueBinding? = null
    private val binding get() = _binding!!
    private val patientQueueViewModel by viewModels<PatientQueueViewModel> { ViewModelFactory() }
    private val args: PatientQueueFragmentArgs by navArgs()
    private lateinit var recyclerView: RecyclerView
    private lateinit var patientQueueAdapter: PatientQueueAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPatientQueueBinding.inflate(inflater, container, false)

        initObservers()
        initViews()

        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun initViews() {
        binding.run {
            recyclerView = binding.appointmentRecyclerview
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            recyclerView.setHasFixedSize(true)
            patientQueueAdapter = PatientQueueAdapter(args.doctorUserID) {
                showRatingBottomSheet(it)
            }
            recyclerView.adapter = patientQueueAdapter

            // Thêm xử lý chọn ngày
            if (!args.hideSelectedDate) {
                selectDate.visibility = View.VISIBLE
                selectDate.setOnClickListener {
                    val datePicker = MaterialDatePicker.Builder.datePicker().build()
                    requireActivity().supportFragmentManager.let {
                        datePicker.show(it, Constants.DatePicker)
                    }
                    datePicker.addOnPositiveButtonClickListener { selectedDate ->
                        val dateFormatter = SimpleDateFormat(Constants.dateFormat)
                        val formattedDate = dateFormatter.format(Date(selectedDate))
                        selectedDateText.text = "Selected Date: $formattedDate"
                        patientQueueViewModel.setPassedData(
                            args.doctorUserID,
                            formattedDate,
                            args.hideSelectedDate
                        )
                        patientQueueViewModel.getPatientsListFromFirebase()
                    }
                }
            } else {
                selectDate.visibility = View.GONE
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun getAppointmentDateTime(date: String?, time: String): String {
        val format = SimpleDateFormat("dd-MM-yyyy HH:mm")
        val appointmentDateTime = date + " " + time.substring(0, 5)
        return format.parse(appointmentDateTime)?.toString() ?: ""
    }

    private fun showRatingBottomSheet(doctorAppointment: DoctorAppointment) {
        val dialog = RatingModalBinding.inflate(layoutInflater)
        val bottomSheet = requireContext().createBottomSheet()
        dialog.apply {
            this.apply {
                submitRating.setOnClickListener {
                    val rating = Rating(
                        patientId = doctorAppointment.PatientID!!,
                        doctorId = doctorAppointment.DoctorUID!!,
                        rating = ratingBar.rating,
                        review = ratingComment.text.toString().trim(),
                        patientName = doctorAppointment.PatientName!!,
                        timestamp = getAppointmentDateTime(
                            doctorAppointment.Date,
                            doctorAppointment.Time!!
                        )
                    )
                    patientQueueViewModel.updateRating(rating)
                    bottomSheet.dismiss()
                }
            }
        }
        dialog.root.setBottomSheet(bottomSheet)
    }

    private fun initObservers() {
        patientQueueViewModel.run {
            setPassedData(
                args.doctorUserID,
                args.selectedDate,
                args.hideSelectedDate
            )
            doctorUserID.observe(viewLifecycleOwner) {
                getPatientsListFromFirebase()
                binding.selectedDateText.text = "Selected Date: ${selectedDate.value}"
            }
            patientsListLiveData.observe(viewLifecycleOwner) {
                patientQueueAdapter.setData(it as ArrayList<DoctorAppointment>)
            }
        }
    }
}