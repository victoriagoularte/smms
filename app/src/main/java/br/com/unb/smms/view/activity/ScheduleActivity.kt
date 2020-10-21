package br.com.unb.smms.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import br.com.unb.smms.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ScheduleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)
    }



}