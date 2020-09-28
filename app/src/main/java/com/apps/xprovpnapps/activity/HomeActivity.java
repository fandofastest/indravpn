package com.apps.xprovpnapps.activity;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anchorfree.hydrasdk.HydraSdk;
import com.anchorfree.hydrasdk.api.response.RemainingTraffic;
import com.anchorfree.hydrasdk.callbacks.Callback;
import com.anchorfree.hydrasdk.exceptions.HydraException;
import com.anchorfree.hydrasdk.vpnservice.VPNState;
import com.apps.xprovpnapps.Ads;
import com.apps.xprovpnapps.Apppurchaseconfig;
import com.bumptech.glide.Glide;
import com.apps.xprovpnapps.utils.Convert;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.google.android.material.navigation.NavigationView;
import com.onesignal.OneSignal;
import com.apps.xprovpnapps.xpro.R;
import com.pixplicity.easyprefs.library.Prefs;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


import static com.apps.xprovpnapps.activity.MainActivity.selectedCountry;
public abstract class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    protected static final String TAG = MainActivity.class.getSimpleName();

    VPNState state;
    int progressBarValue = 0;
    Handler handler = new Handler();
    private Handler customHandler = new Handler();

    public static Menu menuItem;

    @BindView(R.id.toolbar)
    protected Toolbar toolbar;


    @BindView(R.id.connect_btn)
    ImageView connectBtnTextView;


    @BindView(R.id.connection_state)
    ImageView connectionStateTextView;

    @BindView(R.id.connection_progress)
    ProgressBar connectionProgressBar;

    LinearLayout adviewly;

    private String STATUS;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        adviewly=findViewById(R.id.adview);

        Ads ads  = new Ads(HomeActivity.this,false);
        Display display =getWindowManager().getDefaultDisplay();
        ads.ShowBannerAds(adviewly,display);

        ButterKnife.bind(this);
        OneSignal.startInit(this).init();
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ImageView img_quick=findViewById(R.id.imgquick);
        img_quick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectToVpn();
            }
        });
        ImageView img_country = findViewById(R.id.imgcountry);
        img_country.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(HomeActivity.this, ServersActivity.class));
            }
        });
        ImageView img_menu=findViewById(R.id.imgmenu);
        img_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, MenuActivity.class));

            }
        });
        ImageView img_rate = findViewById(R.id.imgrate);
        img_rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("market://details?id=" + HomeActivity.this.getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=" + HomeActivity.this.getPackageName())));
                }
            }


        });




        if (Prefs.contains("connectStart") && Prefs.getString("connectStart", "").equals("on")) {

            isConnected(new Callback<Boolean>() {
                @Override
                public void success(@NonNull Boolean aBoolean) {
                    if (aBoolean) {
                        STATUS = "Disconnect";

                        disconnectAlert();





                    } else {

                        updateUI();
                        connectToVpn();


                    }
                }

                @Override
                public void failure(@NonNull HydraException e) {
                    Toast.makeText(getApplicationContext(), "" + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }

        if (Prefs.contains("noti") && Prefs.getString("noti", "off").equals("off")) {
            OneSignal.setSubscription(false);
        } else if (Prefs.contains("noti") && Prefs.getString("noti", "off").equals("on")) {
            OneSignal.setSubscription(true);
        } else {
            OneSignal.setSubscription(false);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
//        STATUS="Connect";
//        Ads ads = new Ads(HomeActivity.this,true);
//        ads.setCustomObjectListener(new Ads.MyCustomObjectListener() {
//            @Override
//            public void onAdsfinish() {
//                if (STATUS.equals("Connect")) {
//                    updateUI();
//                    connectToVpn();
//                    loadAdAgain();
//                } else if (STATUS.equals("Disconnect")) {
//                    disconnectAlert();
//                    loadAdAgain();
//                }
//            }
//
//            @Override
//            public void onRewardOk() {
//
//            }
//        });

    }
    private void loadAdAgain() {
//        load Ads for multiple times in background
//        if (getResources().getBoolean(R.bool.ads_switch)) {
//            mInterstitialAd.loadAd(new AdRequest.Builder().build());
//        }
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


        getMenuInflater().inflate(R.menu.main, menu);

        menuItem = menu;
        if (selectedCountry != null)
            if (!selectedCountry.equalsIgnoreCase(""))

                menuItem.findItem(R.id.action_glob).setIcon(this.getResources().getIdentifier(selectedCountry.toLowerCase(), "drawable", this.getPackageName()));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        if (id == R.id.action_glob) {

            startActivity(new Intent(this, ServersActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_upgrade) {
            startActivity(new Intent(this,ServersActivity.class));
        } else if (id == R.id.nav_rate) {
            rateUs();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private Handler mUIHandler = new Handler(Looper.getMainLooper());
    final Runnable mUIUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            updateUI();
            checkRemainingTraffic();
            mUIHandler.postDelayed(mUIUpdateRunnable, 10000);
        }
    };
    @Override
    protected void onResume() {
//        if the application again available from background state...
        super.onResume();
        isConnected(new Callback<Boolean>() {
            @Override
            public void success(@NonNull Boolean aBoolean) {
                if (aBoolean) {
                    startUIUpdateTask();
                }
            }

            @Override
            public void failure(@NonNull HydraException e) {

            }
        });
    }
    @Override
    protected void onPause() {
//        application in the background state...
        super.onPause();
        stopUIUpdateTask();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }
    protected abstract void loginToVpn();

    @OnClick(R.id.connect_btn)
    public void onConnectBtnClick(View v) {
        isConnected(new Callback<Boolean>() {
            @Override
            public void success(@NonNull Boolean aBoolean) {
                if (aBoolean) {
                    STATUS = "Disconnect";
                    Ads ads = new Ads(HomeActivity.this,true);
                    ads.setCustomObjectListener(new Ads.MyCustomObjectListener() {
                        @Override
                        public void onAdsfinish() {
                            disconnectAlert();
                        }

                        @Override
                        public void onRewardOk() {

                        }
                    });


                } else {

                    STATUS = "Connect";
                    Ads ads = new Ads(HomeActivity.this,true);
                    ads.setCustomObjectListener(new Ads.MyCustomObjectListener() {
                        @Override
                        public void onAdsfinish() {
                            updateUI();
                            connectToVpn();
                        }

                        @Override
                        public void onRewardOk() {

                        }
                    });


                }
            }

            @Override
            public void failure(@NonNull HydraException e) {
                Toast.makeText(getApplicationContext(), "" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    protected abstract void isConnected(Callback<Boolean> callback);

    protected abstract void connectToVpn();

    protected abstract void disconnectFromVnp();

    protected abstract void chooseServer();

    protected abstract void getCurrentServer(Callback<String> callback);

    protected void startUIUpdateTask() {
        stopUIUpdateTask();
        mUIHandler.post(mUIUpdateRunnable);
    }

    protected void stopUIUpdateTask() {
        mUIHandler.removeCallbacks(mUIUpdateRunnable);
        updateUI();
    }

    protected abstract void checkRemainingTraffic();

    protected void updateUI() {
        HydraSdk.getVpnState(new Callback<VPNState>() {
            @Override
            public void success(@NonNull VPNState vpnState) {
                state = vpnState;
                switch (vpnState) {
                    case IDLE: {
                        loadIcon();
                        connectBtnTextView.setEnabled(true);
                        connectionStateTextView.setImageResource(R.drawable.disc);
                        hideConnectProgress();
                        break;
                    }
                    case CONNECTED: {
                        loadIcon();
                        connectBtnTextView.setEnabled(true);
                        connectionStateTextView.setImageResource(R.drawable.conne);
                        hideConnectProgress();
                        break;
                    }
                    case CONNECTING_VPN:
                    case CONNECTING_CREDENTIALS:
                    case CONNECTING_PERMISSIONS: {
                        loadIcon();
                        connectionStateTextView.setImageResource(R.drawable.connecting);
                        connectBtnTextView.setEnabled(false);
                        showConnectProgress();
                        break;
                    }
                    case PAUSED: {
                        connectBtnTextView.setBackgroundResource(R.drawable.ic_hare_connect);
                        break;
                    }
                }
            }

            @Override
            public void failure(@NonNull HydraException e) {

            }
        });

        getCurrentServer(new Callback<String>() {
            @Override
            public void success(@NonNull final String currentServer) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    }
                });
            }

            @Override
            public void failure(@NonNull HydraException e) {
            }
        });
    }

    protected void updateTrafficStats(long outBytes, long inBytes) {
        String outString = Convert.humanReadableByteCountOld(outBytes, false);
        String inString = Convert.humanReadableByteCountOld(inBytes, false);
    }

    protected void updateRemainingTraffic(RemainingTraffic remainingTrafficResponse) {
        if (remainingTrafficResponse.isUnlimited()) {
            //trafficLimitTextView.setText("UNLIMITED available");
        } else {
            String trafficUsed = Convert.megabyteCount(remainingTrafficResponse.getTrafficUsed()) + "Mb";
            String trafficLimit = Convert.megabyteCount(remainingTrafficResponse.getTrafficLimit()) + "Mb";
        }
    }

    protected void showConnectProgress() {

        connectionProgressBar.setVisibility(View.VISIBLE);

        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub

                while (state == VPNState.CONNECTING_VPN || state == VPNState.CONNECTING_CREDENTIALS) {
                    progressBarValue++;

                    handler.post(new Runnable() {

                        @Override
                        public void run() {

                            connectionProgressBar.setProgress(progressBarValue);


                        }
                    });
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    protected void hideConnectProgress() {
        connectionProgressBar.setVisibility(View.GONE);
        connectionStateTextView.setVisibility(View.VISIBLE);
    }

    protected void showMessage(String msg) {
        Toast.makeText(HomeActivity.this, msg, Toast.LENGTH_SHORT).show();
    }
    protected void rateUs()
    {
        Uri uri = Uri.parse("market://details?id=" + this.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/vpn/details?id=" + this.getPackageName())));
        }
    }
    protected void loadIcon() {
        if (state == VPNState.IDLE) {
            Glide.with(this).load(R.drawable.ic_hare_connect).into(connectBtnTextView);
            Glide.with(this).load(R.drawable.ic_hare_connect).into(connectBtnTextView);

        } else if (state == VPNState.CONNECTING_VPN || state == VPNState.CONNECTING_CREDENTIALS) {
            connectBtnTextView.setVisibility(View.VISIBLE);
            Glide.with(this).load(R.drawable.rabbit).into(connectBtnTextView);
        } else if (state == VPNState.CONNECTED) {
            Glide.with(this).load(R.drawable.ic_hare_connected).into(connectBtnTextView);
            Glide.with(this).load(R.drawable.ic_hare_connected).into(connectBtnTextView);
            connectBtnTextView.setVisibility(View.VISIBLE);

        }
    }

    protected void disconnectAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Do you want to disconnet?");

        builder.setPositiveButton("Disconnect",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        disconnectFromVnp();
                    }
                });
        builder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        builder.show();
    }






}
