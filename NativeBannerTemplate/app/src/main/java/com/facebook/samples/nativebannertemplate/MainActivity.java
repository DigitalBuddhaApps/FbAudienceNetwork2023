package com.facebook.samples.nativebannertemplate;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSettings;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdListener;
import com.facebook.ads.NativeAdView;
import com.facebook.ads.NativeAdViewAttributes;
import com.facebook.ads.NativeBannerAd;
import com.facebook.ads.NativeBannerAdView;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class MainActivity extends AppCompatActivity implements NativeAdListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int COLOR_LIGHT_GRAY = 0xff90949c;
    private static final int COLOR_DARK_GRAY = 0xff4e5665;
    private static final int COLOR_CTA_BLUE_BG = 0xff4080ff;
    private @Nullable
    NativeBannerAd mNativeBannerAd;
    private NativeBannerAdView.Type mViewType = NativeBannerAdView.Type.HEIGHT_100;
    private int mAdBackgroundColor, mTitleColor, mLinkColor, mContentColor, mCtaBgColor;
    private ViewGroup mNativeAdContainer;
    private Spinner mBackgroundColorSpinner;
    private Spinner mAdViewTypeSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the Audience Network SDK
        AudienceNetworkAds.initialize(this);
        AdSettings.addTestDevice("322d3e86-ca10-4f76-bbf9-4b182c64fdee");

        mNativeAdContainer = findViewById(R.id.templateContainer);
        mBackgroundColorSpinner = findViewById(R.id.backgroundColorSpinner);
        mAdViewTypeSpinner = findViewById(R.id.adViewTypeSpinner);

        ArrayAdapter<CharSequence> backgroundColorSpinnerAdapter =
                ArrayAdapter.createFromResource(
                        this, R.array.background_color_array,
                        android.R.layout.simple_spinner_item);
        backgroundColorSpinnerAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        mBackgroundColorSpinner.setAdapter(backgroundColorSpinnerAdapter);

        ArrayAdapter<CharSequence> adViewTypeSpinnerAdapter =
                ArrayAdapter.createFromResource(
                        this, R.array.ad_bannerview_type_array,
                        android.R.layout.simple_spinner_item);
        adViewTypeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mAdViewTypeSpinner.setAdapter(adViewTypeSpinnerAdapter);

        setSpinnerListeners();
        createAndLoadNativeAd();
    }

    protected void createAndLoadNativeAd() {
        mNativeBannerAd = new NativeBannerAd(getApplicationContext(), getResources().getString(R.string.fan_banner_template));
        mNativeBannerAd.loadAd(mNativeBannerAd.buildLoadAdConfig().withAdListener(this).build());
    }

    private void reloadAdContainer() {
        Activity activity = this;
        if (mNativeBannerAd != null && mNativeBannerAd.isAdLoaded() && !mNativeBannerAd.isAdInvalidated()) {
            mNativeAdContainer.removeAllViews();

            // Create a NativeAdViewAttributes object and set the attributes
            NativeAdViewAttributes attributes =
                    new NativeAdViewAttributes(activity)
                            .setBackgroundColor(mAdBackgroundColor)
                            .setTitleTextColor(mTitleColor)
                            .setDescriptionTextColor(mContentColor)
                            .setButtonBorderColor(mCtaBgColor)
                            .setButtonTextColor(mLinkColor)
                            .setButtonColor(mCtaBgColor);

            // Use NativeAdView.render to generate the ad View
            View adView = NativeBannerAdView.render(activity, mNativeBannerAd, mViewType,
                    attributes);

            // Add adView to the container showing Ads
            mNativeAdContainer.addView(adView, 0);
            mNativeAdContainer.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    private void setSpinnerListeners() {
        mBackgroundColorSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View view, int position,
                                               long id) {
                        int item = mBackgroundColorSpinner.getSelectedItemPosition();
                        switch (item) {
                            case 0:
                                mAdBackgroundColor = Color.WHITE;
                                mTitleColor = COLOR_DARK_GRAY;
                                mLinkColor = Color.WHITE;
                                mContentColor = COLOR_LIGHT_GRAY;
                                mCtaBgColor = COLOR_CTA_BLUE_BG;
                                break;
                            case 1:
                                mAdBackgroundColor = Color.BLACK;
                                mTitleColor = Color.WHITE;
                                mContentColor = Color.LTGRAY;
                                mLinkColor = Color.BLACK;
                                mCtaBgColor = Color.WHITE;
                                break;
                        }
                        reloadAdContainer();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

        mAdViewTypeSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> arg0, View view, int position,
                                               long id) {
                        int item = mAdViewTypeSpinner.getSelectedItemPosition();
                        if (item == 0) {
                            mViewType = NativeBannerAdView.Type.HEIGHT_50;
                        } else if (item == 1) {
                            mViewType = NativeBannerAdView.Type.HEIGHT_100;
                        } else if (item == 2) {
                            mViewType = NativeBannerAdView.Type.HEIGHT_120;
                        }
                        reloadAdContainer();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
    }

    @Override
    public void onAdLoaded(Ad ad) {
        if (mNativeBannerAd == null || mNativeBannerAd != ad) {
            return;
        }
        reloadAdContainer();
    }

    @Override
    public void onError(Ad ad, AdError error) {
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

    @Override
    public void onDestroy() {
        mNativeBannerAd = null;
        super.onDestroy();
    }
}