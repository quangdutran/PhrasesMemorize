package com.dutq.mywordingapp;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.dutq.mywordingapp.db.WordDBHelper;

import kotlin.Pair;

/**
 * Implementation of App Widget functionality.
 */
public class WordingWidget extends AppWidgetProvider {
    WordDBHelper dbHelper;
    private static String UPDATE = "UPDATE";

    private void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.wording_widget);
        updateWords(context, views);
        views.setOnClickPendingIntent(R.id.widgetBox, getPendingSelfIntent(context, UPDATE));
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (UPDATE.equals(intent.getAction())) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName watchWidget = new ComponentName(context, WordingWidget.class);
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.wording_widget);
            updateWords(context, remoteViews);
            appWidgetManager.updateAppWidget(watchWidget, remoteViews);
        }
    }

    private void updateWords(Context context, RemoteViews views) {
        dbHelper = WordDBHelper.getInstance(context);
        Pair<String, String> wordAndMeaning = dbHelper.getRandomPhrase();
        views.setTextViewText(R.id.phrase, wordAndMeaning.getFirst());
        views.setTextViewText(R.id.meaning, wordAndMeaning.getSecond());
    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
    }
}