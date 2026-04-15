package com.bahdja.homepilot

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.content.Context
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Spinner
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ProgrammesActivity : AppCompatActivity() {

    private lateinit var spinnerType : Spinner
    private lateinit var schedulesListView: ListView
    private val gson = Gson()
    private val typesMap = mapOf(
        "Volets" to "rolling shutter",
        "Lumières" to "light",
        "Garage" to "garage door"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_programmes)

        setToolBar()

        spinnerType = findViewById<Spinner>(R.id.spinnerType)
        schedulesListView = findViewById<ListView>(R.id.lstProgrammes)

        setSpinner()

    }

    private fun setToolBar(){
        var toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setSpinner(){
        val labels = typesMap.keys.toList()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, labels)
        spinnerType.adapter = adapter

        spinnerType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedLabel = labels[position]
                val selectedType = typesMap[selectedLabel] ?: return
                getProgrammes(selectedType)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

    }
    private fun getProgrammes(type: String){
        val prefs = getSharedPreferences("schedules", Context.MODE_PRIVATE)
        val json = prefs.getString("${type}_list", null)

        val programmes: MutableList<ScheduleData> = if (json != null) {
            gson.fromJson(json, object : TypeToken<MutableList<ScheduleData>>() {}.type)
        } else {
            mutableListOf()
        }

        val adapter = ProgrammeAdapter(this, programmes) { programme ->
            deleteProgramme(type, programme)
        }

        schedulesListView.adapter = adapter
    }

    private fun deleteProgramme(type: String, programmeToDelete: ScheduleData) {
        val prefs = getSharedPreferences("schedules", Context.MODE_PRIVATE)
        val json = prefs.getString("${type}_list", null)

        val programmes: MutableList<ScheduleData> = if (json != null) {
            gson.fromJson(json, object : TypeToken<MutableList<ScheduleData>>() {}.type)
        } else {
            mutableListOf()
        }

        programmes.removeAll {
            it.heure == programmeToDelete.heure &&
                    it.minute == programmeToDelete.minute &&
                    it.command == programmeToDelete.command &&
                    it.type == programmeToDelete.type
        }

        prefs.edit().putString("${type}_list", gson.toJson(programmes)).apply()
        getProgrammes(type)
    }
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }


}