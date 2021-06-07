package com.example.firealert;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ProgressBar;

public class MainActivity extends AppCompatActivity {
    //Use for Choose Server
    public static String Server_username_send;
    public static String Server_password_send;
    public static String Server_username_get;
    public static String Server_password_get;
    //...........................

    //Get house link
    public static String HousePath;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar= findViewById(R.id.progressBar1);
        progressBar.setMax(30);
        progressBar.getProgressDrawable().setColorFilter(
                Color.rgb(50,130,184), android.graphics.PorterDuff.Mode.SRC_IN);
        new AsyncLoading().execute();



    }
    private class AsyncLoading extends AsyncTask<Void,Integer,Void>
    {

        @Override
        protected Void doInBackground(Void... voids) {
            for(int count=1;count<=30;count++)
            {
                try {
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                publishProgress(count);
            }
            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            startActivity( new Intent(MainActivity.this,LoginActivity.class));
            finish();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressBar.setProgress(values[0]);

        }




    }
    public static void SetUpServer(String username_get, String password_get, String username_send, String password_send, String path){
        Server_username_send = username_send;
        Server_password_send = password_send;
        Server_username_get = username_get;
        Server_password_get = password_get;
        HousePath = path;
    }

    public static void SetUpHouseDevice(){

    }
}