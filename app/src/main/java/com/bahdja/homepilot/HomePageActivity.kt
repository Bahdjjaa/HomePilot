package com.bahdja.homepilot


import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.fragment.app.Fragment

class HomePageActivity : AppCompatActivity() {
    private var devices = ArrayList<DeviceData>()
    private var token: String? = null
    private var houseId: Int? = null
    private val homePageFragment = HomePageFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home_page)

        val bottomNavigationView : BottomNavigationView = findViewById(R.id.bottomNavigationView)
        setCurrentFragment(homePageFragment)
        token = intent.getStringExtra("token")
        houseId = intent.getIntExtra("houseId",0)

        val usersFragment = UsersFragment()
        usersFragment.setData(token , houseId)

        val settingsFragment = SettingsFragment()
        settingsFragment.setData(token,houseId)

        @Suppress("DEPRECATION")
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.home -> setCurrentFragment(homePageFragment)
                R.id.users -> setCurrentFragment(usersFragment)
                R.id.settings -> setCurrentFragment(settingsFragment)
            }
            true
        }

        getDevices()

    }

    private fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, fragment)
            commit()
        }

    private fun getDevicesSuccess(responseCode: Int, data: DevicesResponse?){
        if(responseCode == 200 && data != null){
            runOnUiThread {
                devices.clear()
                devices.addAll(data.devices)
                homePageFragment.setData(token, devices, houseId)
            }
        }else{
            println("Erreur lors de la récupération : $responseCode")
        }
    }

    private fun getDevices(){
        Api().get("https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/devices", ::getDevicesSuccess, token)
    }
}