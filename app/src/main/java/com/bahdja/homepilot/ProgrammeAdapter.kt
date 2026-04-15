package com.bahdja.homepilot

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog

class ProgrammeAdapter(
    context: Context,
    private val programmes: MutableList<ScheduleData>,
    private val onDelete : (ScheduleData) -> Unit
): ArrayAdapter<ScheduleData>(context, 0, programmes){

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_programme, parent, false)

        val programme = programmes[position]

        val tvProgramme = view.findViewById<TextView>(R.id.tvProgramme)
        val btnDelete = view.findViewById<Button>(R.id.btnDelete)

        val minuteStr = programme.minute.toString().padStart(2, '0')
        tvProgramme.text = "${programme.command} à ${programme.heure}h$minuteStr"

        btnDelete.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("Suppression")
                .setMessage("Supprimer ce programme ?")
                .setPositiveButton("Oui") { _, _ ->
                    onDelete(programme)
                }
                .setNegativeButton("Non", null)
                .show()
        }
        return view
    }
}