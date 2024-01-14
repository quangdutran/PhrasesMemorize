package com.dutq.mywordingapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.dutq.mywordingapp.db.WordDBHelper;

import java.util.List;

import kotlin.Triple;

public class MainActivity extends AppCompatActivity {
    Button saveButton, searchButton;
    ListView searchResult;
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

        saveButton = findViewById(R.id.saveBtn);
        searchButton = findViewById(R.id.searchBtn);
        searchResult = findViewById(R.id.searchResult);
        phrase = findViewById(R.id.wordText);
        meaning = findViewById(R.id.meaningText);

        dbHelper = WordDBHelper.getInstance(MainActivity.this);
        saveButton.setOnClickListener(this::saveWord);
        searchButton.setOnClickListener(this::searchWord);
    }
    void saveWord(View view) {
        dbHelper.addWord(phrase.getText().toString(), meaning.getText().toString());
    }

    void searchWord(View view) {
        List<Triple<Integer, String, String>> result = dbHelper.searchForWord(phrase.getText().toString());
        searchResult.setAdapter(new WordItemViewAdapter(getApplicationContext(), result));
    }
}