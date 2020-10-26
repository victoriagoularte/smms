package br.com.unb.smms.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.unb.smms.databinding.FragmentSchedulerBinding
import br.com.unb.smms.viewmodel.SchedulerViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SchedulerFragment : Fragment() {

    lateinit var binding: FragmentSchedulerBinding
    private val viewModel: SchedulerViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSchedulerBinding.inflate(inflater, container, false)
        binding.fragment = this@SchedulerFragment
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        binding.rvPostsPending.layoutManager = LinearLayoutManager(context)
        binding.rvPostsPending.adapter

        viewModel.resultBanks.observe(viewLifecycleOwner, Observer {
            when(it) {
                is SipagData.Success -> {
                    banks.value = it.data
                    banks.value?.sortBy { bank -> bank.number }

                    adapter = BankAdapter(it.data) {
                        selectedBank(it)
                    }

                    binding.rvBanks.layoutManager = LinearLayoutManager(context)
                    binding.rvBanks.adapter = adapter
                }
                is SipagData.Error -> {
                    Toast.makeText(context, it.error.localizedMessage, Toast.LENGTH_LONG)
                        .show()
                }
            }
        })
    }


}