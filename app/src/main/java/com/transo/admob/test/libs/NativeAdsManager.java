package com.transo.admob.test.libs;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mopub.common.MoPub;
import com.mopub.nativeads.GooglePlayServicesAdRenderer;
import com.mopub.nativeads.MediaViewBinder;
import com.mopub.nativeads.MoPubNative;
import com.mopub.nativeads.MoPubStaticNativeAdRenderer;
import com.mopub.nativeads.NativeAd;
import com.mopub.nativeads.NativeErrorCode;
import com.mopub.nativeads.RequestParameters;
import com.mopub.nativeads.ViewBinder;
import com.transo.admob.test.R;
import com.transo.admob.test.callback.NativeAdsCallback;

import java.util.EnumSet;

/**
 * Manages cache & timeout of native ads
 */
@SuppressWarnings("unused")
public class NativeAdsManager {

    public static void setUpMoPubAdTryout(@NonNull final Context context, @NonNull String unitId,
                                          @LayoutRes int layoutId,
                                          @NonNull final NativeAdsCallback callback) {
        ViewBinder viewBinder = new ViewBinder.Builder(layoutId).titleId(R.id.textView_title).iconImageId(R.id.imageView_icon)
                .callToActionId(R.id.textView_cta)
                .privacyInformationIconImageId(R.id.imageView_privacy).build();

        MediaViewBinder mediaViewBinder =
                new MediaViewBinder.Builder(layoutId).titleId(R.id.textView_title)
                        // If you've content TextView
                        // .textId(R.id.textView_content)
                        .iconImageId(R.id.imageView_icon)
                        // If you've MediaLayout
                        // .mediaLayoutId(R.id.mediaLayout)
                        .callToActionId(R.id.textView_cta)
                        .privacyInformationIconImageId(R.id.imageView_privacy)
                        // Must be FrameLayout
                        .addExtra(
                                GooglePlayServicesAdRenderer.VIEW_BINDER_KEY_AD_CHOICES_ICON_CONTAINER,
                                R.id.view_privacy)
                        // Must set parent view to UnifiedNativeAdView
                        .addExtra(
                                GooglePlayServicesAdRenderer.VIEW_BINDER_KEY_UNIFIED_NATIVE_AD_VIEW,
                                R.id.unifiedNativeAdView).build();

        setUpMoPubAdTryout(context, unitId, viewBinder, mediaViewBinder, callback);
    }

    private static void setUpMoPubAdTryout(@NonNull final Context context,
                                           @NonNull final String unitId,
                                           @NonNull final ViewBinder viewBinder,
                                           @NonNull final NativeAdsCallback callback) {
        setUpMoPubAdTryout(context, unitId, viewBinder, null, callback);
    }

    public static void setUpMoPubAdTryout(@NonNull final Context context,
                                          @NonNull final String unitId,
                                          @NonNull final ViewBinder viewBinder,
                                          @Nullable final MediaViewBinder mediaViewBinder,
                                          @NonNull final NativeAdsCallback callback) {
        if (!MoPub.isSdkInitialized()) {
            new Thread(() -> {
                int cnt = 0;
                while (!MoPub.isSdkInitialized() && cnt <= 30) {
                    try {
                        Thread.sleep(100);
                        cnt++;
                    } catch (Exception ignore) {

                    }
                }
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (MoPub.isSdkInitialized()) {
                        setUpMoPubAdTryout(context, unitId, viewBinder, mediaViewBinder, callback);
                    } else {
                        callback.onFail();
                    }
                });
            }).start();
            return;
        }

        MoPubNative.MoPubNativeNetworkListener moPubNativeListener =
                new MoPubNative.MoPubNativeNetworkListener() {

                    @Override
                    public void onNativeLoad(NativeAd nativeAd) {
                        callback.onNative(nativeAd);
                    }

                    @Override
                    public void onNativeFail(NativeErrorCode errorCode) {
                        callback.onFail();
                    }
                };

        // MoPub.initializeSdk(context, new SdkConfiguration.Builder(unitId).build(), null);
        MoPubNative moPubNative = new MoPubNative(context, unitId, moPubNativeListener);

        MoPubStaticNativeAdRenderer moPubStaticNativeAdRenderer =
                new MoPubStaticNativeAdRenderer(viewBinder);
        moPubNative.registerAdRenderer(moPubStaticNativeAdRenderer);

        if (mediaViewBinder != null) {
            GooglePlayServicesAdRenderer adMobAdRenderer =
                    new GooglePlayServicesAdRenderer(mediaViewBinder);
            moPubNative.registerAdRenderer(adMobAdRenderer);
        }

        EnumSet<RequestParameters.NativeAdAsset> assetsSet =
                EnumSet.of(RequestParameters.NativeAdAsset.TITLE,
                        RequestParameters.NativeAdAsset.TEXT,
                        RequestParameters.NativeAdAsset.CALL_TO_ACTION_TEXT,
                        RequestParameters.NativeAdAsset.ICON_IMAGE,
                        RequestParameters.NativeAdAsset.MAIN_IMAGE);
        RequestParameters requestParameters =
                new RequestParameters.Builder().desiredAssets(assetsSet).build();
        moPubNative.makeRequest(requestParameters);
    }

}

