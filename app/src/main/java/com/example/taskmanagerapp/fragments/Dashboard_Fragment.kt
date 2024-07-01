package com.example.taskmanagerapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.taskmanagerapp.R
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

class Dashboard_Fragment : Fragment() {
    private var settingsIcon: ImageView? = null
    private var completedTaskCountTextView: TextView? = null
    private var dueTaskCountTextView: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_dashboard_, container, false)
        val barChart = rootView.findViewById<BarChart>(R.id.barChart)
        completedTaskCountTextView = rootView.findViewById(R.id.completedTaskCount)
        dueTaskCountTextView = rootView.findViewById(R.id.dueTaskCount)



        val entries = listOf(
            BarEntry(0f, 2f),
            BarEntry(1f, 4f),
            BarEntry(2f, 1f),
            BarEntry(3f, 0f),
            BarEntry(4f, 5f),
            BarEntry(5f, 3f),
            BarEntry(6f, 2f)
        )
        val dataSet = BarDataSet(entries, "Tasks")
        val barData = BarData(dataSet)

        barChart.data = barData
        barChart.description.isEnabled = false

        val xAxis = barChart.xAxis
        val days = arrayOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
        xAxis.valueFormatter = IndexAxisValueFormatter(days)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.setDrawGridLines(false)

        val leftAxis = barChart.axisLeft
        leftAxis.axisMinimum = 0f
        leftAxis.axisMaximum = 12f
        leftAxis.granularity = 2f
        leftAxis.setDrawGridLines(false)

        val rightAxis = barChart.axisRight
        rightAxis.isEnabled = false

        barChart.invalidate()

        settingsIcon = rootView.findViewById(R.id.settingsIcon)
        settingsIcon?.setOnClickListener {
            // Replace with the SettingsFragment
            val settingsFragment = SettingsFragment()
            val transaction: FragmentTransaction = requireFragmentManager().beginTransaction()
            transaction.replace(R.id.fragment_container, settingsFragment)
            transaction.addToBackStack(null)  // Optional: Adds the transaction to the back stack
            transaction.commit()
        }

        return rootView
    }


}