package com.SPAccounting.HealthX.ui.adapter

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.SPAccounting.HealthX.databinding.StatiscticsCardListBinding
import com.SPAccounting.HealthX.model.HealthData
import com.SPAccounting.HealthX.utils.DateTimeExtension.convertTimestampToDateTime


class StatsListAdapter(
    private val listener: (HealthData) -> Unit
) : RecyclerView.Adapter<StatsListAdapter.StatsViewHolder>() {

    private var testList: List<HealthData> = ArrayList()

    class StatsViewHolder(val binding: StatiscticsCardListBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatsViewHolder {
        return StatsViewHolder(
            StatiscticsCardListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return testList.size
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: StatsViewHolder, position: Int) {
        val healthData = testList[position]

        holder.binding.apply {
            testName.text = healthData.name
            val sortedTests = healthData.tests?.sortedBy { it.dateTime } ?: emptyList()
            val sortedResults = sortedTests.map { 
                try {
                    it.result?.toInt() ?: 0
                } catch (e: NumberFormatException) {
                    0
                }
            }
            val min = sortedResults.minOrNull() ?: 0
            val max = sortedResults.maxOrNull() ?: 0
            testDataRange.text = "Min: $min - Max: $max"
            testDataStats.setData(sortedResults as ArrayList<Int>)
            testDateTime.text = healthData.healthId?.convertTimestampToDateTime()

            parentLayout.setOnClickListener {
                listener(healthData)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addItems(list: List<HealthData>) {
        testList = list
        notifyDataSetChanged()
    }
}
