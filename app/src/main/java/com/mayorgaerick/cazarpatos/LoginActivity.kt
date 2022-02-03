package com.mayorgaerick.cazarpatos

import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.mayorgaerick.cazarpatos.FileManagers.FileHandler
import com.mayorgaerick.cazarpatos.FileManagers.SharedPreferencesManager

class LoginActivity : AppCompatActivity() {
    private lateinit var manejadorArchivo: FileHandler
    private lateinit var checkBoxRecordarme: CheckBox
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword:EditText
    private lateinit var buttonLogin: Button
    private lateinit var buttonNewUser:Button
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        //Inicialización de variables
        editTextEmail = findViewById(R.id.editTextEmail)
        editTextPassword = findViewById(R.id.editTextPassword)
        buttonLogin = findViewById(R.id.buttonLogin)
        buttonNewUser = findViewById(R.id.buttonNewUser)
        checkBoxRecordarme = findViewById(R.id.checkBoxRecordarme)

        // Initialize Firebase Auth
        auth = Firebase.auth


        //File Managers
        manejadorArchivo = SharedPreferencesManager(this)
//        manejadorArchivo = EncryptedSharedPreferences(this)
//        manejadorArchivo = FileExternalManager(this)
//        manejadorArchivo = FileInternalManager(this)


        LeerDatosDePreferencias()

        //Eventos clic
        buttonLogin.setOnClickListener {
            val email = editTextEmail.text.toString()
            val clave = editTextPassword.text.toString()
            //Validaciones de datos requeridos y formatos
            if(!ValidarDatosRequeridos())
                return@setOnClickListener
            //Guardar datos en preferencias.
            GuardarDatosEnPreferencias()

            //Si pasa validación de datos requeridos, ir a pantalla principal
            //val intencion = Intent(this, MainActivity::class.java)
            //intencion.putExtra(EXTRA_LOGIN, email)
            //startActivity(intencion)

            AutenticarUsuario(email, clave)
        }
        buttonNewUser.setOnClickListener{

        }
        mediaPlayer=MediaPlayer.create(this, R.raw.title_screen)
        mediaPlayer.start()
    }

    private fun AutenticarUsuario(email:String, password:String){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(EXTRA_LOGIN, "signInWithEmail:success")
                    //Si pasa validación de datos requeridos, ir a pantalla principal
                    val intencion = Intent(this, MainActivity::class.java)
                    intencion.putExtra(EXTRA_LOGIN, auth.currentUser!!.email)
                    startActivity(intencion)
                    //finish()
                } else {
                    Log.w(EXTRA_LOGIN, "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, task.exception!!.message,
                        Toast.LENGTH_SHORT).show()
                }
            }
    }


    private fun ValidarDatosRequeridos():Boolean{
        val email = editTextEmail.text.toString()
        val clave = editTextPassword.text.toString()
        if (email.isEmpty()) {
            editTextEmail.error = "El email es obligatorio"
            editTextEmail.requestFocus()
            return false
        }
        if (clave.isEmpty()) {
            editTextPassword.error = "La clave es obligatoria"
            editTextPassword.requestFocus()
            return false
        }
        if (clave.length < 3) {
            editTextPassword.error = "La clave debe tener al menos 3 caracteres"
            editTextPassword.requestFocus()
            return false
        }
        return true
    }
    override fun onDestroy() {
        mediaPlayer.release()
        super.onDestroy()
    }

    private fun LeerDatosDePreferencias(){
        val listadoLeido = manejadorArchivo.ReadInformation()
        checkBoxRecordarme.isChecked = true
        editTextEmail.setText ( listadoLeido.first )
        editTextPassword.setText ( listadoLeido.second )
    }

    private fun GuardarDatosEnPreferencias(){
        val email = editTextEmail.text.toString()
        val clave = editTextPassword.text.toString()
        val listadoAGrabar:Pair<String,String>
        if(checkBoxRecordarme.isChecked){
            listadoAGrabar = email to clave
        }
        else{
            listadoAGrabar ="" to ""
        }
        manejadorArchivo.SaveInformation(listadoAGrabar)
    }

}

