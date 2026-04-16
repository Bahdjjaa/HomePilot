package com.bahdja.homepilot

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class HouseAdapter(
    context: Context,
    private val houses: List<HouseData>
) : ArrayAdapter<HouseData>(context, 0, houses) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_house, parent, false)

        val house = houses[position]

        val title = view.findViewById<TextView>(R.id.HouseTitle)
        val role = view.findViewById<TextView>(R.id.HouseRole)

        title.text = "Maison #${house.houseId}"
        role.text = if (house.owner) "Propriétaire" else "Invité"

        return view
    }
}