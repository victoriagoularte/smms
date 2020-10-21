package br.com.unb.smms.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import br.com.unb.smms.R
import br.com.unb.smms.SmmsData
import br.com.unb.smms.databinding.FragmentAnalitycsBinding
import br.com.unb.smms.domain.facebook.PostFacebook
import br.com.unb.smms.viewmodel.AnalitycsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AnalitycsFragment : Fragment() {

    lateinit var binding: FragmentAnalitycsBinding
    private val viewModel: AnalitycsViewModel by viewModels()
    lateinit var listPosts: List<PostFacebook>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentAnalitycsBinding.inflate(inflater, container, false)
        binding.fragment = this@AnalitycsFragment
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        resetFields()

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
                    listPosts = viewModel.filterPostsByPeriod(viewModel.periodSelected.value ?: "day", it.data.data!!)
                    viewModel.postInsightLikes(listPosts.map { it.id!! }).toString()
                    viewModel.postInsightImpressions(listPosts.map { it.id!! }).toString()
                    viewModel.postInsightComments(listPosts.map { it.id!! }).toString()
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

    }

    private fun resetFields() {
        viewModel.resultCountComments.value = 0
        viewModel.resultCountLikes.value = 0
        viewModel.resultCountImpressions.value = 0
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

    fun moreInfoChart(view: View) {
        val bundle = bundleOf("period" to viewModel.periodSelected.value, "likesEntries" to viewModel.entriesLikes.value, "commentsEntries" to viewModel.entriesComments.value, "listPostIds" to listPosts)
        findNavController().navigate(R.id.action_smmsFragment_to_moreInfoChartsFragment, bundle)
    }


}



//enum {NONE, LIKE, LOVE, WOW, HAHA, SAD, ANGRY, THANKFUL, PRIDE, CARE} to reations
