# AdMob-Adapter-MoPub-Android
Adapter that fixed AdMob with MoPub impr. &amp; click zero issue

## Setup
**Step 1.** Set your [AdMob App Id](https://github.com/BusPlus/AdMob-Adapter-MoPub-Android/blob/master/app/src/main/res/values/strings.xml#L4).

**Step 2.** Set your [MoPub Ad Unit Id](https://github.com/BusPlus/AdMob-Adapter-MoPub-Android/blob/master/app/src/main/java/com/transo/admob/test/base/BaseApplication.java#L15).

**Step 3.** Set your [Test Device Id](https://github.com/BusPlus/AdMob-Adapter-MoPub-Android/blob/master/app/src/main/java/com/mopub/nativeads/GooglePlayServicesNative.java#L453) to request ads in debug mode.

**Step 4.** Run `app`

**Step 5.** When you see ad show in test app, and `onAdImpression` show in `Logcat`. You've succeeded! Enjoy it!
