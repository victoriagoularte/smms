package br.com.unb.smms.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import br.com.unb.smms.SmmsData
import br.com.unb.smms.databinding.FragmentAnalitycsBinding
import br.com.unb.smms.viewmodel.AnalitycsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AnalitycsFragment : Fragment() {

    lateinit var binding: FragmentAnalitycsBinding
    private val viewModel: AnalitycsViewModel by viewModels()

//    private val viewModel: AnalitycsViewModel by lazy {
//        ViewModelProvider(this).get(AnalitycsViewModel::class.java)
//    }

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


}
