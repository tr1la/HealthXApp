package com.SPAccounting.HealthX.ui.adapter

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.SPAccounting.HealthX.model.DoctorAppointment
import com.SPAccounting.HealthX.R
import com.SPAccounting.HealthX.utils.Logger
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date


class PatientQueueAdapter(
    private val userID: String,
    val listener: (DoctorAppointment) -> Unit,
) : RecyclerView.Adapter<PatientQueueAdapter.DoctorAppointmentViewHolder>() {

    private var appointmentList: ArrayList<DoctorAppointment> = ArrayList()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorAppointmentViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.appointment_list, parent, false)
        return DoctorAppointmentViewHolder(itemView)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onBindViewHolder(holder: DoctorAppointmentViewHolder, position: Int) {

        val currentItem = appointmentList[position]
        val context = holder.itemView.context

        if (currentItem.PatientPhone == "" || currentItem.PatientPhone!!.isEmpty()) {
            holder.name.text = currentItem.PatientName
        } else {
            holder.name.text = context.getString(
                R.string.patient_name_with_phone,
                currentItem.PatientName,
                currentItem.PatientPhone
            )
        }

        val userAndDoctorAreSamePerson = currentItem.DoctorUID == userID

        Logger.debugLog("CurrentUser: $userID and DoctorID: ${currentItem.DoctorUID} are same person: $userAndDoctorAreSamePerson")

        if (currentItem.DoctorUID == userID) {
            val datesList: List<Date?> = getDates(currentItem.Date, currentItem.Time)
            /** 0th Index = dateNow, 1st Index = dateAppointment */

            Logger.debugLog("Current Date Time: ${datesList[0]}")
            Logger.debugLog("Appointment Date Time: ${datesList[1]}")
            datesList[1]?.let {
                val isBefore = it.before(datesList[0])
                if (isBefore) {
                    Logger.debugLog("Appointment Date Time is after Current Date Time")
                    holder.rate.visibility = View.VISIBLE
                } else {
                    Logger.debugLog("Appointment Date Time is before Current Date Time")
                    holder.rate.visibility = View.GONE
                }
            }
        }

        holder.disease.text = context.getString(
            R.string.disease_and_condition,
            currentItem.Disease,
            currentItem.PatientCondition
        )
        
        holder.button.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // For Android 13 and above
                Dexter.withContext(holder.button.context)
                    .withPermission(Manifest.permission.READ_MEDIA_IMAGES)
                    .withListener(object : PermissionListener {
                        override fun onPermissionGranted(response: PermissionGrantedResponse) {
                            openPrescription(currentItem, holder)
                        }

                        override fun onPermissionDenied(response: PermissionDeniedResponse) {
                            Toast.makeText(
                                holder.button.context, 
                                context.getString(R.string.permission_required), 
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        override fun onPermissionRationaleShouldBeShown(
                            permission: PermissionRequest,
                            token: PermissionToken
                        ) {
                            token.continuePermissionRequest()
                        }
                    }).check()
            } else {
                // For Android 12 and below
                openPrescription(currentItem, holder)
            }
        }

        holder.rate.setOnClickListener {
            listener(currentItem)
        }
    }

    private fun openPrescription(currentItem: DoctorAppointment, holder: DoctorAppointmentViewHolder) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(currentItem.Prescription.toString().trim()))
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        holder.button.context.startActivity(intent)
    }

    @SuppressLint("SimpleDateFormat")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getDates(date: String?, time: String?): List<Date?> {
        val currentDateTime = LocalDateTime.now()
        val format = SimpleDateFormat("dd-MM-yyyy HH:mm")
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
        val formattedDateTime = currentDateTime.format(formatter)
        val dateNow = format.parse(formattedDateTime)
        val appointmentDateTime = date + " " + time!!.substring(0, 5)
        val dateAppointment = format.parse(appointmentDateTime)
        return listOf(dateNow, dateAppointment)
    }

    override fun getItemCount() = appointmentList.size

    class DoctorAppointmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val name: TextView = itemView.findViewById(R.id.nameDisplay)
        val disease: TextView = itemView.findViewById(R.id.diseaseDisplay)
        val button: ImageView = itemView.findViewById(R.id.downloadPrescription)
        val rate: ImageView = itemView.findViewById(R.id.rateUser)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(appointmentList: ArrayList<DoctorAppointment>) {
        this.appointmentList.clear()
        this.appointmentList = appointmentList
        notifyDataSetChanged()
    }
}