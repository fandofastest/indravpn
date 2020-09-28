package com.apps.xprovpnapps.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.apps.xprovpnapps.Ads;
import com.apps.xprovpnapps.xpro.R;

import org.json.JSONException;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        getStatusApp(Ads.urlconfig);

    }

    private void getStatusApp(String url){

        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            Log.e("ress",response.toString());


            try {
                Ads.primaryads=response.getString("primaryads");
                Ads.modealternatif=response.getString("modealternatif");
                Ads.fanbanner=response.getString("fanbanner");
                Ads.faninter=response.getString("faninter");
                Ads.admobinter=response.getString("admobinter");
                Ads.admobbanner=response.getString("admobbanner");

                Button button= findViewById(R.id.buttonstart);
                ProgressBar progressBar =findViewById(R.id.progressbar);
                progressBar.setVisibility(View.INVISIBLE);
                button.setVisibility(View.VISIBLE);

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Ads ads = new Ads(SplashScreen.this);
                        ads.setCustomObjectListener(new Ads.MyCustomObjectListener() {
                            @Override
                            public void onAdsfinish() {
                                Intent intent = new Intent(SplashScreen.this,MainActivity.class);
                                startActivity(intent);
                                finish();
                            }

                            @Override
                            public void onRewardOk() {

                            }
                        });
                    }
                });














            } catch (JSONException e) {
                Log.e("errr",e.getMessage());
            }


        }, error -> {


            System.out.println("errrrrr"+error.getMessage());

        });

        Volley.newRequestQueue(getApplicationContext()).add(jsonObjectRequest);


    }
}
