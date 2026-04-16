package com.bahdja.homepilot

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlin.math.min

class ScheduleReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        println("ScheduleReceiver déclenché !")

        val type = intent.getStringExtra("type")
        val command = intent.getStringExtra("command")
        val houseId = intent.getIntExtra("houseId", 0)
        val token = intent.getStringExtra("token")
        val hour = intent.getIntExtra("hour", -1)
        val minute = intent.getIntExtra("minute", -1)

        // Débogage
        println("type=$type command=$command houseId=$houseId token=$token")
        if (type == null) { println("type est null !"); return }
        if (command == null) { println("command est null !"); return }

        val prefs = context.getSharedPreferences("devices_$type", Context.MODE_PRIVATE)
        val deviceJson = prefs.getString("devices", null)

        // Débogage
        println("deviceJson=$deviceJson")
        if (deviceJson == null) { println("deviceJson est null !"); return }

        if (hour == -1 || minute == -1) {
            println("heure ou minute invalide !")
            return
        }

        val gson = Gson()
        val deviceType = object : TypeToken<List<DeviceData>>() {}.type
        val devices: List<DeviceData> = gson.fromJson(deviceJson, deviceType)

        // débuuug
        println("devices size=${devices.size}")


        devices.forEach { device ->
            println("Commande pour ${device.type}")
            sendCommand(houseId, device.id, command, token)
        }

        // reprogrammer pour demain
        reporogrammerSchedule(context, type, command, houseId, token, hour, minute)
    }

    private fun reporogrammerSchedule(context: Context, type: String, command: String, houseId: Int, token: String?, hour: Int, minute: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                println("Permission exact alarm non accordée, reprogrammation impossible")
                return
            }
        }

        println("Alarme programmée pour $hour:$minute")

        val newIntent = Intent(context, ScheduleReceiver::class.java).apply {
            putExtra("type", type)
            putExtra("command", command)
            putExtra("houseId", houseId)
            putExtra("token", token)
            putExtra("hour", hour)
            putExtra("minute", minute)
        }

        val requestCode = "${type}_${hour}_${minute}_$command".hashCode()

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            newIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 1)
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )

        println("Prochaine alarme reprogrammée pour ${calendar.time}")
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