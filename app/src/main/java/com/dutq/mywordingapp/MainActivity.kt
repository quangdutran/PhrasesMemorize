package com.dutq.mywordingapp

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemLongClickListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.dutq.mywordingapp.DialogBuilder.Companion.deleteConfirmation
import com.dutq.mywordingapp.databinding.ActivityMainBinding
import com.dutq.mywordingapp.db.WordDBHelper

class MainActivity : AppCompatActivity() {
    private var dbHelper: WordDBHelper = WordDBHelper.getInstance(this@MainActivity)
    private lateinit var mainBinding: ActivityMainBinding
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        val view = mainBinding.root
        setContentView(view)
        mainBinding.saveBtn.setOnClickListener { view: View -> saveWord(view) }
        mainBinding.searchBtn.setOnClickListener { view: View -> searchWord(view) }
    }

    fun saveWord(view: View) {
        dbHelper.addWord(mainBinding.wordText.text.toString(), mainBinding.meaningText.text.toString())
    }

    fun searchWord(view: View) {
        val result = dbHelper.searchForWord(
            mainBinding.wordText.text.toString()
        )
        val wordItemAdapter: ArrayAdapter<*> = WordItemViewAdapter(applicationContext, result)
        mainBinding.searchResult.adapter = wordItemAdapter
        mainBinding.searchResult.onItemLongClickListener =
            OnItemLongClickListener { _: AdapterView<*>?, view1: View, i: Int, l: Long ->
                try {
                    val deleteItem = Procedure {
                        dbHelper.deleteWord(view1.tag as Int)
                        result.removeAt(i)
                        wordItemAdapter.notifyDataSetChanged()
                    }
                    deleteConfirmation(this@MainActivity, deleteItem)
                    return@OnItemLongClickListener true
                } catch (e: Exception) {
                    Toast.makeText(
                        this@MainActivity,
                        "Could not delete item in db",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@OnItemLongClickListener false
                }
            }
    }
}