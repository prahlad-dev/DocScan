package info.hannes.liveedgedetection.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

public class SplashScreen extends AppCompatActivity {
    private static final int SPLASH_DISPLAY_TIME = 1000;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable() {
            public void run() {

                Intent intent = new Intent(SplashScreen.this, StartActivity.class);
                startActivity(intent);
                finish();

            }
        }, SPLASH_DISPLAY_TIME);
    }
}