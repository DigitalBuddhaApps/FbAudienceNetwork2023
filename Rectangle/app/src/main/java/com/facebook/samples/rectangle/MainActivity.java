package com.facebook.samples.rectangle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.LinearLayout;

import com.facebook.ads.AdSettings;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.AudienceNetworkAds;

public class MainActivity extends AppCompatActivity {

    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the Audience Network SDK
        AudienceNetworkAds.initialize(this);
        AdSettings.addTestDevice("5e104645-be85-4fc6-849e-83dbedda275d");
        // FAN debug

        // FAN
        adView = new AdView(this, getResources().getString(R.string.fan_rectangle), AdSize.RECTANGLE_HEIGHT_250);
        LinearLayout adContainer = (LinearLayout) findViewById(R.id.banner_container);
        adContainer.addView(adView);
        adView.loadAd();
        // FAN
    }

    @Override
    protected void onDestroy() {
        // FAN
        if (adView != null) {
            adView.destroy();
        }
        // FAN
        super.onDestroy();
    }
}