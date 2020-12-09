package com.example.ladm_u3_practica1

import android.content.Intent
import android.database.sqlite.SQLiteException
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main3.*

class MainActivity3 : AppCompatActivity() {
    var baseDatos = BaseDatos(this, "tareas", null, 1)
    var datos = ArrayList<String>()
    var listaID = ArrayList<String>()
    var opcion = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)

        radioButton.setOnClickListener { opcion=1; txtBusqueda.hint = "Descripción" }
        radioButton2.setOnClickListener { opcion=2; txtBusqueda.hint = "Fecha entrega" }
        radioButton3.setOnClickListener { opcion=3; txtBusqueda.hint = "Fecha captura" }

        btnBuscar.setOnClickListener {
            try {
                var trans = baseDatos.readableDatabase
                datos.clear()
                listaID.clear()
                when (opcion) {
                    1 -> {
                        var res = trans.query("actividades", arrayOf("*"), "descripcion=?", arrayOf(txtBusqueda.text.toString()), null, null, null)
                        if (res.moveToFirst()){
                            do {
                                var cad = "Descripción: ${res.getString(1)}\n" +
                                        "Fecha Captura: ${res.getString(2)}\n" +
                                        "Fecha Entrega: ${res.getString(3)}"
                                datos.add(cad)
                                listaID.add("${res.getInt(0)}")
                            } while (res.moveToNext())
                        } else { datos.add("No se encontró resultados") }
                    }
                    2 -> {
                        var res = trans.query("actividades", arrayOf("*"), "fechaEntrega=?", arrayOf(txtBusqueda.text.toString()), null, null, null)
                        if (res.moveToFirst()){
                            do {
                                var cad = "Descripción: ${res.getString(1)}\n" +
                                        "Fecha Captura: ${res.getString(2)}\n" +
                                        "Fecha Entrega: ${res.getString(3)}"
                                datos.add(cad)
                                listaID.add("${res.getInt(0)}")
                            } while (res.moveToNext())
                        } else { datos.add("No se encontró resultados") }
                    }
                    3 -> {
                        var res = trans.query("actividades", arrayOf("*"), "fechaCaptura=?", arrayOf(txtBusqueda.text.toString()), null, null, null)
                        if (res.moveToFirst()){
                            do {
                                var cad = "Descripción: ${res.getString(1)}\n" +
                                        "Fecha Captura: ${res.getString(2)}\n" +
                                        "Fecha Entrega: ${res.getString(3)}"
                                datos.add(cad)
                                listaID.add("${res.getInt(0)}")
                            } while (res.moveToNext())
                        } else { datos.add("No se encontró resultados") }
                    }
                }
                trans.close()
                resultadosBusqueda.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, datos)
                resultadosBusqueda.setOnItemClickListener { _, _, position, _ ->
                    AlertDialog.Builder(this)
                        .setTitle("ATENCIÓN")
                        .setMessage("¿Que desea hacer con este elemento?")
                        .setPositiveButton("Ver a detalle"){ _, _ ->
                            var intent = Intent(this, MainActivity4::class.java)
                            intent.putExtra("id", listaID[position])
                            startActivity(intent)
                        }
                        .setNegativeButton("Eliminar"){ _, _ -> eliminar(listaID[position]) }
                        .setNeutralButton("Nada"){ d, _ -> d.dismiss() }
                        .show()
                }
            } catch (e: SQLiteException){ mensaje(e.message) }
        }
    }

    private fun eliminar(idEliminar: String) {
        AlertDialog.Builder(this)
            .setTitle("ATENCIÓN")
            .setMessage("¿Esta seguro que desea eliminar este elemento?\nEsto eliminará también sus evidencias")
            .setPositiveButton("Sí"){ _, _ ->
                try {
                    var trans = baseDatos.writableDatabase
                    var res = trans.delete("evidencias", "id_actividad=?", arrayOf(idEliminar))
                    var res2 = trans.delete("actividades", "id_actividad=?", arrayOf(idEliminar))
                    if (res2 == 0) mensaje("Error, no se pudo eliminar")
                    else mensaje("Borrado correctamente")
                    trans.close()
                } catch (e: SQLiteException) {mensaje(e.message)}
            }
            .setNegativeButton("Cancelar"){ d, _ -> d.dismiss() }
            .show()
    }

    private fun mensaje(s: String?) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show()
    }
}