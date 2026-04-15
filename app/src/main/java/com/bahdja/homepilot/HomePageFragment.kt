package com.bahdja.homepilot

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.gson.Gson

/**
 * A simple [Fragment] subclass.
 * Use the [HomePageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomePageFragment : Fragment() {

    private var token: String? = null
    private var devices: ArrayList<DeviceData> = arrayListOf()
    private var houseId: Int? = null

    fun setData(token: String?, devices: ArrayList<DeviceData>, houseId: Int?){
        this.token = token
        this.devices = devices
        this.houseId = houseId
        saveDevicesToPrefs(devices)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<Button>(R.id.voletsBtn).setOnClickListener{
            navigateToDevices("rolling shutter")
        }

        view.findViewById<Button>(R.id.garageBtn).setOnClickListener {
            navigateToDevices("garage door")
        }

        view.findViewById<Button>(R.id.LimieresBtn).setOnClickListener {
            navigateToDevices("light")
        }

    }
    private fun navigateToDevices(type: String){
        val divFilter = ArrayList(devices.filter { it.type == type })
        val intent = Intent(requireContext(), DeviceCommandsActivity::class.java)
        intent.putExtra("token", token)
        intent.putExtra("type", type)
        intent.putExtra("houseId", houseId)
        intent.putParcelableArrayListExtra("devices", divFilter)
        startActivity(intent)
    }

    private fun saveDevicesToPrefs(devices: ArrayList<DeviceData>){
        val gson = Gson()

        val prefs = requireContext().getSharedPreferences("devices_rolling shutter", Context.MODE_PRIVATE)
        prefs.edit().putString("devices", gson.toJson(devices.filter { it.type == "rolling shutter"})).apply()

        val prefs2 = requireContext().getSharedPreferences("devices_light", Context.MODE_PRIVATE)
        prefs2.edit().putString("devices", gson.toJson(devices.filter { it.type == "light" })).apply()

        val prefs3 = requireContext().getSharedPreferences("devices_garage door", Context.MODE_PRIVATE)
        prefs3.edit().putString("devices", gson.toJson(devices.filter { it.type == "garage door" })).apply()
    }


}