package br.com.unb.smms.view.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import br.com.unb.smms.R
import br.com.unb.smms.SmmsData
import br.com.unb.smms.SmmsData.Error
import br.com.unb.smms.SmmsData.Success
import br.com.unb.smms.databinding.ActivitySmmsBinding
import br.com.unb.smms.viewmodel.SmmsViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener

class SmmsActivity : AppCompatActivity() {

    lateinit var binding: ActivitySmmsBinding
    private lateinit var navOptions: NavOptions
    private lateinit var navController: NavController


    private val viewModel: SmmsViewModel by lazy {
        ViewModelProviders.of(this).get(SmmsViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySmmsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        navController =
            Navigation.findNavController(this@SmmsActivity, R.id.smmsNavigationFragment)

        navOptions = NavOptions.Builder()
            .setLaunchSingleTop(true)
            .setPopUpTo(navController.graph.startDestination, false)
            .build()
    }

    override fun onResume() {
        super.onResume()

        setParameters()
        configNav()
    }

    private fun setParameters() {

        viewModel.access()
        viewModel.getUserProfilePicture()

        viewModel.resultPic.observe(this@SmmsActivity, Observer {
            when (it) {
                is Success -> binding.ivUserPic.setImageBitmap(it.data)
                is Error -> Toast.makeText(
                    this@SmmsActivity,
                    it.error.localizedMessage,
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        viewModel.resultPageInfo.observe(this@SmmsActivity, Observer {
            when (it) {
                is SmmsData.Error -> Toast.makeText(
                    this@SmmsActivity,
                    it.error.localizedMessage,
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

    }

    private fun configNav() {
        binding.tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> navController.navigate(R.id.smmsFragment, null, navOptions)
                    1 -> navController.navigate(R.id.newPostFragment, null, navOptions)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

}
