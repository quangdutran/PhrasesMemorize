package com.dutq.mywordingapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView


class WordItemViewAdapter(context: Context, items : List<Triple<Int, String, String>>)
    : ArrayAdapter<Triple<Int, String, String>>(context, 0, items) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rowView = inflater.inflate(R.layout.word_item, parent,false)
        val currentItem = getItem(position)
        val phrase = rowView.findViewById<TextView>(R.id.phraseItem)
        val meaning = rowView.findViewById<TextView>(R.id.meaningItem)
        phrase.text = currentItem?.second
        meaning.text = currentItem?.third
        rowView.tag = currentItem?.first
        return rowView
    }
}