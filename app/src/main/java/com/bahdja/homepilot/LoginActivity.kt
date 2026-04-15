package com.bahdja.homepilot

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.EditText

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)
    }

    public fun createNewAccount(view: View){
        val intent = Intent(this, RegisterActivity::class.java);
        startActivity(intent);
    }

    private fun loginSuccess(responseCode: Int, token : Token?){
        if(responseCode == 200 && token?.token != null){
            runOnUiThread {
                val intent = Intent(this, HousesActivity::class.java)
                intent.putExtra("token", token.token)
                startActivity(intent)
                finish()
            }
        }else{
            print("Error: $responseCode")
        }
    }


    public fun login(view: View){
        val login = findViewById<EditText>(R.id.loginTxt).text.toString()
        val pwd = findViewById<EditText>(R.id.loginPwdTxt).text.toString()

        val data = LoginData(login, pwd)
        Api().post<LoginData, Token>("https://polyhome.lesmoulinsdudev.com/api/users/auth",data, ::loginSuccess, null)
    }
}