package com.openclassrooms.realestatemanager.loan

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.marginStart
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.ActivityLoaningBinding
import kotlin.math.pow
import kotlin.math.roundToInt

class LoaningActivity : AppCompatActivity() {
    private lateinit var amount: String
    private lateinit var rate: String
    private lateinit var down: String
    var years: Int = 0

    lateinit var binding: ActivityLoaningBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loaning)

        configureBinding()
        getSeekBar()
        buttonClick()
    }

    private fun configureBinding() {
        binding = ActivityLoaningBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    private fun getInfoFromUi(){
        amount= binding.editTextAmount.text.toString()
        rate = binding.editTextRate.text.toString()
        down = binding.editTextDown.text.toString()
    }

    private fun getSeekBar(){
        binding.seekBarYears.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            var progressChangeValue = 0
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                progressChangeValue = progress
                binding.textViewProgressSeekBar.text = progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                years = progressChangeValue
                Toast.makeText(this@LoaningActivity, "Seek bar progress is :" + progressChangeValue,
                        Toast.LENGTH_SHORT).show();
            }
        })
    }

    private fun buttonClick(){
        binding.buttonCalculate.setOnClickListener(View.OnClickListener {
            getInfoFromUi()
            calculateMortgage()

            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
        })
    }

    private fun calculateMortgage() {
        val secureAmount: Double = if (amount.isNotEmpty()) amount.toDouble()
        else 0.0
        val secureRate: Double = if (rate.isNotEmpty()) rate.toDouble()
        else 0.0
        val secureDown: Double = if (down.isNotEmpty()) down.toDouble()
        else 0.0

        Log.d("loan", "Mortgage: $amount + $rate")
        Log.d("loan", "calculateMortgage: $secureAmount + $secureRate")
        if (secureAmount != 0.0 || secureRate != 0.0) {
            val interest = secureRate / 100 / 12
            val months = years.times(12)
            val totalResult: Int = if (secureAmount == 0.0 || secureRate == 0.0) 0
            else
                (((secureAmount - secureDown) * (interest * (1 + interest).pow(months) / ((1 + interest).pow(months) - 1))) * months).roundToInt()

            val monthlyResult: Int = totalResult / months
            binding.apply {
                textViewTotalCoast.visibility = View.VISIBLE
                textViewTotalCoastEmptyField.visibility = View.VISIBLE
                textViewTotalCoastEmptyField.text = totalResult.toString()

                textViewMonthlyCoast.visibility = View.VISIBLE
                textViewMonthlyCoastEmptyField.visibility = View.VISIBLE
                binding.textViewMonthlyCoastEmptyField.text = monthlyResult.toString()
            }
        } else {
            binding.textViewNoResult.visibility = View.VISIBLE
            binding.textViewNoResult.text = getString(R.string.no_result)
        }
    }
}