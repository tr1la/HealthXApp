package com.SPAccounting.HealthX.ui.mainFragments.stats

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.SPAccounting.HealthX.R
import com.SPAccounting.HealthX.base.ViewModelFactory
import com.SPAccounting.HealthX.databinding.FragmentAddStatsDataBinding
import com.SPAccounting.HealthX.utils.Constants
import com.SPAccounting.HealthX.utils.Logger


class AddStatsDataFragment : Fragment() {

    private var _binding: FragmentAddStatsDataBinding? = null
    private val args by navArgs<AddStatsDataFragmentArgs>()
    private val addStatsDataViewModel by viewModels<AddStatsDataViewModel> { ViewModelFactory() }
    private lateinit var sharedPreferences: SharedPreferences
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAddStatsDataBinding.inflate(inflater, container, false)
        sharedPreferences = requireActivity().getSharedPreferences(Constants.UserData, Context.MODE_PRIVATE)

        initObserver()
        initView()

        return binding.root
    }

    private fun initObserver() {
        addStatsDataViewModel.run {
            saveDataFromSharedPreferences(sharedPreferences)
            if (args.stats.healthId != null) {
                Logger.debugLog("AddStatsDataFragment HealthId: ${args.stats}")
                setHealthData(args.stats)
                binding.testNameEditText.setEditTextBox(args.stats.name)
            } else {
                setHealthId()
            }
//            statsList.observe(viewLifecycleOwner) {
//                Toast.makeText(context, "Added new data", Toast.LENGTH_SHORT).show() //Temporary
//            }
            enableButton.observe(viewLifecycleOwner) {
                binding.saveButton.isEnabled = it
                binding.saveButton.setButtonEnabled(it)
            }
            isDataSaved.observe(viewLifecycleOwner) {
                if (it) {
                    Toast.makeText(context, getString(R.string.data_saved_message), Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
            }
        }
    }

    private fun initView() {
        binding.run {
            testNameEditText.setUserInputListener {
                addStatsDataViewModel.setTestName(it)
            }
            testResultEditText.setUserInputListener {
                addStatsDataViewModel.setTestResult(it)
            }
            saveButton.setOnClickListener {
                addStatsDataViewModel.saveDataInFirebase()
            }
        }
    }
}