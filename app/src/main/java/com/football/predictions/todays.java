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
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.football.predictions.retrofit.ApiClient;
import com.football.predictions.retrofit.GameResponse;

import java.util.ArrayList;

import io.presage.Presage;
import io.presage.common.AdConfig;
import io.presage.common.network.models.RewardItem;
import io.presage.interstitial.optinvideo.PresageOptinVideo;
import io.presage.interstitial.optinvideo.PresageOptinVideoCallback;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class todays extends Fragment {
    private View rootView;
    private ProgressBar progressBar;
    private TextView txtError;
    private WebView webView;
    private int currentPage;
    private int totalPageCount;

    private Button btnPrevious;
    private Button btnNext;

    private ArrayList<Game> originalGamesList=new ArrayList<>();
    private ArrayList<Game> filteredGamesList=new ArrayList<>();
    private ArrayList<String> nativeAdIds=new ArrayList<>();

    private RecyclerView recyclerView;
    private PresageOptinVideo presageOptinVideo;



    public todays() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Presage.getInstance().start("272929", getActivity());
        rootView = inflater.inflate(R.layout.fragment_previous, container, false);
        txtError=rootView.findViewById(R.id.txtError);


        btnPrevious=rootView.findViewById(R.id.btnPrevious);
        btnNext=rootView.findViewById(R.id.btnNext);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentPage==totalPageCount-1){
                    showOptInVideo(getResources().getString(R.string.opt_in_video));
                }
                if(currentPage<totalPageCount){
                    currentPage++;
                    Log.d("pagenation","Current page: "+currentPage+" Total Page:"+totalPageCount);
                    for(int i=0;i<4;i++){
                        filteredGamesList.remove(0);
                    }
                    for(int i=0;i<4;i++){
                        try{
                            filteredGamesList.add(originalGamesList.get((4*currentPage)+i));
                        }catch(Exception e){
                            Log.d("pagenation","This is a silly exception");
                        }
                    }
                    recyclerView.setAdapter(new GamesAdapter(getActivity(),filteredGamesList,nativeAdIds,getResources().getString(R.string.todays_banner)));
                    recyclerView.getAdapter().notifyDataSetChanged();
                }else{
                    Toast.makeText(getActivity(),"You are already on the last page !!!",Toast.LENGTH_LONG).show();
                }
            }
        });

        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentPage>0){
                    currentPage--;
                    Log.d("pagenation","Current page: "+currentPage+" Total Page:"+totalPageCount);
                    try{
                        for(int i=0;i<4;i++){
                            filteredGamesList.remove(0);
                        }
                    }catch (Exception ignored){}

                    for(int i=0;i<4;i++){
                        try{
                            filteredGamesList.add(originalGamesList.get((4*currentPage)+i));
                        }catch(Exception e){
                            Log.d("pagenation","This is a silly exception");
                        }
                    }
                    recyclerView.setAdapter(new GamesAdapter(getActivity(),filteredGamesList,nativeAdIds,getResources().getString(R.string.todays_banner)));
                    recyclerView.getAdapter().notifyDataSetChanged();
                }else{
                    Toast.makeText(getActivity(),"You are already on the first page !!!",Toast.LENGTH_LONG).show();
                }
            }
        });

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

        progressBar=rootView.findViewById(R.id.progressBar);
        recyclerView=rootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //showPreviousTopNative();
        //showPreviousBottomNative();
        progressBar.setVisibility(View.VISIBLE);
        Call<GameResponse> call= ApiClient.getClient().getPhoneTodayGames();
        call.enqueue(new Callback<GameResponse>() {
            @Override
            public void onResponse(Call<GameResponse> call, Response<GameResponse> response) {
                Log.d("awesome","Got response: "+response.body().getData().size());
                progressBar.setVisibility(View.GONE);
                if(response.body().getData().size()==0){
                    recyclerView.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    txtError.setVisibility(View.VISIBLE);

                }else{
                    //nativeAdIds.add(getResources().getString(R.string.todays_a)); //previous top native
                    //nativeAdIds.add(getResources().getString(R.string.todays_b)); //previous middle native
                    //nativeAdIds.add(getResources().getString(R.string.todays_c)); //previous bottom native

                    //nativeAdIds.add(getResources().getString(R.string.nat_1));
                    //nativeAdIds.add(getResources().getString(R.string.nat_1));
                    //nativeAdIds.add(getResources().getString(R.string.nat_1));

                    nativeAdIds.add(getResources().getString(R.string.sep_todays_top));
                    nativeAdIds.add(getResources().getString(R.string.sep_todays_middle));
                    nativeAdIds.add(getResources().getString(R.string.sep_todays_bottom));


                    originalGamesList=response.body().getData();
                    Log.d("pagenation","Total game size: "+originalGamesList.size());
                    int totalSize=originalGamesList.size();
                    for(int i=0;i<originalGamesList.size();i++){
                        originalGamesList.get(i).setCount(totalSize-i);
                    }
                    totalPageCount=(int)(Math.ceil(originalGamesList.size()/4));
                    Log.d("pagenation","Total page count: "+totalPageCount);
                    currentPage=0;
                    for(int i=0;i<4;i++){
                        try{
                            filteredGamesList.add(originalGamesList.get((4*currentPage)+i));
                        }catch(Exception e){
                            Log.d("pagenation","This is a silly exception");
                        }
                    }
                    recyclerView.setAdapter(new GamesAdapter(getActivity(),filteredGamesList,nativeAdIds,getResources().getString(R.string.todays_banner)));
                    recyclerView.getAdapter().notifyDataSetChanged();

                }
            }

            @Override
            public void onFailure(Call<GameResponse> call, Throwable t) {
                Log.d("awesome","Got failure in loading previous_1.5: "+t.getLocalizedMessage());
            }
        });


        return rootView;
    }

    private void showOptInVideo(String adId) {
        AdConfig adConfig=new AdConfig(adId);
        presageOptinVideo=new PresageOptinVideo(getActivity(),adConfig);
        presageOptinVideo.setOptinVideoCallback(new PresageOptinVideoCallback() {
            @Override
            public void onAdRewarded(RewardItem rewardItem) {
                Log.d("presage","presage opt in reward received");
            }

            @Override
            public void onAdAvailable() {
                Log.d("presage","presage opt in ad available");
            }

            @Override
            public void onAdNotAvailable() {
                Log.d("presage","presage opt in ad not available");
            }

            @Override
            public void onAdLoaded() {
                Log.d("presage","presage opt in ad loaded");
                if(presageOptinVideo.isLoaded()){
                    presageOptinVideo.show();
                }
            }

            @Override
            public void onAdNotLoaded() {
                Log.d("presage","presage opt in ad not loaded");
            }

            @Override
            public void onAdDisplayed() {
                Log.d("presage","presage opt in ad displayed");
            }

            @Override
            public void onAdClosed() {
                Log.d("presage","presage opt in ad closed");
            }

            @Override
            public void onAdError(int i) {
                Log.d("presage","presage opt in ad error");
            }
        });
        presageOptinVideo.load();
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

}