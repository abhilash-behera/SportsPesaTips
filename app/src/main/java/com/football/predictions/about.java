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
import com.facebook.ads.AdListener;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.football.predictions.R;

import java.util.ArrayList;
import java.util.List;

public class about extends AppCompatActivity {
    //private AdView mAdView;
    //private InterstitialAd mInterstitialAd;

    private com.facebook.ads.AdView bannerAdView;

    private NativeAd nativeAd;
    private LinearLayout nativeAdContainer;
    private LinearLayout nativeAdLayout;

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
        RelativeLayout adViewContainer = (RelativeLayout)findViewById(R.id.adViewContainer);
        try{
            bannerAdView = new com.facebook.ads.AdView(about.this, "342304149587187_342509876233281", com.facebook.ads.AdSize.BANNER_HEIGHT_50);
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
        try{
            nativeAd = new NativeAd(about.this, "342304149587187_342510142899921");
            nativeAd.setAdListener(new AdListener() {

                @Override
                public void onError(Ad ad, AdError error) {
                    // Ad error callback
                    if(error.getErrorCode()==AdError.NO_FILL_ERROR_CODE){
                        Handler handler=new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                showNativeAd();
                            }
                        },30000);
                    }
                    Log.d("awesome","Error in opening home fragment native ad: "+error.getErrorMessage());
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    // Ad loaded callback
                    Log.d("awesome","home fragment Native ad loaded: "+ad);
                    if (nativeAd != null) {
                        nativeAd.unregisterView();
                    }

                    // Add the Ad view into the ad container.
                    nativeAdContainer = (LinearLayout)findViewById(R.id.native_ad_container);
                    try{
                        LayoutInflater inflater = LayoutInflater.from(about.this);
                        // Inflate the Ad view.  The layout referenced should be the one you created in the last step.
                        nativeAdLayout = (LinearLayout) inflater.inflate(R.layout.native_ad_layout, nativeAdContainer, false);
                        nativeAdContainer.addView(nativeAdLayout);

                        // Create native UI using the ad metadata.
                        ImageView nativeAdIcon = (ImageView) nativeAdLayout.findViewById(R.id.native_ad_icon);
                        TextView nativeAdTitle = (TextView) nativeAdLayout.findViewById(R.id.native_ad_title);
                        MediaView nativeAdMedia = (MediaView) nativeAdLayout.findViewById(R.id.native_ad_media);
                        TextView nativeAdSocialContext = (TextView) nativeAdLayout.findViewById(R.id.native_ad_social_context);
                        TextView nativeAdBody = (TextView) nativeAdLayout.findViewById(R.id.native_ad_body);
                        Button nativeAdCallToAction = (Button) nativeAdLayout.findViewById(R.id.native_ad_call_to_action);

                        // Set the Text.
                        nativeAdTitle.setText(nativeAd.getAdTitle());
                        nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
                        nativeAdBody.setText(nativeAd.getAdBody());
                        nativeAdCallToAction.setText(nativeAd.getAdCallToAction());

                        // Download and display the ad icon.
                        NativeAd.Image adIcon = nativeAd.getAdIcon();
                        NativeAd.downloadAndDisplayImage(adIcon, nativeAdIcon);

                        // Download and display the cover image.
                        nativeAdMedia.setNativeAd(nativeAd);

                        // Add the AdChoices icon
                        LinearLayout adChoicesContainer = (LinearLayout)findViewById(R.id.ad_choices_container);
                        AdChoicesView adChoicesView = new AdChoicesView(about.this, nativeAd, true);
                        adChoicesContainer.addView(adChoicesView);

                        // Register the Title and CTA button to listen for clicks.
                        List<View> clickableViews = new ArrayList<>();
                        clickableViews.add(nativeAdTitle);
                        clickableViews.add(nativeAdCallToAction);
                        nativeAd.registerViewForInteraction(nativeAdContainer,clickableViews);
                    }catch (Exception ignored){}

                }

                @Override
                public void onAdClicked(Ad ad) {
                    // Ad clicked callback
                    Log.d("awesome","home fragment Native ad clicked: "+ad);
                }

                @Override
                public void onLoggingImpression(Ad ad) {
                    // Ad impression logged callback
                    Log.d("awesome","home fragment Native ad impression: "+ad);
                }
            });

            // Request an ad
            nativeAd.loadAd();
        }catch (Exception ignored){}

    }



}
