package com.example.ladm_u3_practica1

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class BaseDatos(context: Context?,
                name: String?,
                factory: SQLiteDatabase.CursorFactory?,
                version: Int)
    : SQLiteOpenHelper(context, name, factory, version) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE actividades (id_actividad INTEGER PRIMARY KEY, descripcion VARCHAR(2000), fechaCaptura TEXT DEFAULT CURRENT_DATE, fechaEntrega TEXT);")
        db?.execSQL("CREATE TABLE evidencias (idEvidencia INTEGER PRIMARY KEY, id_actividad INTEGER, foto BLOB, FOREIGN KEY (id_actividad) REFERENCES actividades(id_actividad));")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

}