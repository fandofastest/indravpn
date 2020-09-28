package com.apps.xprovpnapps.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.apps.xprovpnapps.Apppurchaseconfig;
import com.apps.xprovpnapps.xpro.R;

public class SubscriptionActivity extends AppCompatActivity implements View.OnClickListener, BillingProcessor.IBillingHandler {

    private Button mAdsBuy;
    private Button mVipBuy;
    private Button mAllBuy;

    private BillingProcessor bp;

    private int ads_check = -1;
    private int vip_check = -1;
    private int all_check = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription);

        bp = new BillingProcessor(this, Apppurchaseconfig.IAP_LISENCE_KEY, this);
        bp.initialize();

        mAdsBuy = findViewById(R.id.ads_pur);
        mVipBuy = findViewById(R.id.vip_pur);
        mAllBuy = findViewById(R.id.all_pur);

        mAdsBuy.setOnClickListener(this);
        mVipBuy.setOnClickListener(this);
        mAllBuy.setOnClickListener(this);


        Toolbar toolbar =  findViewById(R.id.toolbarold);
        toolbar.setTitle("Premium Packages");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        if (bp != null) {
            bp.release();
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.ads_pur:
                showAdsSubsDialog();
                break;

            case R.id.vip_pur:
                showVipSubsDialog();
                break;

            case R.id.all_pur:
                showAllSubsDialog();
                break;

            default:
                break;
        }
    }

    private void showAdsSubsDialog() {

        final CharSequence[] items = {getString(R.string.ads_price_monthly)};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Subscription Package");
        builder.setCancelable(false);

        builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ads_check = which;
            }
        });

        builder.setPositiveButton("Subscribe", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                remove_ads(ads_check);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showVipSubsDialog() {

        final CharSequence[] items = {
                getString(R.string.top_package_month),
                getString(R.string.top_package_6month),
                getString(R.string.top_package_12month)
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Subscription Package");
        builder.setCancelable(false);


        builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                vip_check = which;
            }
        });

        builder.setPositiveButton("Subscribe", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                unlock_vip(vip_check);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showAllSubsDialog() {

        final CharSequence[] items = {
                getString(R.string.both_package_month),
                getString(R.string.both_package_6month),
                getString(R.string.both_package_12month)
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Subscription Package");
        builder.setCancelable(false);

        builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                all_check = which;
            }
        });

        builder.setPositiveButton("Subscribe", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                unlock_all(all_check);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {

    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
    }

    @Override
    public void onBillingInitialized() {
    }

    private void remove_ads(int i) {
        if (i == 0) {
            bp.subscribe(SubscriptionActivity.this, Apppurchaseconfig.ads_subscription_id);
        }
    }

    private void unlock_vip(int i) {
        switch (i) {
            case 0:
                bp.subscribe(SubscriptionActivity.this, Apppurchaseconfig.top_month_subscription_id);
                break;
            case 1:
                bp.subscribe(SubscriptionActivity.this, Apppurchaseconfig.top_6months_subscription_id);
                break;
            case 2:
                bp.subscribe(SubscriptionActivity.this, Apppurchaseconfig.top_year_subscription_id);
                break;
        }
    }

    private void unlock_all(int i) {
        switch (i) {
            case 0:
                bp.subscribe(SubscriptionActivity.this, Apppurchaseconfig.both_subscription_month_id);
                break;
            case 1:
                bp.subscribe(SubscriptionActivity.this, Apppurchaseconfig.both_subscription_6months_id);
                break;
            case 2:
                bp.subscribe(SubscriptionActivity.this, Apppurchaseconfig.both_subscription_year_id);
                break;
        }
    }
}
