package com.dutq.mywordingapp

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.ImageProvider
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.text.Text
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
            Content(keyValuePair?.first?: NA, keyValuePair?.second?: NA)
        }
    }

    @Composable
    fun Content(
        phrase: String,
        meaning: String
    ) {
        Column(
            modifier = GlanceModifier
                .fillMaxSize().padding(4.dp)
                .background(White),
            verticalAlignment = Alignment.Vertical.CenterVertically,
            horizontalAlignment = Alignment.Horizontal.Start
        ) {
            Row(
                horizontalAlignment = Alignment.Horizontal.Start
            ) {
                Image(provider = ImageProvider(R.drawable.flag_germany), contentDescription = "german")
                Text(text = phrase, style = TextStyle(
                    color = ColorProvider(Black),
                    fontSize = 20.sp
                ))
            }
            Row(
                horizontalAlignment = Alignment.Horizontal.Start
            ) {
                Image(provider = ImageProvider(R.drawable.flag_america), contentDescription = "usa")
                Text(text = meaning, style = TextStyle(
                    color = ColorProvider(Black),
                    fontSize = 20.sp
                ))
            }
        }
    }
}
