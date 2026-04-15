package com.bahdja.homepilot

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Spinner
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TimePicker
import android.widget.Toast
import android.content.Intent
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Calendar

/**
 * A simple [Fragment] subclass.
 * Use the [ScheduleFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ScheduleFragment : Fragment() {

    private var deviceType: String? = null
    private var houseId: Int? = null
    private  var token: String? = null

//    companion object {
//        fun newInstance(type: String, token: String?, houseId: Int?): ScheduleFragment {
//            val framgment = ScheduleFragment()
//            val args = Bundle()
//            args.putString("type", type)
//            args.putString("token", token)
//            args.putInt("houseId", houseId ?: 0)
//            framgment.arguments = args
//            return framgment
//        }
//    }
    fun setData(deviceType: String?, token: String?, houseId: Int?){
        this.deviceType = deviceType
        this.token = token
        this.houseId = houseId
    }

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        type = arguments?.getString("type")
//        token = arguments?.getString("token")
//        houseId = arguments?.getInt("houseId")
//    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_schedule, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<TextView>(R.id.tvScheduleTitle).text = when(deviceType){
            "rolling shutter" -> "Programmer les volets"
            "light"->"Programmer les lumières"
            "garage door" -> "Programmer le garage"
            else -> deviceType
        }

        val commands = when(deviceType){
            "light" -> listOf("TURN ON", "TURN OFF")
            "rolling shutter", "garage door" -> listOf("OPEN", "CLOSE") // Après PEUT ETRE Ajouter après un champ pour spécifier le pourcentage d'ouverture et de fermeture et le gérérer avec la commande STOP
            else -> emptyList()
        }

        val spinner = view.findViewById<Spinner>(R.id.commandSpinner)
        spinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, commands)

        val timePicker = view.findViewById<TimePicker>(R.id.timePicker)
        timePicker.setIs24HourView(true)

        view.findViewById<Button>(R.id.saveScheduleBtn).setOnClickListener {
            val hour = timePicker.hour
            val minute = timePicker.minute
            val command = spinner.selectedItem.toString()
            saveSchedule(hour, minute, command)
        }

        loadSchedule(view)
    }

    private fun saveSchedule(heure: Int, minute: Int, command: String) {
        val prefs = requireContext().getSharedPreferences("schedules", Context.MODE_PRIVATE)
        val gson = Gson()

        val existing = prefs.getString("${deviceType}_list", null)
        val list : MutableList<ScheduleData> = if (existing != null){
            gson.fromJson(existing, object : TypeToken<MutableList<ScheduleData>>() {}.type)
        }else{
            mutableListOf()
        }

        // Ajouter le nouveau programme
        list.add(ScheduleData(heure, minute, command, deviceType ?: "" ))

        // sauvgarder la liste des programmes
        prefs.edit().putString("${deviceType}_list", gson.toJson(list)).apply()

        scheduleAlarm(heure, minute, command)
        Toast.makeText(requireContext(), "Programmation sauvegardée !", Toast.LENGTH_SHORT).show()
    }

    private fun scheduleAlarm(hour: Int, minute: Int, command: String){
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S){
            if(!alarmManager.canScheduleExactAlarms()){
                val intent = Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                startActivity(intent)
                return
            }
        }
        println("Alarme programmée pour $hour:$minute")

        val intent = Intent(requireContext(), ScheduleReceiver::class.java).apply{
            putExtra("type", deviceType)
            println("Type = $deviceType")

            putExtra("command", command)
            println("command = $command")

            putExtra("houseId", houseId)
            println("houseId = $houseId")

            putExtra("token", token)
            println("token = $token")
        }

        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            deviceType.hashCode(), // ID unique par type
            intent,
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // calcule l'heure du prochain déclenchement
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.DAY_OF_YEAR, 1) // si l'heure est passée, programme pour demain
            }
        }

        // se répète tous les jours
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }
    private fun loadSchedule(view: View) {
        val prefs = requireContext().getSharedPreferences("schedules", Context.MODE_PRIVATE)
        val hour = prefs.getInt("${deviceType}_hour", -1)
        val minute = prefs.getInt("${deviceType}_minute", 0)
        val command = prefs.getString("${deviceType}_command", null)

        if (hour != -1) {
            view.findViewById<TimePicker>(R.id.timePicker).hour = hour
            view.findViewById<TimePicker>(R.id.timePicker).minute = minute
            val spinner = view.findViewById<Spinner>(R.id.commandSpinner)
            val commands = when(deviceType) {
                "light" -> listOf("TURN ON", "TURN OFF")
                "rolling shutter", "garage door" -> listOf("OPEN", "CLOSE")
                else -> emptyList()
            }
            spinner.setSelection(commands.indexOf(command))
        }
    }



}