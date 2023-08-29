package com.facebook.samples.nativeadstemplate;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.AdSettings;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdBase;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;
import com.facebook.ads.NativeAdView;
import com.facebook.ads.NativeAdViewAttributes;
import com.facebook.ads.NativeBannerAd;

import java.util.ArrayList;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class MainActivity extends AppCompatActivity implements NativeAdListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int COLOR_LIGHT_GRAY = 0xff90949c;
    private static final int COLOR_DARK_GRAY = 0xff4e5665;
    private static final int COLOR_CTA_BLUE_BG = 0xff4080ff;
    private static final int MIN_HEIGHT_DP = 200;
    private static final int MAX_HEIGHT_DP = 500;
    private static final int DEFAULT_HEIGHT_DP = 350;
    private static final int DEFAULT_PROGRESS_DP = 50;

    private @Nullable
    NativeAd mNativeAd;
    private int mLayoutHeightDp = DEFAULT_HEIGHT_DP;
    private int mAdBackgroundColor, mTitleColor, mCtaTextColor, mContentColor, mCtaBgColor;
    private ViewGroup mNativeAdContainer;
    private Spinner mBackgroundColorSpinner;
    private SeekBar mSeekBar;
    private View mAdView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the Audience Network SDK
        AudienceNetworkAds.initialize(this);
        AdSettings.addTestDevice("98c7c4bf-0754-4c86-98ab-3279466ae96c");

        mNativeAdContainer = findViewById(R.id.templateContainer);
        mBackgroundColorSpinner = findViewById(R.id.backgroundColorSpinner);
        mSeekBar = findViewById(R.id.seekBar);

        setUpLayoutBuilders();
        createAndLoadNativeAd();
    }

    @Override
    public void onAdLoaded(Ad ad) {
        if (mNativeAd == null || mNativeAd != ad) {
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
        mNativeAd = null;
        super.onDestroy();
    }

    private void createAndLoadNativeAd() {
        mNativeAd = new NativeAd(this, getResources().getString(R.string.fan_native_template));
        mNativeAd.loadAd(mNativeAd.buildLoadAdConfig().withAdListener(this).build());
    }

    private void reloadAdContainer() {
        Activity activity = this;
        if (mNativeAd != null && mNativeAd.isAdLoaded() && !mNativeAd.isAdInvalidated()) {
            mNativeAdContainer.removeAllViews();

            // Create a NativeAdViewAttributes object and set the attributes
            NativeAdViewAttributes attributes =
                    new NativeAdViewAttributes()
                            .setBackgroundColor(mAdBackgroundColor)
                            .setTitleTextColor(mTitleColor)
                            .setDescriptionTextColor(mContentColor)
                            .setButtonBorderColor(mCtaTextColor)
                            .setButtonTextColor(mCtaTextColor)
                            .setButtonColor(mCtaBgColor);

            // Use NativeAdView.render to generate the ad View
            mAdView = NativeAdView.render(activity, mNativeAd, attributes);
            mNativeAdContainer.addView(mAdView, new ViewGroup.LayoutParams(MATCH_PARENT, 0));
            updateAdViewParams();
        }
    }

    private void setUpLayoutBuilders() {
        ArrayAdapter<CharSequence> backgroundColorSpinnerAdapter =
                ArrayAdapter.createFromResource(
                        this, R.array.background_color_array,
                        android.R.layout.simple_spinner_item);
        backgroundColorSpinnerAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        mBackgroundColorSpinner.setAdapter(backgroundColorSpinnerAdapter);

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
                                mCtaTextColor = COLOR_CTA_BLUE_BG;
                                mContentColor = COLOR_LIGHT_GRAY;
                                mCtaBgColor = Color.WHITE;
                                break;
                            case 1:
                                mAdBackgroundColor = Color.BLACK;
                                mTitleColor = Color.WHITE;
                                mContentColor = Color.LTGRAY;
                                mCtaTextColor = Color.BLACK;
                                mCtaBgColor = Color.WHITE;
                                break;
                        }
                        reloadAdContainer();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

        mSeekBar.setProgress(DEFAULT_PROGRESS_DP);
        mSeekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        mLayoutHeightDp =
                                progress * ((MAX_HEIGHT_DP - MIN_HEIGHT_DP) / 100) + MIN_HEIGHT_DP;
                        updateAdViewParams();
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                });
    }

    private void updateAdViewParams() {
        if (mAdView == null) {
            return;
        }
        ViewGroup.LayoutParams params = mAdView.getLayoutParams();
        params.height = (int) (Resources.getSystem().getDisplayMetrics().density * mLayoutHeightDp);
        mAdView.setLayoutParams(params);
        mAdView.requestLayout();
    }
}