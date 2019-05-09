package com.mopub.nativeads;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.ads.formats.AdChoicesView;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.mopub.common.logging.MoPubLog;
import com.mopub.nativeads.GooglePlayServicesNative.GooglePlayServicesNativeAd;

import java.util.Map;
import java.util.WeakHashMap;

import static com.mopub.common.logging.MoPubLog.AdapterLogEvent.CUSTOM;
import static com.mopub.common.logging.MoPubLog.AdapterLogEvent.CUSTOM_WITH_THROWABLE;

/**
 * The {@link GooglePlayServicesAdRenderer} class is used to render
 * GooglePlayServicesStaticNativeAds.
 */
public class GooglePlayServicesAdRenderer implements MoPubAdRenderer<GooglePlayServicesNativeAd> {

    /**
     * Key to set and get star rating text view as an extra in the view binder.
     */
    private static final String VIEW_BINDER_KEY_STAR_RATING = "key_star_rating";

    /**
     * Key to set and get advertiser text view as an extra in the view binder.
     */
    private static final String VIEW_BINDER_KEY_ADVERTISER = "key_advertiser";

    /**
     * Key to set and get store text view as an extra in the view binder.
     */
    private static final String VIEW_BINDER_KEY_STORE = "key_store";

    /**
     * Key to set and get price text view as an extra in the view binder.
     */
    private static final String VIEW_BINDER_KEY_PRICE = "key_price";

    /**
     * Key to set and get the Unified Native Ad View as an extra in the view binder.
     */
    public static final String VIEW_BINDER_KEY_UNIFIED_NATIVE_AD_VIEW = "unified_native_ad_view";

    /**
     * Key to set and get the AdChoices icon view as an extra in the view binder.
     */
    public static final String VIEW_BINDER_KEY_AD_CHOICES_ICON_CONTAINER = "ad_choices_container";

    /**
     * A view binder containing the layout resource and views to be rendered by the renderer.
     */
    private final MediaViewBinder mViewBinder;

    /**
     * A weak hash map used to keep track of view holder so that the views can be properly recycled.
     */
    private final WeakHashMap<View, GoogleStaticNativeViewHolder> mViewHolderMap;

    /**
     * String to store the simple class name for this adapter.
     */
    private static final String ADAPTER_NAME = GooglePlayServicesAdRenderer.class.getSimpleName();

    public GooglePlayServicesAdRenderer(MediaViewBinder viewBinder) {
        this.mViewBinder = viewBinder;
        this.mViewHolderMap = new WeakHashMap<>();
    }

    @NonNull
    @Override
    public View createAdView(@NonNull Context context, @Nullable ViewGroup parent) {
        MoPubLog.log(CUSTOM, ADAPTER_NAME, "Ad view created.");
        return LayoutInflater.from(context).inflate(mViewBinder.layoutId, parent, false);
    }

    @Override
    public void renderAdView(@NonNull View view, @NonNull GooglePlayServicesNativeAd nativeAd) {
        GoogleStaticNativeViewHolder viewHolder = mViewHolderMap.get(view);
        if (viewHolder == null) {
            viewHolder = GoogleStaticNativeViewHolder.fromViewBinder(view, mViewBinder);
            mViewHolderMap.put(view, viewHolder);
        }

        UnifiedNativeAdView unifiedAdView = viewHolder.mUnifiedNativeAdView;
        if (unifiedAdView != null) {
            updateUnifiedAdView(nativeAd, viewHolder, unifiedAdView);
        }
    }

    /**
     * This method will render the given native ad view using the native ad and set the views to
     * Google's native ad view.
     *
     * @param staticNativeAd         a static native ad object containing the required assets to
     *                               set to the native ad view.
     * @param staticNativeViewHolder a static native view holder object containing the mapped
     *                               views from the view binder.
     * @param unifiedAdView          the Google unified ad view in the view hierarchy.
     */

    private void updateUnifiedAdView(GooglePlayServicesNativeAd staticNativeAd,
                                     GoogleStaticNativeViewHolder staticNativeViewHolder,
                                     UnifiedNativeAdView unifiedAdView) {
        if (staticNativeViewHolder.mAdChoicesIconContainer == null) {
            return;
        }

        NativeRendererHelper
                .addTextView(staticNativeViewHolder.mTitleView, staticNativeAd.getTitle());
        unifiedAdView.setHeadlineView(staticNativeViewHolder.mTitleView);
        NativeRendererHelper
                .addTextView(staticNativeViewHolder.mTextView, staticNativeAd.getText());
        unifiedAdView.setBodyView(staticNativeViewHolder.mTextView);
        if (staticNativeViewHolder.mMediaView != null) {
            MediaView mediaview = new MediaView(unifiedAdView.getContext());
            staticNativeViewHolder.mMediaView.removeAllViews();
            staticNativeViewHolder.mMediaView.addView(mediaview);
            unifiedAdView.setMediaView(mediaview);
        }
        NativeRendererHelper.addTextView(staticNativeViewHolder.mCallToActionView,
                staticNativeAd.getCallToAction());
        unifiedAdView.setCallToActionView(staticNativeViewHolder.mMainView);
        NativeImageHelper.loadImageView(staticNativeAd.getIconImageUrl(),
                staticNativeViewHolder.mIconImageView);
        unifiedAdView.setIconView(staticNativeViewHolder.mIconImageView);
        if (staticNativeAd.getAdvertiser() != null) {
            NativeRendererHelper.addTextView(staticNativeViewHolder.mAdvertiserTextView,
                    staticNativeAd.getAdvertiser());
            unifiedAdView.setAdvertiserView(staticNativeViewHolder.mAdvertiserTextView);
        }
        // Add the AdChoices icon to the container if one is provided by the publisher.
        AdChoicesView adChoicesView = new AdChoicesView(unifiedAdView.getContext());
        staticNativeViewHolder.mAdChoicesIconContainer.removeAllViews();
        staticNativeViewHolder.mAdChoicesIconContainer.addView(adChoicesView);
        unifiedAdView.setAdChoicesView(adChoicesView);

        // Set the privacy information icon to null as the Google Mobile Ads SDK automatically
        // renders the AdChoices icon.
        NativeRendererHelper
                .addPrivacyInformationIcon(staticNativeViewHolder.mPrivacyInformationIconImageView,
                        null, null);

        unifiedAdView.setNativeAd(staticNativeAd.getUnifiedNativeAd());
    }

