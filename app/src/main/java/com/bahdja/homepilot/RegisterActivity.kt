package com.bahdja.homepilot

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.EditText

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.register_activity)
    }

    public fun goToLogin(view: View){
        finish();
    }

    public fun register(view: View) {
        val login = findViewById<EditText>(R.id.registerLoginTxt).text.toString()
        val pwd = findViewById<EditText>(R.id.registerPwdTxt).text.toString()

        val data = RegisterData(login, pwd)
        Api().post<RegisterData>(
            "https://polyhome.lesmoulinsdudev.com/api/users/register",
            data,
            ::registerSuccess
        )

    }
    public fun registerSuccess(responseCode: Int){
        if(responseCode == 200){
            print("OKEYYYYYY")
            finish()
        }else{
            print("PAS OKEEEYYYY")
        }
    }
}