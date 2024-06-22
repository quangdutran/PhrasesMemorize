package com.dutq.mywordingapp

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.graphics.Color.Companion.Yellow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.ImageProvider
import androidx.glance.action.clickable
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

class WordWidget : GlanceAppWidget() {
    val NA = "N/A"
    var dbHelper: WordDBHelper? = null

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        dbHelper = WordDBHelper.getInstance(context)
        val keyValuePair = dbHelper?.randomPhrase
        provideContent {
            val (initialPhrase, setText) = remember { mutableStateOf(keyValuePair) }
            Content(initialPhrase, { newPhrase -> setText(newPhrase) }, context)
        }
    }
    fun getPhrase(context: Context): Pair<String, String>? {
        dbHelper = WordDBHelper.getInstance(context)
        return dbHelper?.randomPhrase
    }

    @Composable
    fun Content(
        phrase: Pair<String, String>?, onButtonClick: (Pair<String, String>?) -> Unit, context: Context
    ) {
        val fontSize: TextUnit = 15.sp
        Column(
            modifier = GlanceModifier
                .fillMaxSize().padding(4.dp)
                .background(White),
            verticalAlignment = Alignment.Vertical.CenterVertically,
            horizontalAlignment = Alignment.Horizontal.Start,
        ) {
            Row(
                horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
                modifier = GlanceModifier.fillMaxWidth().clickable { onButtonClick(getPhrase(context)) }
            ) {
                Image(provider = ImageProvider(R.drawable.arrow_right),
                    contentDescription = "next")
            }
            Spacer(modifier = GlanceModifier.height(10.dp))

            Row(
                verticalAlignment = Alignment.Vertical.CenterVertically,
                horizontalAlignment = Alignment.Horizontal.Start
            ) {
                Image(provider = ImageProvider(R.drawable.flag_germany), contentDescription = "german")
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
