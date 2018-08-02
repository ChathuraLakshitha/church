package com.example.zeonit.nclc.actvity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;

import com.example.zeonit.nclc.R;
import com.example.zeonit.nclc.event.Event;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity  {

    private Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        event = new Event(getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.mainLayout).setVisibility(View.GONE);

        ImageView memberBtn = findViewById(R.id.memberSearchBtn);
        memberBtn.setOnClickListener(onClickListenerMember);

        ImageView churchAttendance = findViewById(R.id.chucrchAttendanceBtn);
        churchAttendance.setOnClickListener(onClickListenerChurchAttendance);

        ImageView sdsdsd = findViewById(R.id.noticeBtn);
        sdsdsd.setOnClickListener(onClickListenerNotice);

        ImageView not = findViewById(R.id.noticeAttendanceBtn);
        not.setOnClickListener(onClickListenerNot);

        ImageView event = findViewById(R.id.eventBtn);
        event.setOnClickListener(onClickListenerEvent);

        startService();


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.logOut){
            finish();
            event.putSessionID("00000000000000");
            Intent intent = new Intent(MainActivity.this,SplashActivity.class);
            startActivity(intent);
            return true;
        }else if(id== R.id.live){
            Intent intent = new Intent(MainActivity.this,ChannelActivity.class);
            startActivity(intent);
            return true;
        }else if(id == R.id.callMessenger){
            try {
                Intent messengerIntent = new Intent(Intent.ACTION_SEND);
                messengerIntent.setType("text/plain");
                messengerIntent.setPackage("com.facebook.orca");
                messengerIntent.putExtra(Intent.EXTRA_TEXT, "The text you wanted to share");
                startActivity(messengerIntent);
            } catch (android.content.ActivityNotFoundException anfe) {
                showYesNoDialog();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void startService() {


        class MyAsync extends AsyncTask<String,String,String> {

            ProgressDialog prograss = new ProgressDialog(MainActivity.this);

            public MyAsync(){
                prograss.setTitle("Pleace wait......");
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
                    stringStringMap.put("checked_leader_loged","sd");
                    String d = gson.toJson(stringStringMap);
                    strings = d.split("");
                    HttpClient client = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost("http://rest.nclc.lk/service/LeaderService.php");
                    StringEntity postingString = new StringEntity(gson.toJson(stringStringMap));//gson.tojson() converts your pojo to json
                    httpPost.setEntity(postingString);
                    httpPost.setHeader("Content-type", "application/json");
                    String seeionId = event.getSessionID();
                    httpPost.setHeader("Cookie",seeionId);
                    System.out.println(seeionId+"{}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}");
                    HttpResponse response = client.execute(httpPost);

                    InputStream is = response.getEntity().getContent();
                    String res  = event.convertStreamToString(is);
                    System.out.println(res+"ttttttttttttttttttttttttttttttttttttttttttttttttttttt");
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
                    JsonParser jsonParser = new JsonParser();
                    JsonObject jsonObject = new JsonObject();
                    jsonObject = jsonParser.parse(response).getAsJsonObject();
                    int state = jsonObject.get("state").getAsInt();
                    String message = jsonObject.get("note").getAsString();
                    System.out.println(message+"-------MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM");
                    if(state != 1){

                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    MainActivity.this.finish();
                    }else {
                        findViewById(R.id.mainLayout).setVisibility(View.VISIBLE);
                    }
                }else{
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
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
                        dialog.dismiss();
                    }
                });
// Create the AlertDialog object and return it
        builder.create();
        builder.show();
    }

    private View.OnClickListener onClickListenerMember= new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            System.out.print("<<<<<<<<<<<<<<<<<<<<<<<<<<<<  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            Intent intent=new Intent(MainActivity.this,MemberActivity.class);
            startActivity(intent);
        }
    };

    private View.OnClickListener onClickListenerChurchAttendance= new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            System.out.print("<<<<<<<<<<<<<<<<<<<<<<<<<<<< check for creating attendance >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            checkForCreatingNotice();
        }
    };

    private View.OnClickListener onClickListenerNotice= new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            System.out.print("<<<<<<<<<<<<<<<<<<<<<<<<<<<<  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            Intent intent=new Intent(MainActivity.this,NoticeActivity.class);
            startActivity(intent);
        }
    };

    private View.OnClickListener onClickListenerNot= new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            System.out.print("<<<<<<<<<<<<<<<<<<<<<<<<<<<<  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            Intent intent=new Intent(MainActivity.this,ShowNoticeActivity.class);
            startActivity(intent);
        }
    };

    private View.OnClickListener onClickListenerEvent= new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            System.out.print("<<<<<<<<<<<<<<<<<<<<<<<<<<<< event >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            Intent intent=new Intent(MainActivity.this,ActivityEvent.class);
            startActivity(intent);
        }
    };


    private void checkForCreatingNotice() {


        class MyAsync extends AsyncTask<String,String,String> {

            ProgressDialog prograss = new ProgressDialog(MainActivity.this);

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
                    stringStringMap.put("can_attendece_leader","sd");
                    stringStringMap.put("leader_id",event.getLeaderID());
                    String d = gson.toJson(stringStringMap);
                    strings = d.split("");
                    HttpClient client = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost("http://rest.nclc.lk/service/LeaderService.php");
                    StringEntity postingString = new StringEntity(gson.toJson(stringStringMap));//gson.tojson() converts your pojo to json
                    httpPost.setEntity(postingString);
                    httpPost.setHeader("Content-type", "application/json");
                    String seeionId = event.getSessionID();
                    httpPost.setHeader("Cookie",seeionId);
                    System.out.println(seeionId+"{}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}");
                    HttpResponse response = client.execute(httpPost);

                    InputStream is = response.getEntity().getContent();
                    String res  = event.convertStreamToString(is);
                    System.out.println(res+"ttttttttttttttttttttttttttttttttttttttttttttttttttttt");
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
                    System.out.println(response+" testing////////////////////");
                    JsonParser jsonParser = new JsonParser();
                    JsonObject jsonObject = new JsonObject();
                    jsonObject = jsonParser.parse(response).getAsJsonObject();
                    int state = jsonObject.get("state").getAsInt();
                    if(state == 1){ // create new attendance
//                        showDialog("Creating new attendace","OK");
                        Intent intent = new Intent(MainActivity.this, ChurchMemberAttendanceActivity.class);

                        startActivity(intent);
                    }else if(state == 2){ // update attendance
                        int attendanceID = jsonObject.get("attendance_id").getAsInt();
                        Intent intent = new Intent(MainActivity.this,ActivityUpdateAttendance.class);
                        intent.putExtra("ATTENDANCE_ID",attendanceID);
                        startActivity(intent);
                    }else{
                        System.out.println(response+" jjjjjj error");
                        showDialog("Creating new attendace","Internal Server error");
                    }
                }else{
                   System.out.println(response+" jjjjjj error");
                    showDialog("Creating new attendace","Try again");
                }
            }
        }

        new MyAsync().execute();

    }

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


    private void showYesNoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("The Messaenger not has been installed.\nDo you want to download the app?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();

    }

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.facebook.orca")));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        Toast.makeText(MainActivity.this, "Error/n" + anfe.toString(), Toast.LENGTH_SHORT).show();
                    }
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
            }
        }
    };
}
