package com.football.predictions;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.appmediation.sdk.AMInterstitial;
import com.appmediation.sdk.AMSDK;
import com.appmediation.sdk.listeners.AMListener;
import com.appmediation.sdk.models.AMError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    /*private InterstitialAd interstitialAd;
    private InterstitialAd interstitialAd60;
    private com.google.android.gms.ads.InterstitialAd admobInterstitial;
    private PresageInterstitial presageInterstitial;*/
    private Handler handler;
    private BillingClient mBillingClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(com.football.predictions.R.anim.slide_in, R.anim.slide_out);
        super.onCreate(savedInstanceState);

        mBillingClient = BillingClient.newBuilder(MainActivity.this).setListener(new PurchasesUpdatedListener() {
            @Override
            public void onPurchasesUpdated(int responseCode, @Nullable List<Purchase> purchases) {
                Log.d("awesome","Purchase updated: "+responseCode);
            }
        }).build();

        mBillingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@BillingClient.BillingResponse int billingResponseCode) {
                if (billingResponseCode == BillingClient.BillingResponse.OK) {
                    // The billing client is ready. You can query purchases here.
                    List skuList = new ArrayList<>();
                    skuList.add("vip_access_weekly");
                    SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
                    params.setSkusList(skuList).setType(BillingClient.SkuType.SUBS);
                    mBillingClient.querySkuDetailsAsync(params.build(),
                            new SkuDetailsResponseListener() {
                                @Override
                                public void onSkuDetailsResponse(int responseCode, List<SkuDetails> skuDetailsList) {
                                    // Process the result.
                                    if (responseCode == BillingClient.BillingResponse.OK
                                            && skuDetailsList != null) {
                                        for (SkuDetails skuDetails : skuDetailsList) {
                                            String sku = skuDetails.getSku();
                                            String price = skuDetails.getPrice();
                                            Log.d("awesome","SKU: "+sku+" price: "+price);
                                        }
                                    }
                                }
                            });
                }else{
                    Log.d("awesome","billingResponseError: "+billingResponseCode);
                }
            }
            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        });
        MobileAds.initialize(this, getResources().getString(R.string.admob_app_id));
        AMSDK.init(this, getResources().getString(R.string.app_mediation_app_key));


        AMInterstitial.setListener(new AMListener() {
            @Override
            public void onLoaded() {
                Log.d("awesome", "onLoaded");
            }
            @Override
            public void onFailed(AMError error) {
                Log.d("awesome", "onFailed: " + error.name());
            }

            @Override
            public void onShowed() {
                Log.d("awesome", "onShowed");
            }

            @Override
            public void onClosed() {
                Log.d("awesome", "onClosed");
            }

            @Override
            public void onClicked() {
                Log.d("awesome", "onClicked");
            }
        });

        //Presage.getInstance().start("272929", this); // this = current activity
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("Previous Matches");

        FirebaseMessaging.getInstance().subscribeToTopic("all")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Log.d("awesome","successfully suscribed to topic");
                        }else{
                            Log.d("awesome","Error while suscribing to topic");
                        }
                    }
                });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        navigationView.getMenu().getItem(0).setChecked(true);

        if(!isConnected(MainActivity.this)) buildDialog(MainActivity.this).show();
        else {
            previous previous=new previous();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.content_main, previous, "previous").commit();
            handler=new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    AMInterstitial.show(MainActivity.this);
                }
            },15000);

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showAppMediationRepeatingInterstitial(60000);
                }
            },60000);



            /*handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //showInterstitialAd(getResources().getString(R.string.inter_15_sec)); //main activity interstitial
                    //showInterstitialAd(getResources().getString(R.string.inter_1));
                    showInterstitialAd(getResources().getString(R.string.oct_2_min_interstitial));
                }
            },120000);

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showAdmobInterstitial(getResources().getString(R.string.oct_admob_interstitial));

                }
            },60000);

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //showInterstitialAd60(getResources().getString(R.string.fb_inter_60_sec));
                    //showInterstitialAd60(getResources().getString(R.string.inter_1));
                    showInterstitialAd60(getResources().getString(R.string.oct_4_min_interstitial));
                }
            },240000);

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showPresageInterestitial(getResources().getString(R.string.presage_interstitial));
                }
            },30000);*/
        }
    }

    private void startPurchase(){
        BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                .setSku("gas")
                .setType(BillingClient.SkuType.INAPP) // SkuType.SUB for subscription
                .build();
        int responseCode = mBillingClient.launchBillingFlow(MainActivity.this,flowParams);
    }

    private void showAppMediationRepeatingInterstitial(final long repeatDuration){
        AMInterstitial.show(MainActivity.this);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showAppMediationRepeatingInterstitial(repeatDuration);
            }
        },repeatDuration);
    }

    /*private void showInterstitialAd60(final String adId) {
        interstitialAd60 = new InterstitialAd(this, adId);
        interstitialAd60.setAdListener(new AbstractAdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                super.onError(ad, adError);
                if(adError.getErrorCode()==AdError.NO_FILL_ERROR_CODE){
                    handler=new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showInterstitialAd60(adId);
                        }
                    },60000);
                }
                Log.d("awesome","Main activity interestitial ad error: "+adError.getErrorCode()+":-"+adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                super.onAdLoaded(ad);
                if(interstitialAd60.isAdLoaded()){
                    interstitialAd60.show();

                }

                Log.d("awesome","Main activity interestitial ad loaded: "+ad);
            }

            @Override
            public void onAdClicked(Ad ad) {
                super.onAdClicked(ad);
                Log.d("awesome","Main activity interestitial ad clicked: "+ad);
            }

            @Override
            public void onInterstitialDisplayed(Ad ad) {
                super.onInterstitialDisplayed(ad);
                Log.d("awesome","Main activity interestitial ad displayed: "+ad);
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                super.onInterstitialDismissed(ad);
                Log.d("awesome","Main activity interestitial ad dismissed: "+ad);
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                super.onLoggingImpression(ad);
                Log.d("awesome","Main activity interestitial ad impression: "+ad);
            }
        });
        interstitialAd60.loadAd();
    }

    private void showPresageInterestitial(final String adId) {
        presageInterstitial=new PresageInterstitial(this);
        presageInterstitial.setInterstitialCallback(new PresageInterstitialCallback() {
            @Override
            public void onAdAvailable() {
                Log.d("awesome","presage interstitial available");
            }

            @Override
            public void onAdNotAvailable() {
                Log.d("awesome","presage interstitial ad not available");
            }

            @Override
            public void onAdLoaded() {
                Log.d("awesome","presage interstitial ad loaded");
                if(presageInterstitial.isLoaded()){
                    presageInterstitial.show();
                }
            }

            @Override
            public void onAdNotLoaded() {
                Log.d("awesome","presage interestitial ad not loaded");
            }

            @Override
            public void onAdDisplayed() {
                Log.d("awesome","presage interestitial ad displayed");
            }

            @Override
            public void onAdClosed() {
                Log.d("awesome","presage interstitial ad closed");
            }

            @Override
            public void onAdError(int i) {
                Log.d("awesome","presage interstitial ad error: "+i);
                handler=new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showPresageInterestitial(adId);
                    }
                },30000);
            }
        });
        presageInterstitial.load();
    }*/

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_share) {
            //Toast.makeText(getBaseContext(), "not ready",Toast.LENGTH_LONG).show();
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBodyText = "This App Has Helped me win thousands on SportPesa.\n" +
                    "It gives daily SURE BETS FOR FREE.\n" +
                    "Download it here; \n" +
                    "https://play.google.com/store/apps/details?id=com.football.predictions\n";
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,"Football Predictions");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBodyText);
            startActivity(Intent.createChooser(sharingIntent, "Share Football Predictions app With Friends"));

            return true;
        }else if (id == R.id.action_about) {

            if(!isConnected(MainActivity.this)) buildDialog(MainActivity.this).show();
            else {   startActivity(new Intent(MainActivity.this,about.class));   }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.previous_matches) {
            Fragment fragment=getSupportFragmentManager().findFragmentByTag("previous");
            if(fragment==null){
                if(!isConnected(MainActivity.this)) buildDialog(MainActivity.this).show();
                else {
                    setTitle("Previous Matches");
                    previous Previous = new previous();
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.content_main, Previous, "previous").commit();
                    AMInterstitial.show(MainActivity.this);
                    //showInterstitialAd(getResources().getString(R.string.previous_inter)); //previous interstitial
                    //showInterstitialAd(getResources().getString(R.string.inter_1));
                }
            }
        }

        else if (id == R.id.todays_matches) {
            Fragment fragment=getSupportFragmentManager().findFragmentByTag("todays");
            if(fragment==null){
                if(!isConnected(MainActivity.this)) buildDialog(MainActivity.this).show();
                else {
                    setTitle("Today's Matches");
                    todays Todays = new todays();
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.content_main, Todays, "todays").commit();
                    AMInterstitial.show(MainActivity.this);
                    //showInterstitialAd(getResources().getString(R.string.todays_inter)); //todays interstitial
                    //showInterstitialAd(getResources().getString(R.string.inter_1));
                }
            }
        }

        else if (id == R.id.nav_rateus) {


            if(!isConnected(MainActivity.this)) buildDialog(MainActivity.this).show();
            else {

                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }


        }
        else if (id == R.id.nav_share) {

            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBodyText = "This App Has Helped me win thousands on SportPesa.\n" +
                    "It gives daily SURE BETS FOR FREE.\n" +
                    "Download it here; \n" +
                    "https://play.google.com/store/apps/details?id=com.football.predictions\n";
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,"Football Predictions");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBodyText);
            startActivity(Intent.createChooser(sharingIntent, "Share Football Predictions app With Friends"));


        }
        else if (id == R.id.nav_about) {
            if(!isConnected(MainActivity.this)) buildDialog(MainActivity.this).show();
            else {
                startActivity(new Intent(MainActivity.this, about.class));
                //showInterstitialAd("342304149587187_356787844805484"); //about interstitial
            }

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /*private void showInterstitialAd(final String interstitialAdId) {
        interstitialAd = new InterstitialAd(this, interstitialAdId);
        interstitialAd.setAdListener(new AbstractAdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                super.onError(ad, adError);
                if(adError.getErrorCode()==AdError.NO_FILL_ERROR_CODE){
                    handler=new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showInterstitialAd(interstitialAdId);
                        }
                    },30000);
                }
                Log.d("awesome","Main activity interestitial ad error: "+adError.getErrorCode()+":-"+adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                super.onAdLoaded(ad);
                try{
                    interstitialAd.show();
                }catch (Exception ignored){}

                Log.d("awesome","Main activity interestitial ad loaded: "+ad);
            }

            @Override
            public void onAdClicked(Ad ad) {
                super.onAdClicked(ad);
                Log.d("awesome","Main activity interestitial ad clicked: "+ad);
            }

            @Override
            public void onInterstitialDisplayed(Ad ad) {
                super.onInterstitialDisplayed(ad);
                Log.d("awesome","Main activity interestitial ad displayed: "+ad);
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                super.onInterstitialDismissed(ad);
                Log.d("awesome","Main activity interestitial ad dismissed: "+ad);
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                super.onLoggingImpression(ad);
                Log.d("awesome","Main activity interestitial ad impression: "+ad);
            }
        });
        interstitialAd.loadAd();
    }

    private void showAdmobInterstitial(final String adId){
        admobInterstitial=new com.google.android.gms.ads.InterstitialAd(this);
        admobInterstitial.setAdUnitId(adId);
        AdRequest adRequest=new AdRequest.Builder().addTestDevice("493C01A0BB55FCED482A594F8E4023E0").build();
        admobInterstitial.loadAd(adRequest);
        admobInterstitial.setAdListener(new AdListener(){
            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                Log.d("awesome","Failed to load admob interstitial: "+i);
                handler=new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showAdmobInterstitial(adId);
                    }
                },60000);
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                Log.d("awesome","Admob interstitial loaded");
                admobInterstitial.show();
            }
        });
    }*/


    public boolean isConnected(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();

        if (netinfo != null && netinfo.isConnectedOrConnecting()) {
            android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if((mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting())) return true;
            else return false;
        } else
            return false;
    }


    public AlertDialog.Builder buildDialog(Context c) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setIcon(R.drawable.ic_error);
        builder.setTitle("Internet Connection Lost");
        builder.setMessage("You need to have Mobile Data or wifi to access this.");

        builder.setPositiveButton("REMAIN", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {


            }
        });

        builder.setNegativeButton("EXIT", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                finish();
                System.exit(0);
            }
        });
        return builder;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);

    }
}
