package com.bahdja.homepilot

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ScheduleReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        println("ScheduleReceiver déclenché !")

        val type = intent.getStringExtra("type")
        val command = intent.getStringExtra("command")
        val houseId = intent.getIntExtra("houseId", 0)
        val token = intent.getStringExtra("token")

        // Débogage
        println("type=$type command=$command houseId=$houseId token=$token")
        if (type == null) { println("type est null !"); return }
        if (command == null) { println("command est null !"); return }

        val prefs = context.getSharedPreferences("devices_$type", Context.MODE_PRIVATE)
        val deviceJson = prefs.getString("devices", null)

        // Débogage
        println("deviceJson=$deviceJson")
        if (deviceJson == null) { println("deviceJson est null !"); return }

        val gson = Gson()
        val deviceType = object : TypeToken<List<DeviceData>>() {}.type
        val devices: List<DeviceData> = gson.fromJson(deviceJson, deviceType)

        // débuuug
        println("devices size=${devices.size}")


        devices.forEach { device ->
            println("Commande pour ${device.type}")
            sendCommand(houseId, device.id, command, token)
        }
    }

    private fun sendCommand(houseId: Int, deviceId: String, command: String, token: String?){
        val body = CommandData(command)
        Api().post("https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/devices/$deviceId/command", body, ::sendCommandSuccess, token)
    }

    private fun sendCommandSuccess(responseCode: Int){
        if(responseCode == 200){
            println("Success")
        }else{
            println("Erreur lors de l'envoi de la commande : $responseCode")
        }
    }
}