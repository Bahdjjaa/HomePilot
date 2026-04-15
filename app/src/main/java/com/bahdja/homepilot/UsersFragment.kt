package com.bahdja.homepilot

import android.os.Bundle
import android.service.autofill.UserData
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import java.util.ArrayList

/**
 * A simple [Fragment] subclass.
 * Use the [UsersFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UsersFragment : Fragment() {

    private var token: String? = null
    private var houseId: Int? = null
    private var users = ArrayList<UsersData>()

    fun setData(token: String?, houseId: Int?){
        this.token = token
        this.houseId = houseId
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_users, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getUsers()

        view.findViewById<Button>(R.id.addUserBtn).setOnClickListener {
            setChildFragment(AddUserFragment())
        }

        view.findViewById<Button>(R.id.delUserBtn).setOnClickListener {
            setChildFragment(DeleteUserFragment())
        }
    }

    private fun getUsersSuccess(responseCode: Int, data: List<UsersData>?){
        if(responseCode == 200 && data != null){
            activity?.runOnUiThread {
                users.clear()
                users.addAll(data)
                displayUsers()
            }
        }else{
            println("Erreur de récupération des users : $responseCode")
        }
    }

    private fun getUsers(){
        Api().get("https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/users", ::getUsersSuccess, token)
    }

    private fun displayUsers(){
        val list = view?.findViewById<ListView>(R.id.usersListView)
        val labels = users.map{ "${it.userLogin} - ${if (it.owner == 1) "Propriétaire" else "Invité" }"}
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, labels)
        list?.adapter = adapter
    }

    private fun setChildFragment(fragment: Fragment){
        if(fragment is DeleteUserFragment){
            fragment.setData(houseId, token)
            fragment.onUserChanged = object : OnUserChangedListener {
                override fun onUserChanged() {
                    getUsers()
                }
            }
        }

        if(fragment is AddUserFragment){
            fragment.setData(houseId, token)
            fragment.onUserChanged = object : OnUserChangedListener {
                override fun onUserChanged() {
                    getUsers()
                }
            }
        }


        childFragmentManager.beginTransaction()
            .replace(R.id.flFragment, fragment)
            .commit()
    }

}