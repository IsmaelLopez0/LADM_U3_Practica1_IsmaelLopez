package com.example.ladm_u3_practica1

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.database.sqlite.SQLiteException
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main5.*


class MainActivity5 : AppCompatActivity() {
    private var fotoSel = 0
    var baseDatos = BaseDatos(this, "tareas", null, 1)
    var id = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main5)

        var extras = intent.extras!!
        id = extras.getString("id")!!

        selectPicture.setOnClickListener {
            var foto = Intent(Intent.ACTION_PICK)
            foto.type = "image/*"
            startActivityForResult(foto, fotoSel)
        }

        btnRegEvidencia.setOnClickListener {
            try{
                var bitmap = (img.drawable as BitmapDrawable).bitmap
                var trans = baseDatos.writableDatabase
                var values = ContentValues()
                values.put("id_actividad", id)
                values.put("foto", Utils.getBytes(bitmap))
                var res = trans.insert("evidencias", "idEvidencia", values)

                if(res.toInt() == -1){ mensaje("Error, no se pudo guardar la imagen")}
                trans.close()
                finish()
            }catch (e : SQLiteException){ mensaje(e.message) }
        }
    }

    private fun mensaje(s: String?) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == fotoSel && resultCode == Activity.RESULT_OK && data != null){
            var foto = data.data
            img.setImageURI(foto)
            btnRegEvidencia.isEnabled = true
        }
    }
}