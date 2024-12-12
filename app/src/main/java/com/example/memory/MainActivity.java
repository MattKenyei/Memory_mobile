package com.example.memory;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout layout;
    private TextView scoreTextView;
    private int score = 0;
    private ArrayList<ImageView> stars;
    private Random random;
    private ImageView lastClickedStar; // Изменено на lastClickedStar
    private int width, height;
    private boolean isFirstRound = true; // Изменённая переменная
    private MediaPlayer backgroundMusic; // Фоновая музыка
    private MediaPlayer clickSound;
    private Button restartButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        layout = findViewById(R.id.relativeLayout);
        scoreTextView = findViewById(R.id.scoreTextView);

        stars = new ArrayList<>();
        random = new Random();
        backgroundMusic = MediaPlayer.create(this, R.raw.background_music); // Замените на имя вашего файла
        backgroundMusic.setLooping(true); // Зациклить музыку
        backgroundMusic.start();
        restartButton = findViewById(R.id.btn_restart);
        restartButton.setOnClickListener(v -> restartGame());

        Button settingsButton = findViewById(R.id.btn_settings);
        settingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });
        layout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                width = layout.getWidth();
                height = layout.getHeight();

                // Добавляем 5 звездочек на первый запуск
                for (int i = 0; i < 5; i++) {
                    addNewStar();
                }
            }
        });
        clickSound = MediaPlayer.create(this, R.raw.click_sound2);
    }

    protected void onDestroy() {
        super.onDestroy();
        if (backgroundMusic != null) {
            backgroundMusic.stop();
            backgroundMusic.release();
        }
        if (clickSound != null) {
            clickSound.release();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences preferences = getSharedPreferences("AppSettings", MODE_PRIVATE);

        // Управление музыкой
        boolean isMusicEnabled = preferences.getBoolean("MusicEnabled", true);
        if (isMusicEnabled) {
            if (backgroundMusic != null && !backgroundMusic.isPlaying()) {
                backgroundMusic.start();
            }
        } else {
            if (backgroundMusic != null && backgroundMusic.isPlaying()) {
                backgroundMusic.pause();
            }
        }

        // Управление звуками — сохраняйте настройки для их проверки
        boolean isSoundEnabled = ((SharedPreferences) preferences).getBoolean("SoundEnabled", true);
        if (!isSoundEnabled && clickSound != null) {
            clickSound.setVolume(0, 0);
        } else if (clickSound != null) {
            clickSound.setVolume(1, 1);
        }
    }
    private void restartGame() {
        // Очищаем список звёзд
        for (ImageView star : stars) {
            layout.removeView(star);
        }
        stars.clear();

        // Сбрасываем очки
        score = 0;
        scoreTextView.setText("Score: " + score);

        // Обновляем состояние игры
        isFirstRound = true;
        lastClickedStar = null;

        // Добавляем начальные звёзды
        for (int i = 0; i < 5; i++) {
            addNewStar();
        }

        // Сообщение об успешном перезапуске
        Toast.makeText(this, "Игра началась заново!", Toast.LENGTH_SHORT).show();
    }


    private void addNewStar() {
        ImageView star = new ImageView(this);
        star.setImageResource(R.drawable.ic_star); // Убедитесь, что ресурс звезды существует
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(200, 200);
        star.setLayoutParams(params);

        // Устанавливаем случайное положение, проверяя на наложение
        int x, y;
        do {
            x = random.nextInt(width - 200);
            y = random.nextInt(height - 200);
        } while (isOverlapping(x, y));

        params.leftMargin = x;
        params.topMargin = y;
        layout.addView(star);
        star.setOnClickListener(v -> onStarClick(star));

        // Обновляем последнюю нажатую звезду, если это не первый раунд
        if (!isFirstRound) {
            lastClickedStar = star; // Следующая звезда будет той, на которую нажали
        }
        lastClickedStar = star;
        stars.add(star);
        scoreTextView.setText("Score: " + score);
    }

    // Метод для проверки наложения звездочек
    private boolean isOverlapping(int x, int y) {
        for (ImageView star : stars) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) star.getLayoutParams();
            int starX = params.leftMargin;
            int starY = params.topMargin;

            if (Math.abs(starX - x) < 200 && Math.abs(starY - y) < 200) {
                return true; // Наложение
            }
        }
        return false; // Нет наложения
    }

    private void onStarClick(ImageView clickedStar) {
        if (clickSound != null) {
            clickSound.start();
        }
        if (isFirstRound) {
            // Первый ход: клик по любой звезде разрешён
            score++; // Увеличиваем счёт
            addNewStar(); // Добавляем новую звезду
            isFirstRound = false; // Устанавливаем, что первый ход завершён

        } else if (clickedStar == lastClickedStar) {
            // Клик по правильной звезде
            score++;
            addNewStar();
        } else {
            // Клик по неправильной звезде
            Toast.makeText(this, "Game Over! Your score: " + score, Toast.LENGTH_SHORT).show();
            finish(); // Завершаем игру
        }

        // Обновляем текст счёта
        scoreTextView.setText("Score: " + score);
    }

}
