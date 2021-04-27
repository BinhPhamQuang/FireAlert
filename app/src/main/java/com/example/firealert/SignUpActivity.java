package com.example.firealert;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firealert.Adapter.ViewPageAdapter;
import com.example.firealert.fragment_signup.FragmentSignUp1;
import com.example.firealert.fragment_signup.FragmentSignup2;
import com.example.firealert.fragment_signup.FragmentSignup3;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;

public class SignUpActivity extends AppCompatActivity {

    ViewPager viewPager;
    ImageButton btnBack;
    ImageButton btnNext;
    TextView tv1;
    ViewPageAdapter viewPageAdapter;
    DotsIndicator dotsIndicator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        viewPager= findViewById(R.id.viewpager_signup);
        btnBack= findViewById(R.id.btnBack);
        btnNext= findViewById(R.id.btnNext);
        tv1 = findViewById(R.id.tv_top);
        dotsIndicator = findViewById(R.id.dots_indicator);




        getTab();
        setColorforTopLabel();
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewPager.getCurrentItem()==0)
                {
                   startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
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
                    startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
                    finish();
                }
            }
        });

    }
    private void setColorforTopLabel()
    {
        String a=  "<font color=" + "#0F4C75" + ">" + "Get started" + "</font>";
        String b=  "<font color=" + "#3282B8" + ">" + " with new account !" + "</font>";
        tv1.setText(Html.fromHtml(a+b));
    }
    private void getTab()
    {
         viewPageAdapter = new ViewPageAdapter(getSupportFragmentManager());
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                viewPageAdapter.addFragment(FragmentSignUp1.getInstance(),"1");
                viewPageAdapter.addFragment(FragmentSignup2.getInstance(),"2");
                viewPageAdapter.addFragment(FragmentSignup3.getInstance(),"3");
                viewPager.setAdapter(viewPageAdapter);
                dotsIndicator.setViewPager(viewPager);
            }
        });
    }

}