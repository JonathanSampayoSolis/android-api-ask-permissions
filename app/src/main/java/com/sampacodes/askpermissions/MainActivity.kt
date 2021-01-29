package com.sampacodes.askpermissions

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sampacodes.askpermissions.ui.MainFragment

class MainActivity : AppCompatActivity() {

    private lateinit var mainFragment: MainFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // attach fragment if is not init for avoid fragment duplication
        if (!::mainFragment.isInitialized) {
            mainFragment = MainFragment.newInstance()

            supportFragmentManager.beginTransaction()
                .add(R.id.layout, mainFragment)
                .commit()
        }
    }

}