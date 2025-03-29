package com.example.music_zsz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.WindowManager;

public class SplashActivity extends AppCompatActivity {

    private Handler handler = new Handler(Looper.getMainLooper());
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkPrivacyStatus();
            }
        },1000);
    }

    private void checkPrivacyStatus() {
        PrivacyManager privacyManager = PrivacyManager.getInstance();
        if (privacyManager.hasAgreed()) {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        } else {
            Intent intent = new Intent(SplashActivity.this, PrivacyActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }
    }

    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}