    @Override
    public boolean supports(@NonNull BaseNativeAd nativeAd) {
        return nativeAd instanceof GooglePlayServicesNativeAd;
    }

    private static class GoogleStaticNativeViewHolder {

        @Nullable
        View mMainView;
        @Nullable
        UnifiedNativeAdView mUnifiedNativeAdView;
        @Nullable
        TextView mTitleView;
        @Nullable
        TextView mTextView;
        @Nullable
        TextView mCallToActionView;
        @Nullable
        ImageView mIconImageView;
        @Nullable
        ImageView mPrivacyInformationIconImageView;
        @Nullable
        TextView mStarRatingTextView;
        @Nullable
        TextView mAdvertiserTextView;
        @Nullable
        TextView mStoreTextView;
        @Nullable
        TextView mPriceTextView;
        @Nullable
        FrameLayout mAdChoicesIconContainer;
        @Nullable
        MediaLayout mMediaView;

        private static final GoogleStaticNativeViewHolder EMPTY_VIEW_HOLDER =
                new GoogleStaticNativeViewHolder();

        @NonNull
        public static GoogleStaticNativeViewHolder fromViewBinder(@NonNull View view, @NonNull
                MediaViewBinder viewBinder) {
            final GoogleStaticNativeViewHolder viewHolder = new GoogleStaticNativeViewHolder();
            viewHolder.mMainView = view;
            try {
                viewHolder.mTitleView = (TextView) view.findViewById(viewBinder.titleId);
                viewHolder.mTextView = (TextView) view.findViewById(viewBinder.textId);
                viewHolder.mCallToActionView =
                        (TextView) view.findViewById(viewBinder.callToActionId);

                viewHolder.mIconImageView = (ImageView) view.findViewById(viewBinder.iconImageId);
                viewHolder.mPrivacyInformationIconImageView =
                        (ImageView) view.findViewById(viewBinder.privacyInformationIconImageId);
                viewHolder.mMediaView = (MediaLayout) view.findViewById(viewBinder.mediaLayoutId);
                Map<String, Integer> extraViews = viewBinder.extras;
                Integer unifiedNativeAdViewId =
                        extraViews.get(VIEW_BINDER_KEY_UNIFIED_NATIVE_AD_VIEW);
                if (unifiedNativeAdViewId != null) {
                    viewHolder.mUnifiedNativeAdView =
                            (UnifiedNativeAdView) view.findViewById(unifiedNativeAdViewId);
                }
                Integer starRatingTextViewId = extraViews.get(VIEW_BINDER_KEY_STAR_RATING);
                if (starRatingTextViewId != null) {
                    viewHolder.mStarRatingTextView =
                            (TextView) view.findViewById(starRatingTextViewId);
                }
                Integer advertiserTextViewId = extraViews.get(VIEW_BINDER_KEY_ADVERTISER);
                if (advertiserTextViewId != null) {
                    viewHolder.mAdvertiserTextView =
                            (TextView) view.findViewById(advertiserTextViewId);
                }
                Integer storeTextViewId = extraViews.get(VIEW_BINDER_KEY_STORE);
                if (storeTextViewId != null) {
                    viewHolder.mStoreTextView = (TextView) view.findViewById(storeTextViewId);
                }
                Integer priceTextViewId = extraViews.get(VIEW_BINDER_KEY_PRICE);
                if (priceTextViewId != null) {
                    viewHolder.mPriceTextView = (TextView) view.findViewById(priceTextViewId);
                }
                Integer adChoicesIconViewId =
                        extraViews.get(VIEW_BINDER_KEY_AD_CHOICES_ICON_CONTAINER);
                if (adChoicesIconViewId != null) {
                    viewHolder.mAdChoicesIconContainer =
                            (FrameLayout) view.findViewById(adChoicesIconViewId);
                }
                return viewHolder;
            } catch (ClassCastException exception) {
                MoPubLog.log(CUSTOM_WITH_THROWABLE,
                        "Could not cast from id in ViewBinder to " + "expected View type",
                        exception);
                return EMPTY_VIEW_HOLDER;
            }
        }
    }

}