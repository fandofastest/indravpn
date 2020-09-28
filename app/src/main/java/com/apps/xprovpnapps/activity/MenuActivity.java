package com.apps.xprovpnapps.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.apps.xprovpnapps.xpro.R;

import butterknife.ButterKnife;

public class MenuActivity extends AppCompatActivity {

    ImageView  ivfaq, ivShare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        ButterKnife.bind(this);
        ivfaq = findViewById(R.id.imgfaq);
        ivShare = findViewById(R.id.imgshare);

        ivfaq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MenuActivity.this, Faq.class));
            }


        });

        ivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ishare = new Intent(Intent.ACTION_SEND);
                ishare.setType("text/plain");
                String sAux = "\n" + getResources().getString(R.string.app_name) + "\n\n";
                sAux = sAux + "https://play.google.com/store/apps/details?id=" + getApplication().getPackageName();
                ishare.putExtra(Intent.EXTRA_TEXT, sAux);
                startActivity(Intent.createChooser(ishare, "choose one"));
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarold);
        toolbar.setTitle("X-pro VPN");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}

