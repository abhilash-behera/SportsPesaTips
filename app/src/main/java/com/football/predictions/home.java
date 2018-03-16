package com.football.predictions;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.ads.Ad;
import com.facebook.ads.AdChoicesView;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.football.predictions.R;
import com.football.predictions.retrofit.ApiClient;
import com.football.predictions.retrofit.PhoneHomeResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class home extends Fragment {
    private View rootView;
    private WebView webView;
    /*private AdView bannerAdView;


    private NativeAd nativeAd;
    private LinearLayout nativeAdContainer;
    private LinearLayout nativeAdLayout;*/

    private RecyclerView recyclerView;
    private ProgressBar progressBar;


    public home() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /// Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_home, container, false);

        webView = (WebView)rootView.findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setHorizontalScrollBarEnabled(false);
        webView.loadUrl("http://sportpesatips.dx.am/mybanner.php");
        webView.setWebViewClient(new WebViewClient(){
            @Override
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
/*
        try{
            showBannerAd();
            showNativeAd();
        }catch (Exception ignored){}*/
        // Sample AdMob app ID: ca-app-pub-3940256099942544~3347511713
        /*MobileAds.initialize(getContext(), "ca-app-pub-1591993844409076~8351341971");
        AdView mAdView = (AdView) view.findViewById(R.id.adViewa);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);*/
        progressBar=rootView.findViewById(R.id.progressBar);
        recyclerView=rootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        Call<PhoneHomeResponse> call= ApiClient.getClient().getPhoneHomeGames();
        call.enqueue(new Callback<PhoneHomeResponse>() {
            @Override
            public void onResponse(Call<PhoneHomeResponse> call, Response<PhoneHomeResponse> response) {
                Log.d("awesome","Got response: "+response.body().getData().size());
                progressBar.setVisibility(View.GONE);
                if(response.body().getData().size()==0){
                    TextView textView=new TextView(getActivity());
                    textView.setText("Sorry!! No Games Present.\nPlease come back later.");
                    textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    RelativeLayout.LayoutParams layoutParams=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    textView.setLayoutParams(layoutParams);
                    ((RelativeLayout)rootView).addView(textView);

                }else{
                    ArrayList<String> nativeAdIds=new ArrayList<>();
                    nativeAdIds.add("342304149587187_354849908332611"); //VIP top native
                    nativeAdIds.add("342304149587187_354849984999270"); //VIP middle native
                    nativeAdIds.add("342304149587187_354850054999263"); //VIP bottom native
                    recyclerView.setAdapter(new GamesAdapter(getActivity(),response.body().getData(),nativeAdIds));
                    recyclerView.getAdapter().notifyDataSetChanged();
                }

            }

            @Override
            public void onFailure(Call<PhoneHomeResponse> call, Throwable t) {
                Log.d("awesome","Got failure: "+t.getLocalizedMessage());
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

    /*private void showBannerAd() {
        RelativeLayout adViewContainer = (RelativeLayout)rootView.findViewById(R.id.adViewContainer);
        try{
            bannerAdView = new AdView(getActivity(), "342304149587187_342506782900257", AdSize.BANNER_HEIGHT_50);
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
                    Log.d("awesome","Error in loading home fragment banner ad: "+adError.getErrorMessage());
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    Log.d("awesome","home fragment Banner ad loaded: "+ad);
                }

                @Override
                public void onAdClicked(Ad ad) {
                    Log.d("awesome","home fragment Banner ad clicked: "+ad);
                }

                @Override
                public void onLoggingImpression(Ad ad) {
                    Log.d("awesome","home fragment Banner ad logging impression: "+ad);
                }
            });
            bannerAdView.loadAd();
        }catch (Exception ignored){}
    }

    private void showNativeAd() {
        try{
            nativeAd = new NativeAd(getActivity(), "342304149587187_342507366233532");
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
                    }catch (Exception ignored){

                    }

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

    }*/

}
