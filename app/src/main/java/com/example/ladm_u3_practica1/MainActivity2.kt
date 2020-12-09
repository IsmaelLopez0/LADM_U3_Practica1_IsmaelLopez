package com.example.ladm_u3_practica1

import android.app.Activity
import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Intent
import android.database.sqlite.SQLiteException
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main2.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity2 : AppCompatActivity() {
    private var baseDatos = BaseDatos(this, "tareas", null, 1)
    private var cal = Calendar.getInstance()
    private var cal2 = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        regresar.setOnClickListener {
            finish()
        }

        val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            val myFormat = "yyyy-MM-dd" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            fechaEntrega.setText(sdf.format(cal.time))
        }

        fechaEntrega.setOnClickListener {
            DatePickerDialog(this, dateSetListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
                .show()
        }

        registrarAdctividad.setOnClickListener {
            registrar()
        }
    }

    private fun registrar() {
        try {
            var trans = baseDatos.writableDatabase
            var values = ContentValues()
            values.put("descripcion", descripcion.text.toString())
            values.put("fechaCaptura", "${cal2.get(Calendar.YEAR)}-${cal2.get(Calendar.MONTH)}-${cal2.get(Calendar.DAY_OF_MONTH)}")
            values.put("fechaEntrega", fechaEntrega.text.toString())
            var res = trans.insert("actividades", null, values)
            mensaje("res -> $res")
            if (res >= -1L){
                mensaje("Se insertó correctamente")
                finish()
            } else { mensaje("Error, no se pudo insertar") }
            trans.close()
        } catch (e: SQLiteException) { mensaje(e.message) }
    }

    private fun mensaje(s: String?) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show()
    }
}