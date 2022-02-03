package com.mayorgaerick.cazarpatos

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import android.widget.TextView
import java.util.*
import android.os.CountDownTimer
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var textViewUsuario: TextView
    private lateinit var textViewContador: TextView
    private lateinit var textViewTiempo: TextView
    private lateinit var imageViewPato: ImageView
    private var contador = 0
    private var anchoPantalla = 0
    private var alturaPantalla = 0
    var gameOver = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //Inicialización de variables
        textViewUsuario = findViewById(R.id.textViewUsuario)
        textViewContador = findViewById(R.id.textViewContador)
        textViewTiempo = findViewById(R.id.textViewTiempo)
        imageViewPato = findViewById(R.id.imageViewPato)

        //Obtener el usuario de pantalla login
        val extras = intent.extras ?: return
        var usuario = extras.getString(EXTRA_LOGIN) ?:"Unknown"
        usuario = usuario.substringBefore("@")
        textViewUsuario.text = usuario

        //Determina el ancho y largo de pantalla
        inicializarPantalla()
        //Cuenta regresiva del juego
        inicializarCuentaRegresiva()

        //Evento clic sobre la imagen del pato
        imageViewPato.setOnClickListener {
            if (gameOver) return@setOnClickListener
            contador++
            MediaPlayer.create(this, R.raw.gunshot).start()
            textViewContador.text = contador.toString()
            imageViewPato.setImageResource(R.drawable.duck_clicked)
            //Evento que se ejecuta luego de 500 milisegundos
            Handler().postDelayed({
                imageViewPato.setImageResource(R.drawable.duck)
                moverPato()
            }, 500)

        }
    }

    private fun inicializarPantalla() {
        // 1. Obtenemos el tamaño de la pantalla del dispositivo
        val display = this.resources.displayMetrics
        anchoPantalla = display.widthPixels
        alturaPantalla = display.heightPixels
    }

    private fun moverPato() {
        val min = imageViewPato.width /2
        val maximoX = anchoPantalla - imageViewPato.width
        val maximoY = alturaPantalla - imageViewPato.height
        // Generamos 2 números aleatorios, para la coordenadas x , y
        val randomX = Random().nextInt(maximoX - min + 1)
        val randomY = Random().nextInt(maximoY - min + 1)

        // Utilizamos los números aleatorios para mover el pato a esa nueva posición
        imageViewPato.x = randomX.toFloat()
        imageViewPato.y = randomY.toFloat()
    }

    private fun inicializarCuentaRegresiva() {
        object : CountDownTimer(10000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val segundosRestantes = millisUntilFinished / 1000
                textViewTiempo.setText("${segundosRestantes}s")
            }
            override fun onFinish() {
                textViewTiempo.setText("0s")
                gameOver = true
                mostrarDialogoGameOver()
                val nombreJugador = textViewUsuario.text.toString()
                val patosCazados = textViewContador.text.toString()
                procesarPuntajePatosCazados(nombreJugador, patosCazados.toInt())
            }
        }.start()
    }
    private fun mostrarDialogoGameOver() {
        val builder = AlertDialog.Builder(this)
        builder
            .setMessage("Felicidades!!\nHas conseguido cazar $contador patos")
            .setTitle("Fin del juego")
            .setIcon(R.drawable.duck)
            .setPositiveButton("Reiniciar"
            ) { _, _ ->
                reiniciarJuego()
            }
            .setNegativeButton("Cerrar"
            ) { _, _ ->
                //dialog.dismiss()
                finish()
            }
        builder.create().show()
    }

    private fun reiniciarJuego(){
        contador = 0
        gameOver = false
        textViewContador.text = contador.toString()
        moverPato()
        inicializarCuentaRegresiva()
    }
    private fun jugarOnline(){
        val intentWeb = Intent()
        intentWeb.action = Intent.ACTION_VIEW
        intentWeb.data = Uri.parse("https://duckhuntjs.com/")
        startActivity(intentWeb)
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_nuevo_juego -> {
                reiniciarJuego()
                true
            }
            R.id.action_jugar_online -> {
                jugarOnline()
                true
            }
            R.id.action_ranking -> {
                val intent = Intent(this, RankingActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun procesarPuntajePatosCazados(nombreJugador:String, patosCazados:Int){
        val jugador = Jugador(nombreJugador,patosCazados)
        //Trata de obtener id del documento del ranking específico,
        // si lo obtiene lo actualiza, caso contrario lo crea
        val db = Firebase.firestore
        db.collection("ranking")
            .whereEqualTo("usuario", jugador.usuario)
            .get()
            .addOnSuccessListener { documents ->
                if(documents!= null && documents.documents.count()>0
                ){
                    val idDocumento = documents.documents[0].id
                    actualizarPuntajeJugador(idDocumento, jugador)
                }
                else{
                    ingresarPuntajeJugador(jugador)
                }
            }
            .addOnFailureListener { exception ->
                Log.w(EXTRA_LOGIN, "Error getting documents", exception)
                Toast.makeText(this, "Error al obtener datos de jugador", Toast.LENGTH_LONG).show()
            }
    }
    private fun ingresarPuntajeJugador(jugador:Jugador){
        val db = Firebase.firestore
        db.collection("ranking")
            .add(jugador)
            .addOnSuccessListener {
                Toast.makeText(this,"Puntaje usuario ingresado exitosamente", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener { exception ->
                Log.w(EXTRA_LOGIN, "Error adding document", exception)
                Toast.makeText(this,"Error al ingresar el puntaje", Toast.LENGTH_LONG).show()
            }
    }
    private fun actualizarPuntajeJugador(idDocumento:String, jugador:Jugador){
        val db = Firebase.firestore
        db.collection("ranking")
            .document(idDocumento)
            //.update(contactoHashMap)
            .set(jugador) //otra forma de actualizar
            .addOnSuccessListener {
                Toast.makeText(this,"Puntaje de usuario actualizado exitosamente", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener { exception ->
                Log.w(EXTRA_LOGIN, "Error updating document", exception)
                Toast.makeText(this,"Error al actualizar el puntaje" , Toast.LENGTH_LONG).show()
            }
    }




}

