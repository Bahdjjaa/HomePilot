package com.bahdja.homepilot

import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Button
import com.google.android.material.button.MaterialButton

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
            val materialButton = MaterialButton(holder.itemView.context).apply {
                text = command
                isAllCaps = false
                cornerRadius = 48
                insetTop = 0
                insetBottom = 0
                minHeight = 0
                minimumHeight = 0
                setPadding(28, 10, 28, 10)

                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    marginEnd = 12
                }

                setOnClickListener {
                    onCommandClick(device, command)
                }
            }

            holder.commands.addView(materialButton)
        }
    }

    override fun getItemCount(): Int {
       return devices.size
    }

}