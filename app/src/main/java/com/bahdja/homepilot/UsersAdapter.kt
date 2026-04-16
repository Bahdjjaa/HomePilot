package com.bahdja.homepilot

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class UserAdapter(
    context: Context,
    private val users: List<UsersData>
) : ArrayAdapter<UsersData>(context, 0, users) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_users, parent, false)

        val user = users[position]

        val tvLogin = view.findViewById<TextView>(R.id.tvUserLogin)
        val tvRole = view.findViewById<TextView>(R.id.tvUserRole)

        tvLogin.text = user.userLogin
        tvRole.text = if (user.owner == 1) "Propriétaire" else "Invité"

        return view
    }
}