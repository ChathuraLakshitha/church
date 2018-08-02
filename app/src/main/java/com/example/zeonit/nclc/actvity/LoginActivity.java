package com.example.zeonit.nclc.actvity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Layout;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.zeonit.nclc.R;
import com.example.zeonit.nclc.event.Event;
import com.example.zeonit.nclc.response.LoginResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends Activity {
    private Event event;
    private LoginResponse loginResponse;
    private String cookieId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        event = new Event(getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//        findViewById(R.id.mainLayout).setVisibility(View.GONE);

        TextView loginBtn = findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(onClickListenerLogin);
        TextView edittext = findViewById(R.id.apsswordText);
        TextView textPhone = findViewById(R.id.phoneText);
        textPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        edittext.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });


    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    private void startService(final String password, final int phoneNumber) {


        class MyAsync extends AsyncTask<String,String,String> {

            ProgressDialog prograss = new ProgressDialog(LoginActivity.this);

            public MyAsync(){
                prograss.setTitle("Please wait......");
                prograss.setMessage("Connecting..please wait..");
                prograss.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                prograss.setIndeterminate(true);
                prograss.show();
            }

            @Override
            protected String doInBackground(String... strings) {
                try {


                    Gson gson = new Gson();
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    Map<String,String> stringStringMap = new HashMap<>();
                    stringStringMap.put("is_login_leader","sd");
                    stringStringMap.put("leader_tel",""+phoneNumber);
                    stringStringMap.put("leader_password",password);

                    HttpClient client = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost("http://rest.nclc.lk/service/LeaderService.php");
                    StringEntity postingString = new StringEntity(gson.toJson(stringStringMap));//gson.tojson() converts your pojo to json
                    httpPost.setEntity(postingString);
                    httpPost.setHeader("Content-type", "application/json");

                    HttpResponse response = client.execute(httpPost);

                    InputStream is = response.getEntity().getContent();
                    String res  = event.convertStreamToString(is);
                    Header[] header = response.getHeaders("Set-Cookie");
                    event.putSessionID(header[0].getValue());
                    return res;


                } catch (IOException e) {
                    e.printStackTrace();

                }

                return null;
            }

            @Override
            protected void onPostExecute(String response) {
                super.onPostExecute(response);
                prograss.dismiss();
                if(response != null){
                    Gson gson = new Gson();
                    String responseBody = gson.toJson(response);
                    loginResponse = new Gson().fromJson(response, new TypeToken<LoginResponse>() {
                    }.getType());

                        if (loginResponse.state == 1){
                            System.out.println(loginResponse.leaderId+"VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV");
                            event.putLeaderID(loginResponse.leaderId+"");
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            LoginActivity.this.finish();
                        }else{
                                showDialog("Login Fails","try agin");
                        }

                }
            }
        }

        new MyAsync().execute();

    }

    private View.OnClickListener onClickListenerLogin= new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            TextView textPassword = findViewById(R.id.apsswordText);
            String password = textPassword.getText().toString();
            TextView textPhone = findViewById(R.id.phoneText);
            int phoneNumber = 0;
            if(!textPhone.getText().toString().equals("")){
             phoneNumber = Integer.parseInt(textPhone.getText().toString());
            }

            if(phoneNumber != 0 && !(password.equals(""))){
                startService(password,phoneNumber);
            }else{
                showDialog("Login","Please input login detatils");
            }
        }
    };

    private void showDialog(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                        dialog.dismiss();
                    }
                });
// Create the AlertDialog object and return it
        builder.create();
        builder.show();
    }
}
