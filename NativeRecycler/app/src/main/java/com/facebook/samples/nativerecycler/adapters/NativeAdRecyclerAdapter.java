package com.facebook.samples.nativerecycler.adapters;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.ads.AdOptionsView;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdsManager;
import com.facebook.samples.nativerecycler.R;
import com.facebook.samples.nativerecycler.models.RecyclerPostItem;

import java.util.ArrayList;
import java.util.List;

public class NativeAdRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int AD_DISPLAY_FREQUENCY = 5;
    private static final int POST_TYPE = 0;
    private static final int AD_TYPE = 1;
    private final List<RecyclerPostItem> mPostItems;
    private final List<NativeAd> mAdItems;
    private final NativeAdsManager mNativeAdsManager;
    private final Activity mActivity;

    public NativeAdRecyclerAdapter(
            Activity activity, ArrayList<RecyclerPostItem> postItems,
            NativeAdsManager nativeAdsManager) {
        mNativeAdsManager = nativeAdsManager;
        mPostItems = postItems;
        mAdItems = new ArrayList<>();
        mActivity = activity;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == AD_TYPE) {
            NativeAdLayout inflatedView =
                    (NativeAdLayout)
                            LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.native_ad_unit, parent, false);
            return new AdHolder(inflatedView);
        } else {
            View inflatedView =
                    LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.recycler_post_item, parent, false);
            return new PostHolder(inflatedView);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position % AD_DISPLAY_FREQUENCY == 0 ? AD_TYPE : POST_TYPE;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == AD_TYPE) {
            NativeAd ad;
            if (mAdItems.size() > position / AD_DISPLAY_FREQUENCY) {
                ad = mAdItems.get(position / AD_DISPLAY_FREQUENCY);
            } else {
                ad = mNativeAdsManager.nextNativeAd();
                if (ad != null && !ad.isAdInvalidated()) {
                    mAdItems.add(ad);
                }
            }
            AdHolder adHolder = (AdHolder) holder;
            adHolder.adChoicesContainer.removeAllViews();
            if (ad != null) {
                adHolder.tvAdTitle.setText(ad.getAdvertiserName());
                adHolder.tvAdBody.setText(ad.getAdBodyText());
                adHolder.tvAdSocialContext.setText(ad.getAdSocialContext());
                adHolder.tvAdSponsoredLabel.setText(R.string.sponsored);
                adHolder.btnAdCallToAction.setText(ad.getAdCallToAction());
                adHolder.btnAdCallToAction.setVisibility(
                        ad.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
                AdOptionsView adOptionsView = new AdOptionsView(mActivity, ad,
                        adHolder.nativeAdLayout);
                adHolder.adChoicesContainer.addView(adOptionsView, 0);
                List<View> clickableViews = new ArrayList<>();
                clickableViews.add(adHolder.ivAdIcon);
                clickableViews.add(adHolder.mvAdMedia);
                clickableViews.add(adHolder.btnAdCallToAction);
                ad.registerViewForInteraction(
                        adHolder.nativeAdLayout, adHolder.mvAdMedia, adHolder.ivAdIcon,
                        clickableViews);
            }
        } else {
            PostHolder postHolder = (PostHolder) holder;
            // Calculate where the next postItem index is by subtracting ads we've shown.
            int index = position - (position / AD_DISPLAY_FREQUENCY) - 1;
            RecyclerPostItem postItem = mPostItems.get(index);
            postHolder.tvPostContent.setText(postItem.getPostContent());
        }
    }

    @Override
    public int getItemCount() {
        return mPostItems.size() + mAdItems.size();
    }

    private static class PostHolder extends RecyclerView.ViewHolder {
        TextView tvPostContent;
        PostHolder(View view) {
            super(view);
            tvPostContent = view.findViewById(R.id.tvPostContent);
        }
    }

    private static class AdHolder extends RecyclerView.ViewHolder {

        NativeAdLayout nativeAdLayout;
        MediaView mvAdMedia;
        MediaView ivAdIcon;
        TextView tvAdTitle;
        TextView tvAdBody;
        TextView tvAdSocialContext;
        TextView tvAdSponsoredLabel;
        Button btnAdCallToAction;
        LinearLayout adChoicesContainer;

        AdHolder(NativeAdLayout adLayout) {
            super(adLayout);
            nativeAdLayout = adLayout;
            mvAdMedia = adLayout.findViewById(R.id.native_ad_media);
            tvAdTitle = adLayout.findViewById(R.id.native_ad_title);
            tvAdBody = adLayout.findViewById(R.id.native_ad_body);
            tvAdSocialContext = adLayout.findViewById(R.id.native_ad_social_context);
            tvAdSponsoredLabel = adLayout.findViewById(R.id.native_ad_sponsored_label);
            btnAdCallToAction = adLayout.findViewById(R.id.native_ad_call_to_action);
            ivAdIcon = adLayout.findViewById(R.id.native_ad_icon);
            adChoicesContainer = adLayout.findViewById(R.id.ad_choices_container);
        }
    }
}
