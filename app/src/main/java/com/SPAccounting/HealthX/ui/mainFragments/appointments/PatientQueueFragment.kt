package com.SPAccounting.HealthX.ui.mainFragments.appointments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.SPAccounting.HealthX.R
import com.SPAccounting.HealthX.base.ViewModelFactory
import com.SPAccounting.HealthX.databinding.FragmentPatientQueueBinding
import com.SPAccounting.HealthX.databinding.RatingModalBinding
import com.SPAccounting.HealthX.model.DoctorAppointment
import com.SPAccounting.HealthX.model.Rating
import com.SPAccounting.HealthX.ui.adapter.PatientQueueAdapter
import com.SPAccounting.HealthX.utils.Constants
import com.SPAccounting.HealthX.utils.DialogUtil.createBottomSheet
import com.SPAccounting.HealthX.utils.DialogUtil.setBottomSheet
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
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

            // Luôn hiển thị date picker cho bác sĩ
            selectDate.visibility = View.VISIBLE
            selectDate.setOnClickListener {
                val datePicker = MaterialDatePicker.Builder.datePicker().apply {
                    // disable past dates
                    val constraintsBuilder = CalendarConstraints.Builder()
                    constraintsBuilder.setValidator(DateValidatorPointForward.now())
                    setCalendarConstraints(constraintsBuilder.build())

                    // set the minimum selectable date to today's date
                    val calendar = Calendar.getInstance()
                    setSelection(calendar.timeInMillis)
                }.build()
                
                requireActivity().supportFragmentManager.let {
                    datePicker.show(it, getString(R.string.date_picker))
                }
                
                datePicker.addOnPositiveButtonClickListener { selectedDate ->
                    try {
                        val dateFormatter = SimpleDateFormat(Constants.dateFormat)
                        val formattedDate = dateFormatter.format(Date(selectedDate))
                        selectedDateText.text = getString(R.string.selected_date, formattedDate)
                        patientQueueViewModel.setPassedData(
                            args.doctorUserID,
                            formattedDate,
                            false // Luôn cho phép chọn ngày
                        )
                        patientQueueViewModel.getPatientsListFromFirebase()
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), 
                            getString(R.string.error_selecting_date, e.message), 
                            Toast.LENGTH_SHORT).show()
                    }
                }
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
                false // Luôn cho phép chọn ngày
            )
            
            // Hiển thị ngày đã chọn ngay khi fragment được tạo
            binding.selectedDateText.text = getString(R.string.selected_date, args.selectedDate)
            
            doctorUserID.observe(viewLifecycleOwner) {
                getPatientsListFromFirebase()
            }
            patientsListLiveData.observe(viewLifecycleOwner) {
                if (it.isEmpty()) {
                    binding.noAppointmentText.text = getString(R.string.no_appointment_for_this_date)
                    binding.noAppointmentText.visibility = View.VISIBLE
                    binding.appointmentRecyclerview.visibility = View.GONE
                } else {
                    binding.noAppointmentText.visibility = View.GONE
                    binding.appointmentRecyclerview.visibility = View.VISIBLE
                    patientQueueAdapter.setData(it as ArrayList<DoctorAppointment>)
                }
            }
        }
    }
}