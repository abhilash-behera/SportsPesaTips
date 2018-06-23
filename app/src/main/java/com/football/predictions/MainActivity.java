package com.football.predictions;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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

import com.facebook.ads.AbstractAdListener;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAd;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private InterstitialAd interstitialAd;
    private com.google.android.gms.ads.InterstitialAd admobInterstitial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(com.football.predictions.R.anim.slide_in, R.anim.slide_out);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("Previous Matches");

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
            Handler handler=new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showInterstitialAd(getResources().getString(R.string.inter_15_sec)); //main activity interstitial
                }
            },15000);

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showAdmobInterstitial(getResources().getString(R.string.admob_interstitial));
                }
            },60000);
        }
    }

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
                    showInterstitialAd(getResources().getString(R.string.previous_inter)); //previous interstitial
                }
            }
        } else if(id==R.id.previous_1_5){
            Fragment fragment=getSupportFragmentManager().findFragmentByTag("previous_1_5");
            if(fragment==null){
                if(!isConnected(MainActivity.this)) buildDialog(MainActivity.this).show();
                else{
                    setTitle(getResources().getString(R.string.previous_1_5));
                    previous_1_5 previous_1_5=new previous_1_5();
                    FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.content_main,previous_1_5,"previous_1_5").commit();
                    showInterstitialAd(getResources().getString(R.string.previous_1_5_inter));
                }
            }
        }else if(id==R.id.previous_2_5){
            Fragment fragment=getSupportFragmentManager().findFragmentByTag("previous_2_5");
            if(fragment==null){
                if(!isConnected(MainActivity.this)) buildDialog(MainActivity.this).show();
                else{
                    setTitle(getResources().getString(R.string.previous_2_5));
                    previous_2_5 previous_2_5=new previous_2_5();
                    FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.content_main,previous_2_5,"previous_2_5").commit();
                    showInterstitialAd(getResources().getString(R.string.previous_2_5_inter));
                }
            }
        }else if(id==R.id.previous_3_5){
            Fragment fragment=getSupportFragmentManager().findFragmentByTag("previous_3_5");
            if(fragment==null){
                if(!isConnected(MainActivity.this)) buildDialog(MainActivity.this).show();
                else{
                    setTitle(getResources().getString(R.string.previous_3_5));
                    previous_3_5 previous_3_5=new previous_3_5();
                    FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.content_main,previous_3_5,"previous_3_5").commit();
                    showInterstitialAd(getResources().getString(R.string.previous_3_5_inter));
                }
            }
        }else if(id==R.id.previous_sure){
            Fragment fragment=getSupportFragmentManager().findFragmentByTag("previous_sure");
            if(fragment==null){
                if(!isConnected(MainActivity.this)) buildDialog(MainActivity.this).show();
                else{
                    setTitle(getResources().getString(R.string.previous_sure));
                    previous_sure_tips previous_sure_tips=new previous_sure_tips();
                    FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.content_main,previous_sure_tips,"previous_sure").commit();
                    showInterstitialAd(getResources().getString(R.string.previous_sure_inter));
                }
            }
        }else if(id==R.id.previous_vip){
            Fragment fragment=getSupportFragmentManager().findFragmentByTag("previous_vip");
            if(fragment==null){
                if(!isConnected(MainActivity.this)) buildDialog(MainActivity.this).show();
                else{
                    setTitle(getResources().getString(R.string.previous_vip));
                    previous_vip previous_vip=new previous_vip();
                    FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.content_main,previous_vip,"previous_vip").commit();
                    showInterstitialAd(getResources().getString(R.string.previous_vip_inter));
                }
            }
        }else if (id == R.id.todays_matches) {
            Fragment fragment=getSupportFragmentManager().findFragmentByTag("todays");
            if(fragment==null){
                if(!isConnected(MainActivity.this)) buildDialog(MainActivity.this).show();
                else {
                    setTitle("Today's Matches");
                    todays Todays = new todays();
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.content_main, Todays, "todays").commit();
                    showInterstitialAd(getResources().getString(R.string.todays_inter)); //todays interstitial
                }
            }
        } else if(id==R.id.todays_1_5){
            Fragment fragment=getSupportFragmentManager().findFragmentByTag("todays_1_5");
            if(fragment==null){
                if(!isConnected(MainActivity.this)) buildDialog(MainActivity.this).show();
                else{
                    setTitle(getResources().getString(R.string.todays_1_5));
                    todays_1_5 todays_1_5=new todays_1_5();
                    FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.content_main,todays_1_5,"todays_1_5").commit();
                    showInterstitialAd(getResources().getString(R.string.todays_1_5_inter));
                }
            }
        }else if(id==R.id.todays_2_5){
            Fragment fragment=getSupportFragmentManager().findFragmentByTag("todays_2_5");
            if(fragment==null){
                if(!isConnected(MainActivity.this)) buildDialog(MainActivity.this).show();
                else{
                    setTitle(getResources().getString(R.string.todays_2_5));
                    todays_2_5 todays_2_5=new todays_2_5();
                    FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.content_main,todays_2_5,"todays_2_5").commit();
                    showInterstitialAd(getResources().getString(R.string.todays_2_5_inter));
                }
            }
        }else if(id==R.id.todays_3_5){
            Fragment fragment=getSupportFragmentManager().findFragmentByTag("todays_3_5");
            if(fragment==null){
                if(!isConnected(MainActivity.this)) buildDialog(MainActivity.this).show();
                else{
                    setTitle(getResources().getString(R.string.todays_3_5));
                    todays_3_5 todays_3_5=new todays_3_5();
                    FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.content_main,todays_3_5,"todays_3_5").commit();
                    showInterstitialAd(getResources().getString(R.string.todays_3_5_inter));
                }
            }
        }else if(id==R.id.todays_vip){
            Fragment fragment=getSupportFragmentManager().findFragmentByTag("todays_vip");
            if(fragment==null){
                if(!isConnected(MainActivity.this)) buildDialog(MainActivity.this).show();
                else{
                    setTitle(getResources().getString(R.string.todays_vip));
                    todays_vip todays_vip=new todays_vip();
                    FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.content_main,todays_vip,"todays_vip").commit();
                    showInterstitialAd(getResources().getString(R.string.todays_vip_inter));
                }
            }
        }else if(id==R.id.sure_tips){
            Fragment fragment=getSupportFragmentManager().findFragmentByTag("sure_tips");
            if(fragment==null){
                if(!isConnected(MainActivity.this)) buildDialog(MainActivity.this).show();
                else{
                    setTitle(getResources().getString(R.string.sure_tips));
                    sure_tips sure_tips=new sure_tips();
                    FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.content_main,sure_tips,"sure_tips").commit();
                    showInterstitialAd(getResources().getString(R.string.todays_sure_inter));
                }
            }
        }else if(id==R.id.tomorrows_matches){
            Fragment fragment=getSupportFragmentManager().findFragmentByTag("tomorrows_matches");
            if(fragment==null){
                if(!isConnected(MainActivity.this)) buildDialog(MainActivity.this).show();
                else{
                    setTitle(getResources().getString(R.string.tomorrows_matches));
                    tomorrows tomorrows=new tomorrows();
                    FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.content_main,tomorrows,"tomorrows_matches").commit();
                    showInterstitialAd(getResources().getString(R.string.tomorrows_inter));
                }
            }
        }else if(id==R.id.tomorrows_1_5){
            Fragment fragment=getSupportFragmentManager().findFragmentByTag("tomorrows_1_5");
            if(fragment==null){
                if(!isConnected(MainActivity.this)) buildDialog(MainActivity.this).show();
                else{
                    setTitle(getResources().getString(R.string.tomorrows_1_5));
                    tomorrows_1_5 tomorrows_1_5=new tomorrows_1_5();
                    FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.content_main,tomorrows_1_5,"tomorrows_1_5").commit();
                    showInterstitialAd(getResources().getString(R.string.tomorrows_1_5_inter));
                }
            }
        }else if(id==R.id.tomorrows_2_5){
            Fragment fragment=getSupportFragmentManager().findFragmentByTag("tomorrows_2_5");
            if(fragment==null){
                if(!isConnected(MainActivity.this)) buildDialog(MainActivity.this).show();
                else{
                    setTitle(getResources().getString(R.string.tomorrows_2_5));
                    tomorrows_2_5 tomorrows_2_5=new tomorrows_2_5();
                    FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.content_main,tomorrows_2_5,"tomorrows_2_5").commit();
                    showInterstitialAd(getResources().getString(R.string.tomorrows_2_5_inter));
                }
            }
        }else if(id==R.id.tomorrows_3_5){
            Fragment fragment=getSupportFragmentManager().findFragmentByTag("tomorrows_3_5");
            if(fragment==null){
                if(!isConnected(MainActivity.this)) buildDialog(MainActivity.this).show();
                else{
                    setTitle(getResources().getString(R.string.tomorrows_3_5));
                    tomorrows_3_5 tomorrows_3_5=new tomorrows_3_5();
                    FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.content_main,tomorrows_3_5,"tomorrows_3_5").commit();
                    showInterstitialAd(getResources().getString(R.string.tomorrows_3_5_inter));
                }
            }
        }else if(id==R.id.tomorrows_vip){
            Fragment fragment=getSupportFragmentManager().findFragmentByTag("tomorrows_vip");
            if(fragment==null){
                if(!isConnected(MainActivity.this)) buildDialog(MainActivity.this).show();
                else{
                    setTitle(getResources().getString(R.string.tomorrows_vip));
                    tomorrows_vip tomorrows_vip=new tomorrows_vip();
                    FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.content_main,tomorrows_vip,"tomorrows_vip").commit();
                    showInterstitialAd(getResources().getString(R.string.tomorrows_vip_inter));
                }
            }
        }else if (id == R.id.nav_rateus) {


            if(!isConnected(MainActivity.this)) buildDialog(MainActivity.this).show();
            else {

                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }


        }  else if (id == R.id.nav_share) {

            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBodyText = "This App Has Helped me win thousands on SportPesa.\n" +
                    "It gives daily SURE BETS FOR FREE.\n" +
                    "Download it here; \n" +
                    "https://play.google.com/store/apps/details?id=com.football.predictions\n";
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,"Football Predictions");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBodyText);
            startActivity(Intent.createChooser(sharingIntent, "Share Football Predictions app With Friends"));


        } else if (id == R.id.nav_about) {
            if(!isConnected(MainActivity.this)) buildDialog(MainActivity.this).show();
            else {
                startActivity(new Intent(MainActivity.this, about.class));
                showInterstitialAd("342304149587187_356787844805484"); //about interstitial
            }

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showInterstitialAd(final String interstitialAdId) {
        interstitialAd = new InterstitialAd(this, interstitialAdId);
        interstitialAd.setAdListener(new AbstractAdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                super.onError(ad, adError);
                if(adError.getErrorCode()==AdError.NO_FILL_ERROR_CODE){
                    Handler handler=new Handler();
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
                Handler handler=new Handler();
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
    }


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
        if(interstitialAd!=null){
            interstitialAd.destroy();
        }

    }
}
