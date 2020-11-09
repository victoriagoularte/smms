package br.com.unb.smms.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.unb.smms.R
import br.com.unb.smms.SmmsData
import br.com.unb.smms.databinding.SearchFragmentBinding
import br.com.unb.smms.domain.firebase.Post
import br.com.unb.smms.view.adapter.PostAdapter
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvPosts.layoutManager = LinearLayoutManager(context)

        viewModel.resultSearch.observe(viewLifecycleOwner) {
            when (it) {
                is SmmsData.Success -> {
                    if (it.data.isEmpty()) {
                        binding.tvPostsNotFound.visibility = View.VISIBLE
                        binding.rvPosts.visibility = View.GONE
                    } else {
                        binding.tvPostsNotFound.visibility = View.GONE
                        binding.rvPosts.visibility = View.VISIBLE
                        binding.rvPosts.adapter = PostAdapter(it.data) { post ->
                            TODO()
                        }
                    }
                }
                is SmmsData.Error -> {
                    Toast.makeText(requireContext(), it.error.localizedMessage, Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }



    fun search(view: View) {
        viewModel.search()
    }


}