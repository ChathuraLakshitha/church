package com.example.zeonit.nclc.actvity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.example.zeonit.nclc.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Test extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
//        startService();
    }

    private void startService() {


        class MyAsync extends AsyncTask<String,String,String> {

            ProgressDialog prograss = new ProgressDialog(Test.this);

            public MyAsync(){
                prograss.setTitle("Please wait......");
//                prograss.setMessage("Connecting..please wait..");
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
                    stringStringMap.put("is_admin_login","sd");
                    stringStringMap.put("user_name","admin");
                    stringStringMap.put("password","123");

                    String d = gson.toJson(stringStringMap);
                    HttpClient client = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost("http://192.168.8.107/NCLC/service/superService.php");
                    StringEntity postingString = new StringEntity(gson.toJson(stringStringMap));//gson.tojson() converts your pojo to json
                    httpPost.setEntity(postingString);
                    httpPost.setHeader("Content-type", "application/json");
//                    httpPost.setHeader("Cookie","PHPSESSID=2q0fr31aeuldl4jl1m50eil441");
//                    ResponseHandler<String> responseHandler = new BasicResponseHandler();

                    HttpResponse response = client.execute(httpPost);
                    BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                    String body = "";

                    String res = "";
                    while ((body = rd.readLine()) != null)
                    {
                       res = res +body;
                    }

                    System.out.println(response.getEntity().getContentEncoding().getElements()[0].getName()+"////////////////////////////////?????");
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
//                    System.out.println("///////////////////////////// response "+response);

//                    showDialog(response);
                }
            }
        }

        new MyAsync().execute();

    }

    private void showDialog(String response){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("web service Result");
        builder.setMessage("Ok")
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
