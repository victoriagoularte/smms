package br.com.unb.smms.view.fragment

import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.unb.smms.R
import br.com.unb.smms.SmmsData
import br.com.unb.smms.databinding.FragmentSchedulerBinding
import br.com.unb.smms.domain.firebase.Post
import br.com.unb.smms.view.adapter.PostAdapter
import br.com.unb.smms.viewmodel.SchedulerViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_scheduler.*

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
                is SmmsData.Success -> binding.rvPostsPending.adapter = PostAdapter(it.data) { post -> selectedPost(post) }
                is SmmsData.Error -> Toast.makeText(requireContext(),it.error.localizedMessage, Toast.LENGTH_LONG).show()
            }
        })

        viewModel.resultPost.observe(viewLifecycleOwner, {
            when(it) {
                is SmmsData.Success -> Toast.makeText(context, "post realizado com sucesso", Toast.LENGTH_LONG).show()
                is SmmsData.Error -> Toast.makeText(context, "error: post nao efetuado", Toast.LENGTH_LONG).show()
            }
        })

        viewModel.resultUpdate.observe(viewLifecycleOwner, {
            when(it) {
                is SmmsData.Success -> {
                    Toast.makeText(context, "post atualizado com sucesso", Toast.LENGTH_LONG).show()

                }
                is SmmsData.Error -> Toast.makeText(context, "error: post nao atualizado", Toast.LENGTH_LONG).show()
            }
        })

    }

    fun selectedPost(post: Post) {
        MaterialAlertDialogBuilder(requireContext(), R.style.AppTheme)
            .setTitle(resources.getString(R.string.confirm_post))
            .setMessage(String.format(resources.getString(R.string.body_confirmation), post.title))
            .setNeutralButton(resources.getString(R.string.cancel)) { dialog, which ->
                dialog?.let { dialog.dismiss() }
            }
            .setNegativeButton(resources.getString(R.string.remove)) { dialog, which ->
                // Respond to negative button press
            }
            .setPositiveButton(resources.getString(R.string.publish)) { dialog, which ->
                viewModel.postPublishPending(post)
            }
            .show()
    }


}