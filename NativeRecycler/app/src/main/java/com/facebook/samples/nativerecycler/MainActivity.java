package com.facebook.samples.nativerecycler;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.ads.AdError;
import com.facebook.ads.AdSettings;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.NativeAdsManager;
import com.facebook.samples.nativerecycler.adapters.NativeAdRecyclerAdapter;
import com.facebook.samples.nativerecycler.models.RecyclerPostItem;
import com.facebook.samples.nativerecycler.thridparty.DividerItemDecoration.DividerItemDecoration;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NativeAdsManager.Listener {

    private ArrayList<RecyclerPostItem> mPostItemList;
    private NativeAdsManager mNativeAdsManager;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the Audience Network SDK
        AudienceNetworkAds.initialize(this);
        AdSettings.addTestDevice("4d50eddb-1239-4b50-a643-2fd40563f971");

        // Create some dummy post items
        mPostItemList = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            mPostItemList.add(new RecyclerPostItem("RecyclerView Item #" + i));
        }

        String placement_id = getResources().getString(R.string.fan_native_ads_id);
        mNativeAdsManager = new NativeAdsManager(this, placement_id, 5);
        mNativeAdsManager.loadAds();
        mNativeAdsManager.setListener(this);
        mRecyclerView = findViewById(R.id.recyclerView);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onAdsLoaded() {
        if (mNativeAdsManager == null) {
            return;
        }
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL_LIST);
        mRecyclerView.addItemDecoration(itemDecoration);
        NativeAdRecyclerAdapter adapter = new NativeAdRecyclerAdapter(this, mPostItemList,
                mNativeAdsManager);
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onAdError(AdError adError) {

    }
}