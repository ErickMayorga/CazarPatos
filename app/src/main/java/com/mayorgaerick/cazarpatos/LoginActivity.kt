package com.mayorgaerick.cazarpatos

import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText

class LoginActivity : AppCompatActivity() {
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword:EditText
    private lateinit var buttonLogin: Button
    private lateinit var buttonNewUser:Button
    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        //Inicialización de variables
        editTextEmail = findViewById(R.id.editTextEmail)
        editTextPassword = findViewById(R.id.editTextPassword)
        buttonLogin = findViewById(R.id.buttonLogin)
        buttonNewUser = findViewById(R.id.buttonNewUser)
        //Eventos clic
        buttonLogin.setOnClickListener {
            val email = editTextEmail.text.toString()
            val clave = editTextPassword.text.toString()
            //Validaciones de datos requeridos y formatos
            if(!ValidarDatosRequeridos())
                return@setOnClickListener
            //Si pasa validación de datos requeridos, ir a pantalla principal
            val intencion = Intent(this, MainActivity::class.java)
            intencion.putExtra(EXTRA_LOGIN, email)
            startActivity(intencion)
        }
        buttonNewUser.setOnClickListener{

        }
        mediaPlayer=MediaPlayer.create(this, R.raw.title_screen)
        mediaPlayer.start()
    }

    private fun ValidarDatosRequeridos():Boolean{
        val email = editTextEmail.text.toString()
        val clave = editTextPassword.text.toString()
        if (email.isEmpty()) {
            editTextEmail.setError("El email es obligatorio")
            editTextEmail.requestFocus()
            return false
        }
        if (clave.isEmpty()) {
            editTextPassword.setError("La clave es obligatoria")
            editTextPassword.requestFocus()
            return false
        }
        if (clave.length < 3) {
            editTextPassword.setError("La clave debe tener al menos 3 caracteres")
            editTextPassword.requestFocus()
            return false
        }
        return true
    }
    override fun onDestroy() {
        mediaPlayer.release()
        super.onDestroy()
    }
}

