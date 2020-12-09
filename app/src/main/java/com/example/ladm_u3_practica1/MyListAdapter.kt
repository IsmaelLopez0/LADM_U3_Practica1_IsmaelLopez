package com.example.ladm_u3_practica1

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Bitmap
import android.view.View
import android.view.ViewGroup
import android.widget.*

class MyListAdapter(private val context: Activity,
                    private val title: ArrayList<String>,
                    private val imgid: ArrayList<Bitmap>)
    : ArrayAdapter<String>(context, R.layout.viewcontentcayout, title) {

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val rowView = inflater.inflate(R.layout.viewcontentcayout, null, true)

        //val titleText = rowView.findViewById(R.id.title) as TextView
        val imageView = rowView.findViewById(R.id.icon) as ImageView

        //titleText.text = ""//title[position]
        imageView.setImageBitmap(imgid[position])

        return rowView
    }
}