package com.transo.admob.test.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.mopub.nativeads.NativeAd;
import com.transo.admob.test.R;
import com.transo.admob.test.base.BaseApplication;
import com.transo.admob.test.callback.NativeAdsCallback;
import com.transo.admob.test.libs.NativeAdsManager;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    private Adapter mAdapter;

    private NativeAd mNativeAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setUpViews();
    }

    private void setUpMoPubAdTryout() {
        NativeAdsManager
                .setUpMoPubAdTryout(this, BaseApplication.MOPUB_AD_UNIT_ID,
                        R.layout.item_ad, new NativeAdsCallback() {

                            @Override
                            public void onNative(NativeAd nativeAd) {
                                if (isFinishing()) {
                                    return;
                                }
                                mNativeAd = nativeAd;
                                mNativeAd.setMoPubNativeEventListener(new NativeAd.MoPubNativeEventListener() {
                                    @Override
                                    public void onImpression(View view) {

                                    }

                                    @Override
                                    public void onClick(View view) {
                                        setUpMoPubAdTryout();
                                    }
                                });
                                if (mAdapter != null) {
                                    mAdapter.notifyDataSetChanged();
                                }
                            }


                            @Override
                            public void onFail() {

                            }
                        });
    }

    private void setUpViews() {
        mAdapter = new Adapter();

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mRecyclerView.setAdapter(mAdapter);

        setUpMoPubAdTryout();
    }

    class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private final int VIEW_TYPE = 1, VIEW_TYPE_AD = 2;

        class ViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.textView)
            TextView textView;

            private ViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }

        }

        class AdViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.unifiedNativeAdView)
            UnifiedNativeAdView adView;

            @BindView(R.id.imageView_icon)
            ImageView iconImageView;
            @BindView(R.id.textView_cta)
            TextView ctaTextView;
            @BindView(R.id.textView_title)
            TextView titleTextView;

            @BindView(R.id.view_privacy)
            FrameLayout privacyView;

            private AdViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }
        }

        @Override
        public int getItemViewType(int position) {
            return mNativeAd != null && position == 0 ? VIEW_TYPE_AD : VIEW_TYPE;
        }


        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            if (viewType == VIEW_TYPE) {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_test, parent, false);
                return new ViewHolder(view);
            } else {
                view = mNativeAd.createAdView(parent.getContext(), parent);
                return new AdViewHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            int viewType = getItemViewType(position);


            if (viewType == VIEW_TYPE) {
                ViewHolder viewHolder = (ViewHolder) holder;

                viewHolder.textView.setText(String.format(Locale.getDefault(), "Test %d", position - (mNativeAd != null ? 1 : 0)));
            } else {
                mNativeAd.renderAdView(holder.itemView);
                mNativeAd.prepare(holder.itemView);
            }
        }

        @Override
        public int getItemCount() {
            return 100 + (mNativeAd != null ? 1 : 0);
        }
    }

}
