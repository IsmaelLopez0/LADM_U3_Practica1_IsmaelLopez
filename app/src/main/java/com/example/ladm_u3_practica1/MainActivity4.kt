package com.example.ladm_u3_practica1

import android.content.Intent
import android.database.sqlite.SQLiteException
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_main4.*

class MainActivity4 : AppCompatActivity() {
    var baseDatos = BaseDatos(this, "tareas", null, 1)
    var id = ""
    var evidencias = ArrayList<ByteArray>()
    var evidenciasID = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main4)

        var extras = intent.extras!!
        id = extras.getString("id")!!

        cargarData()

        btnRegresar.setOnClickListener { finish() }

        btnBorrarActividad.setOnClickListener { eliminar(id) }

        addEvidencia.setOnClickListener {
            var intent = Intent(this, MainActivity5::class.java)
            intent.putExtra("id", id)
            startActivity(intent)
        }
    }

    private fun cargarData() {
        try {
            var trans = baseDatos.readableDatabase
            var res = trans.query("actividades", arrayOf("*"), "id_actividad=?", arrayOf(id), null, null, null)
            if (res.moveToFirst())
                txtData.setText("Descripción: ${res.getString(1)}\n" +
                        "Fecha Captura: ${res.getString(2)}\n" +
                        "Fecha Entrega: ${res.getString(3)}")
            else txtData.setText("Hubo un error al recuperar la data")

            var res2 = trans.query("evidencias", arrayOf("*"), "id_actividad=?", arrayOf(id), null, null, null)
            if (res2.moveToFirst()){
                evidenciasID.clear()
                evidencias.clear()
                do {
                    evidencias.add(res2.getBlob(2))
                    evidenciasID.add("${res2.getInt(0)}")
                } while (res2.moveToNext())
            } else mensaje("Al parecer no tiene evidencias")

            var vector = ArrayList<Bitmap>()
            (0 until evidencias.size).forEach {
                var bitmap = Utils.getImage(evidencias[it])
                vector.add((bitmap))
            }

            val myListAdapter = MyListAdapter(this, evidenciasID, vector)
            listaEvidencias.adapter = myListAdapter
            listaEvidencias.setOnItemClickListener { _, _, position, _ ->
                AlertDialog.Builder(this)
                    .setTitle("ATENCIÓN")
                    .setMessage("¿Desea borrar esta evidencia?")
                    .setPositiveButton("Sí"){ _, _ -> eliminarEvidencia(evidenciasID[position])}
                    .setNegativeButton("No") { d, _ -> d.dismiss() }
                    .show()
            }

            trans.close()
        } catch (e:SQLiteException) { mensaje(e.message) }
    }

    private fun eliminarEvidencia(idEvidenciaEliminar: String) {
        try {
            var trans = baseDatos.writableDatabase
            var res = trans.delete("evidencias", "idEvidencia=?", arrayOf(idEvidenciaEliminar))
            if(res == 0) mensaje("No se pudo eliminar evidencia")
            else { mensaje("Evidencia eliminada con exito"); cargarData() }
            trans.close()
        } catch (e: SQLiteException) { mensaje(e.message) }
    }

    private fun eliminar(idEliminar: String) {
        AlertDialog.Builder(this)
            .setTitle("ATENCIÓN")
            .setMessage("¿Esta seguro que desea eliminar esta actividad?\nEsto eliminará también sus evidencias")
            .setPositiveButton("Sí"){ _, _ ->
                try {
                    var trans = baseDatos.writableDatabase
                    var res = trans.delete("evidencias", "id_actividad=?", arrayOf(idEliminar))
                    var res2 = trans.delete("actividades", "id_actividad=?", arrayOf(idEliminar))
                    if (res2 == 0) mensaje("Error, no se pudo eliminar")
                    else mensaje("Borrado correctamente")
                    trans.close()
                    finish()
                } catch (e: SQLiteException) {mensaje(e.message)}
            }
            .setNegativeButton("Cancelar"){ d, _ -> d.dismiss() }
            .show()
    }

    private fun mensaje(s: String?) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show()
    }
}