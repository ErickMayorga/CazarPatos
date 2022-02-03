package com.mayorgaerick.cazarpatos

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mayorgaerick.cazarpatos.database.RankingPlayerDBHelper

class RankingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ranking)

        //OperacionesSqLite()
        //GrabarRankingSQLite()
        //LeerRankingsSQLite()

        consultarPuntajeJugadores()
    }
    private fun OperacionesSqLite(){
        RankingPlayerDBHelper(this).deleteAllRanking()
        RankingPlayerDBHelper(this).insertRankingByQuery(Jugador("Jugador9",10))
        val patosCazados = RankingPlayerDBHelper(this).readDucksHuntedByPlayer("Jugador9")
        RankingPlayerDBHelper(this).updateRanking(Jugador("Jugador9",5))
        RankingPlayerDBHelper(this).deleteRanking("Jugador9")
        RankingPlayerDBHelper(this).insertRanking(Jugador("Jugador9",7))
        val players = RankingPlayerDBHelper(this).readAllRankingByQuery()
    }
    private fun GrabarRankingSQLite(){
        val jugadores = arrayListOf(
            Jugador("Jugador1", 10),
            Jugador("Jugador2", 6),
            Jugador("Jugador3", 3),
            Jugador("Jugador4", 9)
        )
        jugadores.sortByDescending { it.patosCazados }
        for(jugador in jugadores){
            RankingPlayerDBHelper(this).insertRanking(jugador)
        }
    }
    private fun LeerRankingsSQLite(){
        val jugadoresSQLite = RankingPlayerDBHelper(this).readAllRanking()
        val recyclerViewRanking: RecyclerView = findViewById(R.id.recyclerViewRanking)
        recyclerViewRanking.layoutManager = LinearLayoutManager(this)
        recyclerViewRanking.adapter = RankingAdapter(jugadoresSQLite)
        recyclerViewRanking.setHasFixedSize(true)
    }
    fun consultarPuntajeJugadores() {
        val db = Firebase.firestore
        db.collection("ranking")
            .orderBy("patosCazados", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                Log.d(EXTRA_LOGIN, "Success getting documents")
                val jugadores = ArrayList<Jugador>()
                for (document in result) {
                    val jugador = document.toObject(Jugador::class.java)
                    //val jugador = document.toObject<Jugador>()
                    jugadores.add(jugador)
                }
                //Poblar en RecyclerView información usando mi adaptador
                val recyclerViewRanking: RecyclerView = findViewById(R.id.recyclerViewRanking)
                recyclerViewRanking.layoutManager = LinearLayoutManager(this)
                recyclerViewRanking.adapter = RankingAdapter(jugadores)
                recyclerViewRanking.setHasFixedSize(true)
            }
            .addOnFailureListener { exception ->
                Log.w(EXTRA_LOGIN, "Error getting documents.", exception)
                Toast.makeText(this, "Error al obtener datos de jugadores", Toast.LENGTH_LONG)
                    .show()
            }
    }

}

