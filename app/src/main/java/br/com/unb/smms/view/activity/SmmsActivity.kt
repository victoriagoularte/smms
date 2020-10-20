package br.com.unb.smms.view.activity

import TimePickerFragment
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import br.com.unb.smms.utils.AlarmReceiver
import br.com.unb.smms.R
import br.com.unb.smms.SmmsData.Error
import br.com.unb.smms.SmmsData.Success
import br.com.unb.smms.databinding.ActivitySmmsBinding
import br.com.unb.smms.viewmodel.NewPostViewModel
import br.com.unb.smms.viewmodel.SmmsViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class SmmsActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    lateinit var binding: ActivitySmmsBinding
    private val viewModel: SmmsViewModel by viewModels()
    private val newPostViewModel: NewPostViewModel by viewModels()

    private lateinit var navOptions: NavOptions
    private lateinit var navController: NavController
    val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySmmsBinding.inflate(layoutInflater)

        val view = binding.root
        setContentView(view)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        setParameters()
    }


    private fun setParameters() {

        configNav()

        viewModel.access()
        viewModel.getUserProfilePicture()

        viewModel.resultAccess.observe(this@SmmsActivity, Observer {
            when (it) {
                is Success -> {

                    binding.tvName.text = it.data.name

                    navController =
                        Navigation.findNavController(this@SmmsActivity, R.id.smmsNavigationFragment)

                    navOptions = NavOptions.Builder()
                        .setLaunchSingleTop(true)
                        .setPopUpTo(navController.graph.startDestination, false)
                        .build()

                    configNav()
                }
                is Error -> Toast.makeText(
                    this@SmmsActivity,
                    it.error.localizedMessage,
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

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

    override fun onTimeSet(p0: TimePicker?, hour: Int, minute: Int) {
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)
        newPostViewModel.postPendingDate.value = calendar.time
        startAlarm(calendar)
    }

    private fun showTimerPickerFragment() {
        val timePickerFragment = TimePickerFragment()
        timePickerFragment.show(supportFragmentManager, "time_picker")
    }

    override fun onDateSet(p0: DatePicker?, year: Int, month: Int, day: Int) {
        newPostViewModel.postPendingDate.value = Date()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DATE, day)
        showTimerPickerFragment()
    }

    private fun startAlarm(time: Calendar) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0)
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        newPostViewModel.writePostPending(calendar.time)
    }

}
