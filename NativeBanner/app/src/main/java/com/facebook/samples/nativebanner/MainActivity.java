package com.facebook.samples.nativebanner;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.AdSettings;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAdBase;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;
import com.facebook.ads.NativeBannerAd;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NativeAdListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private LinearLayout mAdView;
    private FrameLayout mAdChoicesContainer;
    private NativeAdLayout mNativeBannerAdContainer;
    private @Nullable
    NativeBannerAd mNativeBannerAd;
    private boolean isAdViewAdded;
    private boolean mUseImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the Audience Network SDK
        AudienceNetworkAds.initialize(this);
        AdSettings.addTestDevice("a821c07c-1e68-42a3-b300-e26314615de3");

        // fan native banner
        mNativeBannerAdContainer = findViewById(R.id.native_banner_ad_container);
        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        mAdView = (LinearLayout) inflater.inflate(R.layout.native_banner_ad_unit,
                mNativeBannerAdContainer, false);
        mAdChoicesContainer = mAdView.findViewById(R.id.ad_choices_container);

        mNativeBannerAd = new NativeBannerAd(getApplicationContext(),
                getResources().getString(R.string.fan_native_banner));
        mNativeBannerAd.loadAd(
                mNativeBannerAd
                        .buildLoadAdConfig()
                        .withMediaCacheFlag(NativeAdBase.MediaCacheFlag.ALL)
                        .withAdListener(MainActivity.this)
                        .build());
    }

    @Override
    public void onError(Ad ad, AdError error) {
    }

    @Override
    public void onAdLoaded(Ad ad) {
        if (mNativeBannerAd == null || mNativeBannerAd != ad) {
            // Race condition, load() called again before last ad was displayed
            return;
        }
        if (!isAdViewAdded) {
            isAdViewAdded = true;
            mNativeBannerAdContainer.addView(mAdView);
        }
        // Unregister last ad
        mNativeBannerAd.unregisterView();

        if (!mNativeBannerAd.isAdLoaded() || mNativeBannerAd.isAdInvalidated()) {
            return;
        }

        AdOptionsView adOptionsView =
                new AdOptionsView(
                        this,
                        mNativeBannerAd,
                        mNativeBannerAdContainer,
                        AdOptionsView.Orientation.HORIZONTAL,
                        20);
        mAdChoicesContainer.removeAllViews();
        mAdChoicesContainer.addView(adOptionsView);

        inflateAd(mNativeBannerAd, mAdView);

        mNativeBannerAd.setOnTouchListener(
                new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            int i = view.getId();
                            if (i == R.id.native_ad_call_to_action) {
                                Log.d(TAG, "Call to action button clicked");
                            } else if (i == R.id.native_icon_view) {
                                Log.d(TAG, "Main image clicked");
                            } else {
                                Log.d(TAG, "Other ad component clicked");
                            }
                        }
                        return false;
                    }
                });
    }

    @Override
    public void onAdClicked(Ad ad) {
    }

    @Override
    public void onLoggingImpression(Ad ad) {
        Log.d(TAG, "onLoggingImpression");
    }

    @Override
    public void onMediaDownloaded(Ad ad) {
        Log.d(TAG, "onMediaDownloaded");
    }

    private void inflateAd(NativeBannerAd nativeBannerAd, View adView) {
        // Create native UI using the ad metadata.
        TextView nativeAdTitle = adView.findViewById(R.id.native_ad_title);
        TextView nativeAdSocialContext = adView.findViewById(R.id.native_ad_social_context);
        TextView sponsoredLabel = adView.findViewById(R.id.native_ad_sponsored_label);
        Button nativeAdCallToAction = adView.findViewById(R.id.native_ad_call_to_action);

        // Setting the Text
        nativeAdCallToAction.setText(nativeBannerAd.getAdCallToAction());
        nativeAdCallToAction.setVisibility(
                nativeBannerAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
        nativeAdTitle.setText(nativeBannerAd.getAdvertiserName());
        nativeAdSocialContext.setText(nativeBannerAd.getAdSocialContext());

        // You can use the following to specify the clickable areas.
        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(nativeAdCallToAction);

        MediaView nativeAdIconView = adView.findViewById(R.id.native_icon_view);
        ImageView nativeImageViewAdIconView = adView.findViewById(R.id.image_view_icon_view);
        if (mUseImageView) {
            nativeAdIconView.setVisibility(View.GONE);
            nativeImageViewAdIconView.setVisibility(View.VISIBLE);
            nativeBannerAd.registerViewForInteraction(
                    mNativeBannerAdContainer, nativeImageViewAdIconView, clickableViews);
        } else { // use MediaView
            nativeAdIconView.setVisibility(View.VISIBLE);
            nativeImageViewAdIconView.setVisibility(View.GONE);
            nativeBannerAd.registerViewForInteraction(
                    mNativeBannerAdContainer, nativeAdIconView, clickableViews);
        }
        sponsoredLabel.setText(R.string.sponsored);
    }

    @Override
    public void onDestroy() {
        if (mNativeBannerAd != null) {
            mNativeBannerAd.unregisterView();
            mNativeBannerAd = null;
        }
        super.onDestroy();
    }
}