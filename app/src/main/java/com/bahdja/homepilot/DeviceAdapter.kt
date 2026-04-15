package com.bahdja.homepilot

import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Button

class DeviceAdapter(
    private val devices : ArrayList<DeviceData>,
    private val onCommandClick: (DeviceData, String) -> Unit
) : RecyclerView.Adapter<DeviceAdapter.DeviceHolder>()
{

    class DeviceHolder(view: View) : RecyclerView.ViewHolder(view){
        val deviceId: TextView
        val commands: LinearLayout
        init {
            deviceId = view.findViewById(R.id.tvDeviceId)
            commands = view.findViewById(R.id.commandsContainer)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DeviceHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_device, parent, false)
        return DeviceHolder(view)
    }

    override fun onBindViewHolder(
        holder: DeviceHolder,
        position: Int
    ) {
        val device = devices[position]
        holder.deviceId.text = device.id
        holder.commands.removeAllViews()

        device.availableCommands.forEach { command ->
            val button = Button(holder.itemView.context)
            button.text = command
            button.setOnClickListener { onCommandClick(device, command) }
            holder.commands.addView(button)
        }
    }

    override fun getItemCount(): Int {
       return devices.size
    }

}