package com.example.firealert;

import android.content.Intent;
import android.os.Bundle;

import com.example.firealert.Adapter.ViewPageAdapter;
import com.example.firealert.FragmentTips1;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class TipsActivity extends AppCompatActivity {
    ViewPager viewPager;
    ImageButton btn_back;
    ViewPageAdapter viewPageAdapter;
    DotsIndicator dotsIndicator;
    ImageButton btnBack;
    ImageButton btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tips);
        Toolbar toolbar = findViewById(R.id.toolbar);
        viewPager= findViewById(R.id.viewpager_tips);
        btnBack= findViewById(R.id.btnBack);
        btnNext= findViewById(R.id.btnNext);
        dotsIndicator = findViewById(R.id.dots_indicator);

        getTab();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewPager.getCurrentItem()==0)
                {
                    startActivity(new Intent(TipsActivity.this,HomeActivity.class));
                    finish();
                }
                else
                {
                    viewPager.setCurrentItem(viewPager.getCurrentItem()-1);
                }
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewPager.getCurrentItem()!=2)
                {
                    viewPager.setCurrentItem(viewPager.getCurrentItem()+1);
                }
                else
                {
                    startActivity(new Intent(TipsActivity.this,HomeActivity.class));
                    finish();
                }
            }
        });


        btn_back = (ImageButton) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    private void getTab()
    {
        viewPageAdapter = new ViewPageAdapter(getSupportFragmentManager());
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                viewPageAdapter.addFragment(FragmentTips1.getInstance(),"1");
                viewPager.setAdapter(viewPageAdapter);
                dotsIndicator.setViewPager(viewPager);
            }
        });
    }
}