package com.mayorgaerick.cazarpatos

import android.app.Activity
import android.os.Environment
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class FileExternalManager(val actividad: Activity): FileHandler {
    private fun isExternalStorageWritable(): Boolean {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }
    override fun SaveInformation(datosAGrabar:Pair<String,String>) {
        if (isExternalStorageWritable()) {
            FileOutputStream(
                File(
                    actividad.getExternalFilesDir(null),
                    SHAREDINFO_FILENAME
                )
            ).bufferedWriter().use { outputStream ->
                outputStream.write("dato1")
                outputStream.write(System.lineSeparator())
                outputStream.write("dato2")
            }
        }
    }
    private fun isExternalStorageReadable(): Boolean {
        return Environment.getExternalStorageState() in
                setOf(Environment.MEDIA_MOUNTED, Environment.MEDIA_MOUNTED_READ_ONLY)
    }
    override fun ReadInformation(): Pair<String, String> {
        try {
            if (isExternalStorageReadable()) {
                FileInputStream(
                    File(
                        actividad.getExternalFilesDir(null),
                        SHAREDINFO_FILENAME
                    )
                ).bufferedReader().use {
                    val datoLeido = it.readText()
                    val textArray = datoLeido.split(System.lineSeparator())
                    return (textArray[0] to textArray[1])
                }
            }
            return "" to ""
        }catch (e:Exception){
            return "" to ""
        }
    }
}
