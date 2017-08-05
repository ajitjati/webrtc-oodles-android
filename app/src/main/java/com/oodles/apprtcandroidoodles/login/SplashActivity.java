package com.oodles.apprtcandroidoodles.login;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.oodles.apprtcandroidoodles.R;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_TIME_OUT = 2000;
    private SplashActivity mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_splash);
        mContext = SplashActivity.this;
        startSplashTimer();
    }

    private void startSplashTimer() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                openLoginScreen();
            }

        }, SPLASH_TIME_OUT);
    }

    private void openLoginScreen() {
        Intent loginIntent = new Intent(mContext,LoginActivity.class);
        mContext.startActivity(loginIntent);
        mContext.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
        mContext.finish();
    }


}
