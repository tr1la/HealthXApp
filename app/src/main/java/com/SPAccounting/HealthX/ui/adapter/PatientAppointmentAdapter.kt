package com.SPAccounting.HealthX.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.SPAccounting.HealthX.model.PatientAppointment
import com.SPAccounting.HealthX.R

class PatientAppointmentAdapter(
    val listener: (PatientAppointment) -> Unit
) :
    RecyclerView.Adapter<PatientAppointmentAdapter.PatientAppointmentViewHolder>() {

    private var appointmentList = mutableListOf<PatientAppointment>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PatientAppointmentViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.patient_list, parent, false)
        return PatientAppointmentViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return appointmentList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: PatientAppointmentViewHolder, position: Int) {
        val currentItem = appointmentList[position]
        val context = holder.itemView.context

        holder.apply {
            name.text = context.getString(R.string.doctor_name, currentItem.DoctorName)
            disease.text = context.getString(R.string.disease, currentItem.Disease)
            time.text = context.getString(R.string.time_format, currentItem.Time)
            date.text = context.getString(R.string.date_format, currentItem.Date)
            itemView.setOnClickListener {
                listener(currentItem)
            }
        }
    }

    class PatientAppointmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.nameDisplay)
        val disease: TextView = itemView.findViewById(R.id.diseaseDisplay)
        val time: TextView = itemView.findViewById(R.id.timeDisplay)
        val date: TextView = itemView.findViewById(R.id.dateDisplay)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(appointment: List<PatientAppointment>) {
        this.appointmentList.clear()
        this.appointmentList.addAll(appointment)
        notifyDataSetChanged()
    }
}

