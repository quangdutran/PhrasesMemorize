package com.dutq.mywordingapp

import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.core.view.get
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val WORD = "Wurst"
    private val MEANING = "Pig"

    @Test
    fun add_and_show_word_and_meaning() {
        val scenario = launch(MainActivity::class.java)
        scenario.moveToState(Lifecycle.State.RESUMED)
        scenario.onActivity { activity ->
            activity.findViewById<TextView>(R.id.wordText).text = WORD
            activity.findViewById<TextView>(R.id.meaningText).text = MEANING
            activity.findViewById<Button>(R.id.saveBtn).performClick()
            activity.findViewById<Button>(R.id.searchBtn).performClick()
            val rs = activity.findViewById<ListView>(R.id.searchResult)
            assertTrue(
                "One matching result",
                rs.count == 3
            )
            val item = rs.getItemAtPosition(0) as Triple<Int, String, String>
            assertTrue(
                "Search result contains the word and meaning",
                WORD == item.second && MEANING == item.third
            )
        }
    }
}