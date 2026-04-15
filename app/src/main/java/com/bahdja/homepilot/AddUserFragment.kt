package com.bahdja.homepilot

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText

/**
 * A simple [Fragment] subclass.
 * Use the [AddUserFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddUserFragment : Fragment() {

    private var houseId: Int? = null
    private var token: String? = null
    var onUserChanged: OnUserChangedListener? = null

    fun setData(houseId: Int?, token: String?){
        this.houseId = houseId
        this.token = token
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.valAddUserBtn).setOnClickListener{
            val loginView  = view.findViewById<EditText>(R.id.addUserLogin)
            val login = loginView.text.toString()
            if(login.isNotEmpty()){
                addUser(login)
                loginView.text.clear()
            }
        }
    }
    private fun addUserSuccess(responseCode: Int){
        if(responseCode == 200){
            activity?.runOnUiThread {
                onUserChanged?.onUserChanged()
                println("OK! Utilisateur ajouté")
            }
        }else{
            println("Erreur lors de l'ajout de l'utilisateur : $responseCode")
        }
    }

    private fun addUser(login: String){
        val body = UserLoginData(login)
        Api().post("https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/users", body, ::addUserSuccess,token)
    }

}