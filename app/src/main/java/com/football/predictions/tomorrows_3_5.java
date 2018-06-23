package com.football.predictions;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.ads.NativeAd;
import com.football.predictions.retrofit.ApiClient;
import com.football.predictions.retrofit.PhoneTodayResponse;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class tomorrows_3_5 extends Fragment {
    private View rootView;
    private ProgressBar progressBar;
    private NativeAd previousBottomNative;
    private NativeAd previousTopNative;
    private LinearLayout previousTopNativeContainer;
    private LinearLayout previousBottomNativeContainer;
    private LinearLayout previousTopNativeAdLayout;
    private LinearLayout previousBottomNativeAdLayout;
    private TextView txtError;
    private WebView webView;

    private RecyclerView recyclerView;



    public tomorrows_3_5() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_previous, container, false);
        txtError=rootView.findViewById(R.id.txtError);
        webView = (WebView)rootView.findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setHorizontalScrollBarEnabled(false);
        webView.loadUrl("http://sportpesatips.dx.am/mybanner.php");
        webView.setWebViewClient(new WebViewClient(){
            public void onPageFinished(WebView view, String url) {
                webView.loadUrl("javascript:MyApp.resize(document.getElementById('banner').scrollHeight)");
                super.onPageFinished(view, url);
            }
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url != null ) {
                    view.getContext().startActivity(
                            new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    return true;
                } else {
                    return false;
                }
            }

            public void onReceivedError(WebView webView, int errorCode, String description, String failingUrl) {
                webView.loadUrl("file:///android_asset/error.html");
            }
        });
        webView.addJavascriptInterface(this, "MyApp");

        /*webView = (WebView)rootView.findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.setHorizontalScrollBarEnabled(false);
        webView.loadUrl("http://sportpesatips.dx.am/phoneprevious.php");
        webView.setWebViewClient(new WebViewClient(){

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url != null ) {
                    view.getContext().startActivity(
                            new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    return true;
                } else {
                    return false;
                }
            }

            public void onReceivedError(WebView webView, int errorCode, String description, String failingUrl) {
                webView.loadUrl("file:///android_asset/error.html");
            }
        });

        // Sample AdMob app ID: ca-app-pub-3940256099942544~3347511713
        *//*MobileAds.initialize(getContext(), "ca-app-pub-1591993844409076~8351341971");
        AdView mAdView = (AdView) view.findViewById(R.id.adViewa);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);*//*
        try{
            showBannerAd();
            showNativeAd();
        }catch (Exception ignored){}
*/      progressBar=rootView.findViewById(R.id.progressBar);
        recyclerView=rootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //showPreviousTopNative();
        //showPreviousBottomNative();
        progressBar.setVisibility(View.VISIBLE);
        Call<PhoneTodayResponse> call= ApiClient.getClient().getPhoneTomorrowsGames();
        call.enqueue(new Callback<PhoneTodayResponse>() {
            @Override
            public void onResponse(Call<PhoneTodayResponse> call, Response<PhoneTodayResponse> response) {
                Log.d("awesome","Got response: "+response.body().getData().size());
                progressBar.setVisibility(View.GONE);
                if(response.body().getData().size()==0){
                    recyclerView.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    txtError.setVisibility(View.VISIBLE);

                }else{
                    ArrayList<String> nativeAdIds=new ArrayList<>();
                    nativeAdIds.add(getResources().getString(R.string.tomorrows_3_5_a)); //previous top native
                    nativeAdIds.add(getResources().getString(R.string.tomorrows_3_5_b)); //previous middle native
                    nativeAdIds.add(getResources().getString(R.string.tomorrows_3_5_c)); //previous bottom native

                    ArrayList<Game> modifiedGameList=new ArrayList<>();
                    for(Game game:response.body().getData()){
                        if(game.getTip().toLowerCase().contains("under 3.5")){
                            Log.d("awesome","adding game to modified list");
                            modifiedGameList.add(game);
                        }
                    }

                    if(modifiedGameList.size()==0){
                        recyclerView.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        txtError.setVisibility(View.VISIBLE);
                    }else{
                        recyclerView.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        txtError.setVisibility(View.GONE);
                        recyclerView.setAdapter(new GamesAdapter(getActivity(),modifiedGameList,nativeAdIds));
                        recyclerView.getAdapter().notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<PhoneTodayResponse> call, Throwable t) {
                Log.d("awesome","Got failure in loading previous_1.5: "+t.getLocalizedMessage());
            }
        });
        return rootView;
    }

    @JavascriptInterface
    public void resize(final float height) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("awesome","height: "+height);
                RelativeLayout.LayoutParams layoutParams=new RelativeLayout.LayoutParams(getResources().getDisplayMetrics().widthPixels, (int) (height * getResources().getDisplayMetrics().density));
                webView.setLayoutParams(new RelativeLayout.LayoutParams(layoutParams));
            }
        });
    }

    private void showBannerAd() {
        /*RelativeLayout adViewContainer = (RelativeLayout)rootView.findViewById(R.id.adViewContainer);

        try{
            bannerAdView = new AdView(getActivity(), "342304149587187_342508672900068", AdSize.BANNER_HEIGHT_50);
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
                    Log.d("awesome","Error in loading previous matches banner ad: "+adError.getErrorMessage());
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    Log.d("awesome","previous matches banner ad loaded: "+ad);
                }

                @Override
                public void onAdClicked(Ad ad) {
                    Log.d("awesome","previous matches banner ad clicked: "+ad);
                }

                @Override
                public void onLoggingImpression(Ad ad) {
                    Log.d("awesome","previous matches banner ad impression: "+ad);
                }
            });

            bannerAdView.loadAd();
        }catch (Exception ignored){}*/

    }

    private void showNativeAd() {
        /*try{
            nativeAd = new NativeAd(getActivity(), "342304149587187_342509276233341");
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
                    Log.d("awesome","Error in opening previous fragment native ad: "+error.getErrorMessage());
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    // Ad loaded callback
                    Log.d("awesome","previous fragment Native ad loaded: "+ad);
                    if (nativeAd != null) {
                        nativeAd.unregisterView();
                    }

                    // Add the Ad view into the ad container.
                    nativeAdContainer = (LinearLayout)rootView.findViewById(R.id.native_ad_container);
                    try{
                        LayoutInflater inflater = LayoutInflater.from(getActivity());
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
                        LinearLayout adChoicesContainer = (LinearLayout)rootView.findViewById(R.id.ad_choices_container);
                        AdChoicesView adChoicesView = new AdChoicesView(getActivity(), nativeAd, true);
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
                    Log.d("awesome","previous fragment Native ad clicked: "+ad);
                }

                @Override
                public void onLoggingImpression(Ad ad) {
                    // Ad impression logged callback
                    Log.d("awesome","previous fragment Native ad impression: "+ad);
                }
            });

            // Request an ad
            nativeAd.loadAd();
        }catch (Exception ignored){}
*/
    }

    /*private void showPreviousTopNative(){
        try{
            previousTopNative = new NativeAd(getActivity(), "342304149587187_354848578332744");
            previousTopNative.setAdListener(new AdListener() {

                @Override
                public void onError(Ad ad, AdError error) {
                    // Ad error callback
                    if(error.getErrorCode()==AdError.NO_FILL_ERROR_CODE){
                        Handler handler=new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                showPreviousTopNative();
                            }
                        },30000);
                    }
                    Log.d("awesome","Error in opening previous fragment top native ad: "+error.getErrorMessage());
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    // Ad loaded callback
                    Log.d("awesome","previous fragment top Native ad loaded: "+ad);
                    if (previousTopNative != null) {
                        previousTopNative.unregisterView();
                    }

                    // Add the Ad view into the ad container.
                    previousTopNativeContainer = (LinearLayout)rootView.findViewById(R.id.previous_top_native_container);
                    try{
                        LayoutInflater inflater = LayoutInflater.from(getActivity());
                        // Inflate the Ad view.  The layout referenced should be the one you created in the last step.
                        previousTopNativeAdLayout = (LinearLayout) inflater.inflate(R.layout.native_ad_layout, previousTopNativeContainer, false);
                        previousTopNativeContainer.addView(previousTopNativeAdLayout);

                        // Create native UI using the ad metadata.
                        ImageView nativeAdIcon = (ImageView) previousTopNativeAdLayout.findViewById(R.id.native_ad_icon);
                        TextView nativeAdTitle = (TextView) previousTopNativeAdLayout.findViewById(R.id.native_ad_title);
                        MediaView nativeAdMedia = (MediaView) previousTopNativeAdLayout.findViewById(R.id.native_ad_media);
                        TextView nativeAdSocialContext = (TextView) previousTopNativeAdLayout.findViewById(R.id.native_ad_social_context);
                        TextView nativeAdBody = (TextView) previousTopNativeAdLayout.findViewById(R.id.native_ad_body);
                        Button nativeAdCallToAction = (Button) previousTopNativeAdLayout.findViewById(R.id.native_ad_call_to_action);

                        // Set the Text.
                        nativeAdTitle.setText(previousTopNative.getAdTitle());
                        nativeAdSocialContext.setText(previousTopNative.getAdSocialContext());
                        nativeAdBody.setText(previousTopNative.getAdBody());
                        nativeAdCallToAction.setText(previousTopNative.getAdCallToAction());

                        // Download and display the ad icon.
                        NativeAd.Image adIcon = previousTopNative.getAdIcon();
                        NativeAd.downloadAndDisplayImage(adIcon, nativeAdIcon);

                        // Download and display the cover image.
                        nativeAdMedia.setNativeAd(previousTopNative);



                        // Add the AdChoices icon
                        LinearLayout adChoicesContainer = (LinearLayout)rootView.findViewById(R.id.ad_choices_container);
                        AdChoicesView adChoicesView = new AdChoicesView(getActivity(), previousTopNative, true);
                        adChoicesContainer.addView(adChoicesView);

                        // Register the Title and CTA button to listen for clicks.
                        List<View> clickableViews = new ArrayList<>();
                        clickableViews.add(nativeAdTitle);
                        clickableViews.add(nativeAdCallToAction);
                        previousTopNative.registerViewForInteraction(previousTopNativeContainer,clickableViews);
                    }catch (Exception ignored){}

                }

                @Override
                public void onAdClicked(Ad ad) {
                    // Ad clicked callback
                    Log.d("awesome","previous fragment bottom Native ad clicked: "+ad);
                }

                @Override
                public void onLoggingImpression(Ad ad) {
                    // Ad impression logged callback
                    Log.d("awesome","previous fragment bottom Native ad impression: "+ad);
                }
            });

            // Request an ad
            previousTopNative.loadAd();
        }catch (Exception ignored){}
    }

    private void showPreviousBottomNative(){
        try{
            previousBottomNative = new NativeAd(getActivity(), "342304149587187_354849218332680");
            previousBottomNative.setAdListener(new AdListener() {

                @Override
                public void onError(Ad ad, AdError error) {
                    // Ad error callback
                    if(error.getErrorCode()==AdError.NO_FILL_ERROR_CODE){
                        Handler handler=new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                showPreviousBottomNative();
                            }
                        },30000);
                    }
                    Log.d("awesome","Error in opening previous fragment bottom native ad: "+error.getErrorMessage());
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    // Ad loaded callback
                    Log.d("awesome","previous fragment bottom Native ad loaded: "+ad);
                    if (previousBottomNative != null) {
                        previousBottomNative.unregisterView();
                    }

                    // Add the Ad view into the ad container.
                    previousBottomNativeContainer = (LinearLayout)rootView.findViewById(R.id.previous_bottom_native_container);
                    try{
                        LayoutInflater inflater = LayoutInflater.from(getActivity());
                        // Inflate the Ad view.  The layout referenced should be the one you created in the last step.
                        previousBottomNativeAdLayout = (LinearLayout) inflater.inflate(R.layout.native_ad_layout, previousBottomNativeContainer, false);
                        previousBottomNativeContainer.addView(previousBottomNativeAdLayout);

                        // Create native UI using the ad metadata.
                        ImageView nativeAdIcon = (ImageView) previousBottomNativeAdLayout.findViewById(R.id.native_ad_icon);
                        TextView nativeAdTitle = (TextView) previousBottomNativeAdLayout.findViewById(R.id.native_ad_title);
                        MediaView nativeAdMedia = (MediaView) previousBottomNativeAdLayout.findViewById(R.id.native_ad_media);
                        TextView nativeAdSocialContext = (TextView) previousBottomNativeAdLayout.findViewById(R.id.native_ad_social_context);
                        TextView nativeAdBody = (TextView) previousBottomNativeAdLayout.findViewById(R.id.native_ad_body);
                        Button nativeAdCallToAction = (Button) previousBottomNativeAdLayout.findViewById(R.id.native_ad_call_to_action);

                        // Set the Text.
                        nativeAdTitle.setText(previousBottomNative.getAdTitle());
                        nativeAdSocialContext.setText(previousBottomNative.getAdSocialContext());
                        nativeAdBody.setText(previousBottomNative.getAdBody());
                        nativeAdCallToAction.setText(previousBottomNative.getAdCallToAction());

                        // Download and display the ad icon.
                        NativeAd.Image adIcon = previousBottomNative.getAdIcon();
                        NativeAd.downloadAndDisplayImage(adIcon, nativeAdIcon);

                        // Download and display the cover image.
                        nativeAdMedia.setNativeAd(previousBottomNative);



                        // Add the AdChoices icon
                        LinearLayout adChoicesContainer = (LinearLayout)rootView.findViewById(R.id.ad_choices_container);
                        AdChoicesView adChoicesView = new AdChoicesView(getActivity(), previousBottomNative, true);
                        adChoicesContainer.addView(adChoicesView);

                        // Register the Title and CTA button to listen for clicks.
                        List<View> clickableViews = new ArrayList<>();
                        clickableViews.add(nativeAdTitle);
                        clickableViews.add(nativeAdCallToAction);
                        previousBottomNative.registerViewForInteraction(previousBottomNativeContainer,clickableViews);
                    }catch (Exception ignored){}

                }

                @Override
                public void onAdClicked(Ad ad) {
                    // Ad clicked callback
                    Log.d("awesome","previous fragment bottom Native ad clicked: "+ad);
                }

                @Override
                public void onLoggingImpression(Ad ad) {
                    // Ad impression logged callback
                    Log.d("awesome","previous fragment bottom Native ad impression: "+ad);
                }
            });

            // Request an ad
            previousBottomNative.loadAd();
        }catch (Exception ignored){}
    }*/

}
