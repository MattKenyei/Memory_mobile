package com.example.memory;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private Switch switchMusic, switchSound;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        switchMusic = findViewById(R.id.switch_music);
        switchSound = findViewById(R.id.switch_sound);

        // Настройки для сохранения состояния
        preferences = getSharedPreferences("AppSettings", MODE_PRIVATE);
        editor = preferences.edit();

        // Загрузка текущих настроек
        switchMusic.setChecked(preferences.getBoolean("MusicEnabled", true));
        switchSound.setChecked(preferences.getBoolean("SoundEnabled", true));

        // Обработчики переключателей
        switchMusic.setOnCheckedChangeListener((buttonView, isChecked) -> {
            editor.putBoolean("MusicEnabled", isChecked);
            editor.apply();
        });

        switchSound.setOnCheckedChangeListener((buttonView, isChecked) -> {
            editor.putBoolean("SoundEnabled", isChecked);
            editor.apply();
        });
    }
}
