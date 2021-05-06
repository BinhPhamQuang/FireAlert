package com.example.firealert;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.example.firealert.Adapter.ViewPageAdapter;
import com.google.android.material.tabs.TabLayout;

public class AccountActivity extends AppCompatActivity {
    ViewPager viewPager;
    ImageButton btn_back;
    ViewPageAdapter viewPageAdapter;
    TabLayout tab_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        Toolbar toolbar = findViewById(R.id.toolbar);
        viewPager = findViewById(R.id.viewpager_account);
        tab_layout= findViewById(R.id.tab_layout);
        getTab();

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
                viewPageAdapter.addFragment(FragmentAccountTab1.getInstance(),"Information");
                viewPageAdapter.addFragment(FragmentAccountTab2.getInstance(),"Account");
                viewPager.setAdapter(viewPageAdapter);
                tab_layout.setupWithViewPager(viewPager);
            }
        });
    }
}