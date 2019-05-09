package com.transo.admob.test.base;

import android.content.Context;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.google.android.gms.ads.MobileAds;
import com.mopub.common.MoPub;
import com.mopub.common.SdkConfiguration;
import com.transo.admob.test.R;

public class BaseApplication extends MultiDexApplication {

    public static final String MOPUB_AD_UNIT_ID = "Your AdMob Ad Unit Id";

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MoPub.initializeSdk(this,
                new SdkConfiguration.Builder(MOPUB_AD_UNIT_ID).build(), null);
        MobileAds.initialize(this, getString(R.string.admob_app_id));
    }
}
