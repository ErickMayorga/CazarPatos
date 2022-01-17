package com.mayorgaerick.cazarpatos

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import android.widget.TextView
import java.util.*
import android.os.CountDownTimer
import androidx.appcompat.app.AlertDialog

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
        val usuario = extras.getString(EXTRA_LOGIN) ?:"Unknown"
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
            }
        }.start()
    }
    private fun mostrarDialogoGameOver() {
        val builder = AlertDialog.Builder(this)
        builder
            .setMessage("Felicidades!!\nHas conseguido cazar $contador patos")
            .setTitle("Fin del juego")
            .setPositiveButton("Reiniciar"
            ) { _, _ ->
                contador = 0
                gameOver = false
                textViewContador.text = contador.toString()
                moverPato()
                inicializarCuentaRegresiva()
            }
            .setNegativeButton("Cerrar"
            ) { _, _ ->
                //dialog.dismiss()
                finish()
            }
        builder.create().show()
    }

}

