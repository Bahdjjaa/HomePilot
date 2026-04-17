package com.bahdja.homepilot

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class DeviceCommandsActivity : AppCompatActivity() {
    private var token: String? = null
    private var devices: ArrayList<DeviceData> = arrayListOf()
    private var houseId: Int? = null
    private var type: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_device_commands)
        setToolBar()

        token = intent.getStringExtra("token")
        type = intent.getStringExtra("type")
        houseId = intent.getIntExtra("houseId",0)

        @Suppress("DEPRECATION")
        devices = intent.getParcelableArrayListExtra<DeviceData>("devices") ?: arrayListOf()

        setTitle()
        setGlobalCommands()
        setRecycler()


    }
    private fun setToolBar(){
        var toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
    private fun setTitle()
    {
        findViewById<TextView>(R.id.tvTitle).text = when(type) {
            "rolling shutter" -> "Volets"
            "garage door" -> "Garage"
            "light" -> "Lumières"
            else -> type
        }
    }

    private fun setGlobalCommands(){
        val glbCmds : LinearLayout = findViewById(R.id.globalCommands)
        val commands = when(type){
            "light" -> listOf("TURN ON", "TURN OFF")
            "rolling shutter" -> listOf("OPEN", "CLOSE", "STOP")
            else -> emptyList()
        }

        commands.forEach { command ->
            val button = com.google.android.material.button.MaterialButton(this).apply {
                text = command
                isAllCaps = false
                cornerRadius = 50
                setPadding(32, 12, 32, 12)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    marginEnd = 12
                }
                setOnClickListener {
                    devices.forEach { device ->
                        sendCommand(device.id, command)
                    }
                }
            }
            glbCmds.addView(button)
        }
    }

    private fun setRecycler(){
        val recycler = findViewById<RecyclerView>(R.id.devicesRecyclerView)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = DeviceAdapter(devices) {device, command ->
            sendCommand(device.id, command)
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun sendCommandSuccess(responseCode: Int){
        if(responseCode == 200){
            println("success")
        }else{
            println("Fail")
        }
    }

    private fun sendCommand(deviceId: String, command: String){
        val body = CommandData(command)
        Api().post("https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/devices/$deviceId/command",body,::sendCommandSuccess, token)
    }

}