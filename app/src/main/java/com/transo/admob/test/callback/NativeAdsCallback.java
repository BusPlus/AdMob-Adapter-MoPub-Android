package com.transo.admob.test.callback;

import com.mopub.nativeads.NativeAd;

public abstract class NativeAdsCallback {

    public abstract void onNative(NativeAd nativeAd);

    public abstract void onFail();
}
