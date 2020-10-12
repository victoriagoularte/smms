package br.com.unb.smms.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import br.com.unb.smms.R
import br.com.unb.smms.SmmsData
import br.com.unb.smms.databinding.FragmentAnalitycsBinding
import br.com.unb.smms.viewmodel.AnalitycsViewModel
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.MPPointF
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class AnalitycsFragment : Fragment() {

    lateinit var binding: FragmentAnalitycsBinding
    private val viewModel: AnalitycsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentAnalitycsBinding.inflate(inflater, container, false)
        binding.fragment = this@AnalitycsFragment
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        configureChart()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ArrayAdapter.createFromResource(
            requireContext(), R.array.string_array_metrics,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerPeriod.adapter = adapter
        }

        events()
        viewModel.getFriendsCount()
        viewModel.userIdIg()

//        viewModel.getPostsByPeriod(viewModel.periodSelected.value)
        viewModel.postsFacebook()

        viewModel.resultUserIdIg.observe(viewLifecycleOwner) {
            when (it) {
                is SmmsData.Error -> Toast.makeText(
                    context,
                    it.error.localizedMessage,
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        viewModel.resultPosts.observe(viewLifecycleOwner) {
            Toast.makeText(context, it.size.toString(), Toast.LENGTH_LONG).show()
        }

        viewModel.resultFacebookPosts.observe(viewLifecycleOwner) {
            when(it) {
                is SmmsData.Success -> {
                    val filter = viewModel.filterPostsByPeriod(viewModel.periodSelected.value ?: "day", it.data.data!!)
                    viewModel.postInsightLikes(filter.map { it.id!! }).toString()
                    viewModel.postInsightImpressions(filter.map { it.id!! }).toString()
                    viewModel.postInsightComments(filter.map { it.id!! }).toString()
                }
            }
        }

        viewModel.resultCountLikes.observe(viewLifecycleOwner) {
            binding.tvLikesCount.text = it.toString()
        }

        viewModel.resultCountImpressions.observe(viewLifecycleOwner) {
            binding.tvImpressionsCount.text = it.toString()
        }

        viewModel.resultCountComments.observe(viewLifecycleOwner) {
            binding.tvCommentsCount.text = it.toString()
        }

        viewModel.entries.observe(viewLifecycleOwner) {
            plotChartData(it)
        }

    }

    private fun configureChart() {
        val chart = binding.lineChart

        chart.dragDecelerationFrictionCoef = 0.95f
        chart.isHighlightPerTapEnabled = true

        chart.legend.isEnabled = false
        chart.description.isEnabled = false

        chart.animateY(1400, Easing.EaseInOutQuad)
        chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        chart.setNoDataText("Gráfico sem informações")
        chart.xAxis.granularity = 1.0f
        chart.axisLeft.granularity = 1f
//        chart.xAxis.valueFormatter = ChartDateFormatter()
//        chart.axisLeft.valueFormatter = ChartCurrencyFormatter()
        chart.xAxis.setDrawGridLines(false)
        chart.axisRight.isEnabled = false
    }

    private fun plotChartData(entries: List<Entry>) {
        val chart = binding.lineChart

        if (entries.isNotEmpty()) {
            val dataSet = LineDataSet(entries, null)

            dataSet.setDrawIcons(true)
            dataSet.iconsOffset = MPPointF(0f, 40f)

            dataSet.fillDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.gradient_bg)
            dataSet.setDrawFilled(true)
            dataSet.lineWidth = 2.0F
            dataSet.color = R.color.biometric_error_color

            dataSet.setDrawCircles(false)
            dataSet.setDrawCircleHole(false)

            val data = LineData(dataSet)
            data.setDrawValues(false)

            chart.data = data
        } else {
            chart.data = null
        }

        chart.invalidate()
    }

    class ChartDateFormatter : ValueFormatter() {
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            val date = Date(value.toLong())
            val format = SimpleDateFormat("dd/MM", Locale.getDefault())
            return format.format(date)
        }
    }



    private fun events() {
        binding.spinnerPeriod.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                viewModel.periodSelected.value = "day"
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                viewModel.periodSelected.value = when (position) {
                    0 -> "day"
                    1 -> "month"
                    else -> "year"
                }

                viewModel.resultCountLikes.value = 0
                viewModel.resultCountComments.value = 0
                viewModel.resultCountImpressions.value = 0

                viewModel.postsFacebook()
            }

        }

    }


}



//enum {NONE, LIKE, LOVE, WOW, HAHA, SAD, ANGRY, THANKFUL, PRIDE, CARE} to reations
