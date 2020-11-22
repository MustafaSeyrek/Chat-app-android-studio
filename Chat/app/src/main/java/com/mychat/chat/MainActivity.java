package com.mychat.chat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(Helper.isOnline()){
            startActivity(new Intent(this,HomeActivity.class));
            finish();
        }
        else{
            startActivity(new Intent(this,GirisYapActivity.class));
            finish();
        }
    }
}
