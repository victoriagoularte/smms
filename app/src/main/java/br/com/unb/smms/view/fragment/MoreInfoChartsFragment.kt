package br.com.unb.smms.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import br.com.unb.smms.R
import br.com.unb.smms.databinding.FragmentMoreInfoChartsBinding
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.utils.MPPointF
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MoreInfoChartsFragment : Fragment() {

    private lateinit var likeEntries: ArrayList<Entry>
    private lateinit var commentsEntries: ArrayList<Entry>
    lateinit var binding: FragmentMoreInfoChartsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMoreInfoChartsBinding.inflate(inflater, container, false)
        binding.fragment = this@MoreInfoChartsFragment
        binding.lifecycleOwner = this
//        binding.viewModel = viewModel

        configureCharts(binding.lineChartLikes)
        configureCharts(binding.lineChartComments)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.getSerializable("likesEntries")?.let {
            likeEntries = it as ArrayList<Entry>
            plotChartData(likeEntries, binding.lineChartLikes)
        }

        arguments?.getSerializable("commentsEntries")?.let {
            commentsEntries = it as ArrayList<Entry>
            plotChartData(commentsEntries, binding.lineChartComments)
        }

        arguments?.getString("period")?.let {
            when(it) {
                "day" -> binding.tvPeriod.text = "> Valores relacionados aos posts do dia"
                "month" -> binding.tvPeriod.text = "> Valores relacionados aos posts do mês"
                "year" -> binding.tvPeriod.text = "> Valores relacionados aos posts do ano"
            }
        }
    }


    private fun configureCharts(chart: LineChart) {

        chart.dragDecelerationFrictionCoef = 0.95f
        chart.isHighlightPerTapEnabled = true

        chart.legend.isEnabled = false
        chart.description.isEnabled = false

        chart.animateY(1400, Easing.EaseInOutQuad)
        chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        chart.setNoDataText("Gráfico sem informações")
        chart.xAxis.granularity = 1.0f
        chart.axisLeft.granularity = 1f
        chart.xAxis.setDrawGridLines(false)
        chart.axisRight.isEnabled = false
    }

    private fun plotChartData(entries: List<Entry>, chart: LineChart) {


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



}