package com.example.ladm_u3_practica1

import android.app.Activity
import android.content.Intent
import android.database.sqlite.SQLiteException
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    var baseDatos = BaseDatos(this, "tareas", null, 1)
    var datos = ArrayList<String>()
    var listaID = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cargarDatos()

        btnAddActividad.setOnClickListener {
            var intent = Intent(this, MainActivity2::class.java)
            startActivity(intent)
        }

        textView.setOnClickListener {
            cargarDatos()
        }

        btnBuscarActividad.setOnClickListener {
            var intent = Intent(this, MainActivity3::class.java)
            startActivity(intent)
        }
    }

    private fun cargarDatos() {
        try {
            var trans = baseDatos.readableDatabase
            var res = trans.query("actividades", arrayOf("*"), null, null, null, null, null)
            datos.clear()
            listaID.clear()
            if (res.moveToFirst()){
                do {
                    var cad = "Descripción: ${res.getString(1)}\n"/* +
                            "Fecha captura: ${res.getString(2)}\n" +
                            "Fecha entrega: ${res.getString(3)}"*/
                    datos.add(cad)
                    listaID.add("${res.getInt(0)}")
                }while (res.moveToNext())
            } else { datos.add("No hay datos registrados") }
            trans.close()
            listaActividades.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, datos)
            if (listaID.size>0){
                listaActividades.setOnItemClickListener { _, _, position, _ ->
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
            }
        } catch (e: SQLiteException){ mensaje(e.message) }
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