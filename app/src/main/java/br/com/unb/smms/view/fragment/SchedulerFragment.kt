package br.com.unb.smms.view.fragment

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import br.com.unb.smms.R
import br.com.unb.smms.databinding.FragmentNewPostBinding
import br.com.unb.smms.databinding.FragmentSchedulerBinding
import br.com.unb.smms.databinding.SchedulerFragmentBindingImpl
import br.com.unb.smms.viewmodel.NewPostViewModel
import br.com.unb.smms.viewmodel.SchedulerViewModel
import com.google.firebase.storage.FirebaseStorage
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


}