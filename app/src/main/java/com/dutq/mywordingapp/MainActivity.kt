package com.dutq.mywordingapp

import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeechService
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.INVISIBLE
import android.widget.AdapterView.OnItemLongClickListener
import android.widget.AdapterView.VISIBLE
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dutq.mywordingapp.DialogBuilder.Companion.deleteConfirmation
import com.dutq.mywordingapp.databinding.ActivityMainBinding
import com.dutq.mywordingapp.db.WordDBHelper
import com.dutq.mywordingapp.gemini.Gemini
import kotlinx.coroutines.launch
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private var dbHelper: WordDBHelper? = null
    private lateinit var mainBinding: ActivityMainBinding
    private var tts: TextToSpeech? = null
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        dbHelper = WordDBHelper.getInstance(this@MainActivity)
        val view = mainBinding.root
        setContentView(view)
        initializeTextToSpeech()
        mainBinding.saveBtn.setOnClickListener { view: View -> saveWord() }
        mainBinding.searchBtn.setOnClickListener { view: View -> searchWord() }
        mainBinding.makeSentence.setOnClickListener { view: View -> makeSentence() }
    }

    override fun onDestroy() {
        super.onDestroy()
        tts?.stop()
    }

    fun saveWord() {
        dbHelper!!.addWord(mainBinding.wordText.text.toString(), mainBinding.meaningText.text.toString())
    }

    fun searchWord() {
        val result = dbHelper!!.searchForWord(
            mainBinding.wordText.text.toString()
        )
        val wordItemAdapter: ArrayAdapter<*> = WordItemViewAdapter(applicationContext, result)
        mainBinding.searchResult.adapter = wordItemAdapter
        mainBinding.searchResult.onItemLongClickListener =
            OnItemLongClickListener { _: AdapterView<*>?, view1: View, i: Int, l: Long ->
                try {
                    val deleteItem = Procedure {
                        dbHelper!!.deleteWord(view1.tag as Int)
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

    private fun makeSentence() {
        showProgressBar(true)
        lifecycleScope.launch {
            val gemini = Gemini()
            try {
                val sentence = gemini.sendPrompt(dbHelper!!.randomPhrase.first)
                sentence?.also {
                    DialogBuilder.showSentence(
                        this@MainActivity,
                        sentence
                    ) {  tts?.speak(sentence, TextToSpeech.QUEUE_FLUSH, null, "sentence") }
                } ?: throw Exception()
            } catch (exception: Exception) {
                DialogBuilder.showSentence(this@MainActivity, "Gemini could not make a sentence for you", null)
            }
            showProgressBar(false)
        }
    }

    private fun showProgressBar(loading: Boolean) {
        if (loading) {
            mainBinding.progressBar.visibility = VISIBLE
            mainBinding.makeSentence.visibility = INVISIBLE
        } else {
            mainBinding.progressBar.visibility = INVISIBLE
            mainBinding.makeSentence.visibility = VISIBLE
        }
    }

    private fun initializeTextToSpeech() {
        tts = TextToSpeech(this@MainActivity) { status ->
            if (status != TextToSpeech.ERROR) {
                tts?.language = Locale.GERMAN
            } else {
                Toast.makeText(this@MainActivity,"Text to speech error", Toast.LENGTH_SHORT).show()
            }
        }
    }
}