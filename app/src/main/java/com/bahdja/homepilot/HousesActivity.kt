package com.bahdja.homepilot

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent


class HousesActivity : AppCompatActivity() {

    private var houses = ArrayList<HouseData>()
    private var token: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_houses)

        // Récupérer le token passé depus Login activity
        token = intent.getStringExtra("token")
        getHouses()
    }

    private fun getHousesSuccess(responseCode: Int, data: List<HouseData>?){
        if(responseCode == 200 && data != null){
            runOnUiThread {
                houses.clear()
                houses.addAll(data);
                displayHouses()
            }
        }else{
            println("Erreur récupération maisons : $responseCode")
        }

    }

    public fun getHouses(){
        Api().get("https://polyhome.lesmoulinsdudev.com/api/houses", ::getHousesSuccess, token)
    }

    private fun displayHouses(){
        val list = findViewById<ListView>(R.id.housesListView)
        val labels = houses.map { "Maison #${it.houseId} - ${if(it.owner) "Propriétaire" else "Invité"}" }
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, labels)
        list.adapter = adapter

        list.setOnItemClickListener { _, _,position, _ ->
            val selectedHouse = houses[position]
            val intent = Intent(this, HomePageActivity::class.java)
            intent.putExtra("houseId", selectedHouse.houseId)
            intent.putExtra("token", token)
            startActivity(intent)
        }
    }


}