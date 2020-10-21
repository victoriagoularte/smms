package br.com.unb.smms.view.fragment

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.unb.smms.R
import br.com.unb.smms.viewmodel.SchedulerViewModel

class SchedulerFragment : Fragment() {

    companion object {
        fun newInstance() = SchedulerFragment()
    }

    private lateinit var viewModel: SchedulerViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.scheduler_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SchedulerViewModel::class.java)
        // TODO: Use the ViewModel
    }

}