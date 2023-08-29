package com.facebook.samples.rewardvideo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdExperienceType;
import com.facebook.ads.AdSettings;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.CacheFlag;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdExtendedListener;
import com.facebook.ads.RewardData;
import com.facebook.ads.RewardedVideoAd;
import com.facebook.ads.S2SRewardedVideoAdListener;

import java.util.EnumSet;

import static com.facebook.ads.BuildConfig.DEBUG;

public class MainActivity extends AppCompatActivity {

    private TextView rewardedVideoAdStatusLabel;
    private Button loadRewardedVideoButton;
    private Button showRewardedVideoButton;
    private Switch switchEnableRewardedInterstitial;
    private RewardedVideoAd rewardedVideoAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the Audience Network SDK
        AudienceNetworkAds.initialize(this);
        AdSettings.addTestDevice("f5d06ca0-5f04-4ce1-8ef5-e04bad9fa1cd");

        rewardedVideoAdStatusLabel = findViewById(R.id.rewardedVideoAdStatusLabel);
        loadRewardedVideoButton = findViewById(R.id.loadRewardedVideoButton);
        showRewardedVideoButton = findViewById(R.id.showRewardedVideoButton);
        switchEnableRewardedInterstitial =
                findViewById(R.id.switchEnableRewardedInterstitial);

        loadRewardedVideoButton.setOnClickListener(
                v -> {
                    if (rewardedVideoAd != null) {
                        rewardedVideoAd.destroy();
                        rewardedVideoAd = null;
                    }
                    rewardedVideoAd =
                            new RewardedVideoAd(MainActivity.this,
                                    getResources().getString(R.string.fan_rewarded));
                    RewardedVideoAd.RewardedVideoLoadAdConfig loadAdConfig =
                            rewardedVideoAd
                                    .buildLoadAdConfig()
                                    .withFailOnCacheFailureEnabled(true)
                                    .withRewardData(new RewardData("YOUR_USER_ID",
                                            "YOUR_REWARD", 10))
                                    .withAdExperience(
                                            switchEnableRewardedInterstitial.isChecked()
                                                    ?
                                                    AdExperienceType.AD_EXPERIENCE_TYPE_REWARDED_INTERSTITIAL
                                                    :
                                                    AdExperienceType.AD_EXPERIENCE_TYPE_REWARDED)
                                    .build();
                    rewardedVideoAd.loadAd(loadAdConfig);
                    setStatusLabelText("Loading rewarded video ad...");
                });

        showRewardedVideoButton.setOnClickListener(
                v -> {
                    if (rewardedVideoAd == null
                            || !rewardedVideoAd.isAdLoaded()
                            || rewardedVideoAd.isAdInvalidated()) {
                        setStatusLabelText("Ad not loaded. Click load to request an ad.");
                    } else {
                        rewardedVideoAd.show();
                        setStatusLabelText("");
                    }
                });
    }

    private void setStatusLabelText(String label) {
        if (rewardedVideoAdStatusLabel != null) {
            rewardedVideoAdStatusLabel.setText(label);
        }
    }

    @Override
    protected void onDestroy() {
        if (rewardedVideoAd != null) {
            rewardedVideoAd.destroy();
            rewardedVideoAd = null;
        }
        super.onDestroy();
    }
}