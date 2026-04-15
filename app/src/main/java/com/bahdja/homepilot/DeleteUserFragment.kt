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
 * Use the [DeleteUserFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DeleteUserFragment : Fragment() {

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
        return inflater.inflate(R.layout.fragment_delete_user, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.valDelUserBtn).setOnClickListener {
            val loginView = view.findViewById<EditText>(R.id.delUserLogin)
            val login = loginView.text.toString()
            if(login.isNotEmpty()){
                deleteUser(login)
                loginView.text.clear()
            }
        }
    }

    private fun deleteUserSuccess(responseCode: Int){
        if(responseCode == 200){
            activity?.runOnUiThread {
                onUserChanged?.onUserChanged()
                println("OK! Utilisateur supprimé")
            }
        }else{
            println("Erreur lors de la suppression de l'utilisateur : $responseCode")
        }
    }

    private fun deleteUser(login: String){
        val body = UserLoginData(login)
        Api().delete("https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/users", body, ::deleteUserSuccess,token)
    }


}