package com.facebook.samples.nativehscroll;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSettings;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdListener;
import com.facebook.ads.NativeAdScrollView;
import com.facebook.ads.NativeAdView;
import com.facebook.ads.NativeAdViewAttributes;
import com.facebook.ads.NativeAdsManager;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class MainActivity extends AppCompatActivity implements NativeAdsManager.Listener {

    private static final int NATIVE_AD_VIEW_HEIGHT_DP = 300;
    private NativeAdsManager manager;
    private @Nullable
    NativeAdScrollView scrollView;
    private LinearLayout scrollViewContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the Audience Network SDK
        AudienceNetworkAds.initialize(this);
        AdSettings.addTestDevice("a985f446-2434-4cb7-b6c1-27bc85e637e9");

        this.getRequestedOrientation();
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        manager = new NativeAdsManager(this, getResources().getString(R.string.fan_native_hscroll), 5);
        manager.setListener(this);
        manager.loadAds(NativeAd.MediaCacheFlag.ALL);
        scrollViewContainer = findViewById(R.id.hscroll_container);
    }

    @Override
    public void onAdsLoaded() {
        if (scrollView != null) {
            scrollViewContainer.removeView(scrollView);
        }
        scrollView = new NativeAdScrollView(this, manager, NATIVE_AD_VIEW_HEIGHT_DP);
        scrollViewContainer.addView(scrollView);
    }

    @Override
    public void onAdError(AdError error) {
        Toast.makeText(this, "Ad error: " + error.getErrorMessage(),
                Toast.LENGTH_SHORT)
                .show();
    }
}