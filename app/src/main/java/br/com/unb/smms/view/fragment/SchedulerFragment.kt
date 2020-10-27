package br.com.unb.smms.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.unb.smms.SmmsData
import br.com.unb.smms.databinding.FragmentSchedulerBinding
import br.com.unb.smms.domain.firebase.Post
import br.com.unb.smms.view.adapter.PostAdapter
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

        viewModel.getPosts()

        binding.rvPostsPending.layoutManager = LinearLayoutManager(context)

        viewModel.posts.observe(viewLifecycleOwner, {
            when(it) {
                is SmmsData.Success -> binding.rvPostsPending.adapter = PostAdapter(it.data) { selectedPost(it) }
                is SmmsData.Error -> Toast.makeText(requireContext(),it.error.localizedMessage, Toast.LENGTH_LONG).show()
            }
        })




    }

    fun selectedPost(post: Post) {
        Toast.makeText(requireContext(), post.title, Toast.LENGTH_LONG).show()
    }


}