package br.com.unb.smms.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import br.com.unb.smms.R
import br.com.unb.smms.SmmsData
import br.com.unb.smms.databinding.FragmentAnalitycsBinding
import br.com.unb.smms.viewmodel.AnalitycsViewModel
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import dagger.hilt.android.AndroidEntryPoint

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
                    viewModel.postInsightLikes(filter.map { it -> it.id!! }).toString()
                    viewModel.postInsightImpressions(filter.map { it -> it.id!! }).toString()
                    viewModel.postInsightComments(filter.map { it -> it.id!! }).toString()
                }
            }
        }

        viewModel.resultCountLikes.observe(viewLifecycleOwner) {
            binding.tvLikesCount.text = it.toString()
            drawChart(viewModel.entries.value)
        }

        viewModel.resultCountImpressions.observe(viewLifecycleOwner) {
            binding.tvImpressionsCount.text = it.toString()
        }

        viewModel.resultCountComments.observe(viewLifecycleOwner) {
            binding.tvCommentsCount.text = it.toString()
        }

    }

    private fun drawChart(entries: ArrayList<Entry>?) {

        val vl = LineDataSet(entries, viewModel.periodSelected.value)

        vl.setDrawValues(false)
        vl.setDrawFilled(true)
        vl.lineWidth = 3f
        vl.fillColor = R.color.gray
        vl.fillAlpha = R.color.colorAccent

        binding.lineChart.xAxis.labelRotationAngle = 0f
        binding.lineChart.data = LineData(vl)
        binding.lineChart.axisRight.isEnabled = false
        binding.lineChart.xAxis.axisMaximum = 10+0.1f
        binding.lineChart.setTouchEnabled(true)
        binding.lineChart.setPinchZoom(true)
        binding.lineChart.description.text = "Days"
        binding.lineChart.setNoDataText("No forex yet!")
        binding.lineChart.animateX(1800, Easing.EaseInExpo)
//        val markerView = CustomMarker(this@ShowForexActivity, R.layout.marker_view)
//        binding.lineChart.marker = markerView

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
