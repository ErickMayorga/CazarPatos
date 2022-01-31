package com.mayorgaerick.cazarpatos.FileManagers

import android.app.Activity
import android.content.Context
import com.mayorgaerick.cazarpatos.INTERNAL_FILENAME

class FileInternalManager(val actividad: Activity): FileHandler {
    override fun SaveInformation(datosAGrabar: Pair<String, String>) {
        actividad.openFileOutput(INTERNAL_FILENAME, Context.MODE_PRIVATE).bufferedWriter().use { fos ->
            fos.write("${datosAGrabar.first}-${datosAGrabar.second}")
        }
    }

    override fun ReadInformation(): Pair<String, String> {
        actividad.openFileInput(INTERNAL_FILENAME).bufferedReader().use {
            val datoLeido = it.readText()
            val textArray = datoLeido.split("-")
            return (textArray[0] to textArray[1])
        }
    }
}