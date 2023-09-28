package com.dutq.mywordingapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dutq.mywordingapp.db.WordDBHelper;

public class MainActivity extends AppCompatActivity {
    Button button;
    TextView phrase, meaning;
    WordDBHelper dbHelper;

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
        }

        button = findViewById(R.id.saveBtn);
        phrase = findViewById(R.id.wordText);
        meaning = findViewById(R.id.meaningText);

        dbHelper = WordDBHelper.getInstance(MainActivity.this);
        setAlarm();
        button.setOnClickListener(this::saveWord);
    }
    void setAlarm() {
        Intent intent = new Intent(MainActivity.this, ReminderBroadcast.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        long now = System.currentTimeMillis();
        long tenSeconds = 1000 * 10;
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, now + tenSeconds, 60000, pendingIntent);
    }
    void saveWord(View view) {
        dbHelper.addWord(phrase.getText().toString(), meaning.getText().toString());
    }
}