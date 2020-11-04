package br.com.unb.smms.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import br.com.unb.smms.databinding.SearchFragmentBinding
import br.com.unb.smms.viewmodel.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {

    lateinit var binding: SearchFragmentBinding
    private val viewModel: SearchViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = SearchFragmentBinding.inflate(inflater, container, false)
        binding.fragment = this@SearchFragment
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
//        binding.progressBar.visibility = View.VISIBLE

        return binding.root
    }

    fun search(view: View) {
        viewModel.search()
    }

}