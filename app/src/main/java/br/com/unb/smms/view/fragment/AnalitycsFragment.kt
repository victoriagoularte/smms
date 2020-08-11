package br.com.unb.smms.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import br.com.unb.smms.R
import br.com.unb.smms.SmmsData
import br.com.unb.smms.databinding.FragmentAnalitycsBinding
import br.com.unb.smms.viewmodel.AnalitycsViewModel


class AnalitycsFragment : Fragment() {

    lateinit var binding: FragmentAnalitycsBinding

    private val viewModel: AnalitycsViewModel by lazy {
        ViewModelProvider(this).get(AnalitycsViewModel::class.java)
    }

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

        viewModel.resultUserIdIg.observe(viewLifecycleOwner, Observer {
            when (it) {
                is SmmsData.Error -> Toast.makeText(
                    context,
                    it.error.localizedMessage,
                    Toast.LENGTH_LONG
                ).show()
            }
        })

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
                    2 -> "month"
                    else -> "year"
                }
            }
        }

    }

}
