package com.football.predictions;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.ads.Ad;
import com.facebook.ads.AdChoicesView;
import com.facebook.ads.AdError;
import com.facebook.ads.AdIconView;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdView;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdListener;
import com.football.predictions.R;

import java.util.ArrayList;
import java.util.List;

public class about extends AppCompatActivity {
    //private AdView mAdView;
    //private InterstitialAd mInterstitialAd;

    private AdView bannerAdView;

    private NativeAd nativeAd;
    private LinearLayout nativeAdContainer;
    private LinearLayout adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);


        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.about_actionbar);

        setTitle("About Us");

        ImageView backimg = (ImageView)findViewById(R.id.action_before);
        backimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(about.this,MainActivity.class));
            }
        });

        ImageView share = (ImageView)findViewById(R.id.action_share);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBodyText = "This App Has Helped me win lot of money.\n" +
                        "It gives daily SURE BETS FOR FREE.\n" +
                        "Download it here; \n" +
                        "https://play.google.com/store/apps/details?id=com.football.predictions\n";
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,"Football Predictions");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBodyText);
                startActivity(Intent.createChooser(sharingIntent, "Share Football Predictions app With Friends"));

            }
        });

        String htmlcode = "" +
                "<html>" +
                "<head>" +
                "<head>" +
                "<body>" +
                "<b>Football Predictions is an App made by a team of Bet Gurus that are always Right.<br /><br />" +
                " If you're looking for SURE tips that will make you win, then you're in the right place. <br /><br />" +
                " Don't forget to share this amazing App with your friends to promote us.<br /><br />" +
                "</b><br /><br />" +
                "<h3>&copy Copyright 2018. Tips LTD</h3>" +
                "</body>" +
                "</html>";


        WebView wv = (WebView)this.findViewById(R.id.webview);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.loadDataWithBaseURL(null, htmlcode, "text/html", "utf-8", null);

        showBannerAd();
        showNativeAd();

    }

    private void showBannerAd() {
        RelativeLayout adViewContainer =findViewById(R.id.adViewContainer);
        try{
            bannerAdView = new com.facebook.ads.AdView(about.this, getResources().getString(R.string.sep_about_banner), com.facebook.ads.AdSize.BANNER_HEIGHT_50);
            adViewContainer.addView(bannerAdView);
            bannerAdView.setAdListener(new AdListener() {
                @Override
                public void onError(Ad ad, AdError adError) {
                    if(adError.getErrorCode()==AdError.NO_FILL_ERROR_CODE){
                        Handler handler=new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                showBannerAd();
                            }
                        },30000);
                    }
                    Log.d("awesome","Error in loading about activity banner ad: "+adError.getErrorMessage());
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    Log.d("awesome","about activity banner ad loaded: "+ad);
                }

                @Override
                public void onAdClicked(Ad ad) {
                    Log.d("awesome","about activity banner ad clicked: "+ad);
                }

                @Override
                public void onLoggingImpression(Ad ad) {
                    Log.d("awesome","about activity banner ad impression: "+ad);
                }
            });
            bannerAdView.loadAd();
        }catch (Exception ignored){}
    }

    private void showNativeAd() {
        nativeAd=new NativeAd(about.this,getResources().getString(R.string.sep_about_native));
        nativeAd.setAdListener(new NativeAdListener() {
            @Override
            public void onMediaDownloaded(Ad ad) {
                Log.d("awesome","About activity fb native media downloaded: "+ad);
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                Log.d("awesome","About activity fb native error: "+adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                Log.d("awesome","About activity fb native ad loaded: "+ad);
                nativeAd.unregisterView();

                // Add the Ad view into the ad container.
                nativeAdContainer = findViewById(R.id.native_ad_container);
                LayoutInflater inflater = LayoutInflater.from(about.this);
                // Inflate the Ad view.  The layout referenced should be the one you created in the last step.
                adView = (LinearLayout) inflater.inflate(R.layout.native_ad_layout, nativeAdContainer, false);
                nativeAdContainer.addView(adView);

                // Add the AdChoices icon
                LinearLayout adChoicesContainer = findViewById(R.id.ad_choices_container);
                AdChoicesView adChoicesView = new AdChoicesView(about.this, nativeAd, true);
                adChoicesContainer.addView(adChoicesView, 0);

                // Create native UI using the ad metadata.
                AdIconView nativeAdIcon = adView.findViewById(R.id.native_ad_icon);
                TextView nativeAdTitle = adView.findViewById(R.id.native_ad_title);
                MediaView nativeAdMedia = adView.findViewById(R.id.native_ad_media);
                TextView nativeAdSocialContext = adView.findViewById(R.id.native_ad_social_context);
                TextView nativeAdBody = adView.findViewById(R.id.native_ad_body);
                TextView sponsoredLabel = adView.findViewById(R.id.sponsored_label);
                Button nativeAdCallToAction = adView.findViewById(R.id.native_ad_call_to_action);

                // Set the Text.
                nativeAdTitle.setText(nativeAd.getAdvertiserName());
                nativeAdBody.setText(nativeAd.getAdBodyText());
                nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
                nativeAdCallToAction.setVisibility(nativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
                nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
                sponsoredLabel.setText(nativeAd.getSponsoredTranslation());

                // Create a list of clickable views
                List<View> clickableViews = new ArrayList<>();
                clickableViews.add(nativeAdTitle);
                clickableViews.add(nativeAdCallToAction);

                // Register the Title and CTA button to listen for clicks.
                nativeAd.registerViewForInteraction(
                        adView,
                        nativeAdMedia,
                        nativeAdIcon,
                        clickableViews);
            }

            @Override
            public void onAdClicked(Ad ad) {
                Log.d("awesome","About activity fb native clicked");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                Log.d("awesome","About activity fb native impression");
            }
        });
        nativeAd.loadAd();
    }
}
