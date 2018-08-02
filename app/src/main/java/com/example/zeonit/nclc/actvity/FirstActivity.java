package com.example.zeonit.nclc.actvity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.zeonit.nclc.R;

public class FirstActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        TextView loginBttn = findViewById(R.id.loginToLoginBtn);
        loginBttn.setOnClickListener(onClickListenerLogin);

        TextView watchBtn = findViewById(R.id.watchBtn);
        watchBtn.setOnClickListener(onClickListenerYouTube);
    }

    private View.OnClickListener onClickListenerLogin= new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
            Intent intent = new Intent(FirstActivity.this,MainActivity.class);
            startActivity(intent);
        }
    };

    private View.OnClickListener onClickListenerYouTube= new View.OnClickListener() {
        @Override
        public void onClick(View view) {
//            finish();
            Intent intent = new Intent(FirstActivity.this,ChannelActivity.class);
            startActivity(intent);
        }
    };
}
