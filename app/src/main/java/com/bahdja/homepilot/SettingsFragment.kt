package com.bahdja.homepilot

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView


/**
 * A simple [Fragment] subclass.
 * Use the [SettingsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingsFragment : Fragment() {

    private var houseId: Int? = null
    private var token: String? = null

    private var type: String? = null

    fun setData(token: String?, houseId: Int?){
        this.token = token
        this.houseId = houseId
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Créer la liste des péréfériques
        val options = listOf("Volets", "Lumières", "Garage")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1,options)
        val listView = view.findViewById<ListView>(R.id.schedulesListView)
        listView.adapter = adapter

        // Associée le type du péréférique en fonction de la sélection
        listView.setOnItemClickListener { _, _, position, _ ->
            this.type = when(position){
                0 -> "rolling shutter"
                1 -> "light"
                2 -> "garage door"
                else -> ""
            }

            println("Type: ${this.type}")
            // Afficher le fragment pour programmer la commande
            setScheduleFragment()
        }

        // aller voir les programmes
        view.findViewById<Button>(R.id.programsBtn).setOnClickListener {
            val intent = Intent(requireContext(), ProgrammesActivity::class.java)
            startActivity(intent)
        }

        // Déconnexion
        view.findViewById<Button>(R.id.logoutBtn).setOnClickListener {
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    // Méthode pour afficher le fragment de programmation de commande
    private fun setScheduleFragment(){
        val fragment = ScheduleFragment()
        fragment.setData(type, token , houseId)
        // Débogage
        println("Type : ${this.type}")
        println("token : ${this.token}")
        println("houseId : ${this.houseId}")
        childFragmentManager.beginTransaction()
            .replace(R.id.flScheduleFragment, fragment)
            .commit()
    }

}