package com.dutq.mywordingapp

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.TextUnit
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.ImageProvider
import androidx.glance.action.clickable
import androidx.glance.appwidget.CircularProgressIndicator
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.dutq.mywordingapp.db.WordDBHelper
import com.dutq.mywordingapp.gemini.Gemini
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.util.Locale
import kotlin.coroutines.CoroutineContext


class WordWidget : GlanceAppWidget(), CoroutineScope {
    val NA = "N/A"
    var dbHelper: WordDBHelper? = null
    var tts: TextToSpeech? = null
    val gemini = Gemini()
    var sentenceToRead: String = NA
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        dbHelper = WordDBHelper.getInstance(context)
        val keyValuePair = dbHelper?.randomPhrase

        CoroutineScope(Dispatchers.Default).launch {
            keyValuePair?.first?.let { getSentence(it) }
        }

        initializeTextToSpeech(context)
        provideContent {
            val (initialPhrase, setText) = remember { mutableStateOf(keyValuePair) }
            Content(initialPhrase,
                { newPhrase -> setText(newPhrase) },
                context)
       }
    }

    override suspend fun onDelete(context: Context, glanceId: GlanceId) {
        tts?.stop()
        super.onDelete(context, glanceId)
    }
    fun getPhrase(context: Context): Pair<String, String>? {
        sentenceToRead = NA
        dbHelper = WordDBHelper.getInstance(context)
        return dbHelper?.randomPhrase
    }
    suspend fun getSentence(phrase: String) {
        try {
            sentenceToRead = gemini.sendPrompt(phrase) ?: NA
        } catch (exception: Exception) {
            Log.e("GeminiError",
                buildString {
                    append("Gemini API Error \n")
                    append(exception.message)
                }, exception)
        }
    }
    private fun initializeTextToSpeech(context: Context) {
        tts = TextToSpeech(context) { status ->
            if (status != TextToSpeech.ERROR) {
                tts?.language = Locale.GERMAN
            } else {
                Toast.makeText(context,"Text to speech error", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @Composable
    fun Content(
        phrase: Pair<String, String>?,
        changePhrase: (Pair<String, String>?) -> Unit,
        context: Context
    ) {
        val fontSize: TextUnit = 15.sp
        var loading by remember { mutableStateOf(false) }
        val scope = rememberCoroutineScope()
        Column(
            modifier = GlanceModifier
                .fillMaxSize().padding(4.dp)
                .background(White),
            verticalAlignment = Alignment.Vertical.CenterVertically,
            horizontalAlignment = Alignment.Horizontal.Start,
        ) {
            Row(
                horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
                modifier = GlanceModifier.fillMaxWidth().clickable { changePhrase(getPhrase(context)) }
            ) {
                Image(provider = ImageProvider(R.drawable.arrow_right),
                    contentDescription = "next")
            }
            Spacer(modifier = GlanceModifier.height(5.dp))

            Row(
                verticalAlignment = Alignment.Vertical.CenterVertically,
                horizontalAlignment = Alignment.Horizontal.Start,
                modifier = GlanceModifier.fillMaxWidth().clickable {
                    if (phrase?.first != NA) {
                        loading = true
                        scope.launch {
                            phrase?.first?.let { getSentence(it) }
                            var retry = 0
                            while (sentenceToRead == NA && retry < 3) {
                                runBlocking {
                                    delay(1000)
                                }
                                retry++
                            }
                            tts?.speak(
                                if(sentenceToRead == NA) "keinen Satz bilden" else sentenceToRead,
                                TextToSpeech.QUEUE_FLUSH,
                                null,
                                "sentenceWidget")
                            loading = false
                        }
                    }
                }
            ) {
                if (loading) {
                    CircularProgressIndicator(
                        modifier = GlanceModifier.width(12.dp).height(12.dp)
                    )
                } else {
                    Image(provider = ImageProvider(R.drawable.flag_germany),
                        contentDescription = "german")
                }
                Spacer(modifier = GlanceModifier.width(5.dp))
                Text(text = phrase?.first ?: NA,
                    style = TextStyle(
                        color = ColorProvider(Black),
                        fontSize = fontSize))
            }
            Spacer(modifier = GlanceModifier.height(5.dp))
            Row(
                verticalAlignment = Alignment.Vertical.CenterVertically,
                horizontalAlignment = Alignment.Horizontal.Start
            ) {
                Image(
                    provider = ImageProvider(R.drawable.flag_america), contentDescription = "usa")
                Spacer(modifier = GlanceModifier.width(5.dp))
                Text(
                    text = phrase?.second ?: NA,
                    style = TextStyle(
                        color = ColorProvider(Black),
                        fontSize = fontSize,
                        textAlign = TextAlign.Start
                ))
            }
        }
    }

}
