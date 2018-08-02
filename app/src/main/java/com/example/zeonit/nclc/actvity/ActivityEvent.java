package com.example.zeonit.nclc.actvity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zeonit.nclc.R;
import com.example.zeonit.nclc.event.Event;
import com.example.zeonit.nclc.response.Member;
import com.example.zeonit.nclc.response.Notice;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityEvent extends Activity{
    private Event event;
    private ListView listView;
    private List<Notice> myList = new ArrayList<Notice>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        event = new Event(getApplicationContext());
        Button newEventBt = findViewById(R.id.newEventButton);
        newEventBt.setOnClickListener(onClickListenerNewEvent);
        listView = findViewById(R.id.eventListView);
        startService("");
    }

    private class noticeAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return myList.size();
        }

        @Override
        public Object getItem(int i) {
            return myList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            View view1 = LayoutInflater.from(ActivityEvent.this).inflate(R.layout.activity_church_notice_row, null);
//            Layout layout = findViewById(R.id.notice)
            TextView upCommingDate = view1.findViewById(R.id.textUpcommingDate);
            TextView type = view1.findViewById(R.id.textType);
            TextView description = view1.findViewById(R.id.textDescription);
            TextView noticeId = view1.findViewById(R.id.noticeId);
            Notice notice = myList.get(position);
            upCommingDate.setText(notice.getUpCommingDate());
            type.setText(notice.getType());
            noticeId.setText(notice.getNoticeId()+"");

            if( notice.isValid() == true){
                int color  = Color.parseColor("#2efc0404");
//                view1.
//                View s = findViewById(R.drawable.notice_warning_list_style);
//                view1.setBackgroundResource(R.drawable.notice_warning_list_style);
                view1.setOnClickListener(onClickListenerMarkEventMembers);
                //f9e6e7
            }else{
                view1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getApplicationContext(), "Expire in the event....",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
            description.setText(notice.getDescription());

            return view1;
        }
    }

    private View.OnClickListener onClickListenerNewEvent= new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            System.out.print("<<<<<<<<<<<<<<<<<<<<<<<<<<<< event >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            Intent intent=new Intent(ActivityEvent.this,ActivityEventCreate.class);
            startActivity(intent);
            finish();
        }
    };

    private void startService(final String search) {

        class MyAsync extends AsyncTask<String,String,String> {

            ProgressDialog prograss = new ProgressDialog(ActivityEvent.this);

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
                    stringStringMap.put("get_all_event","sd");
                    stringStringMap.put("leader_id",event.getLeaderID());
                    HttpClient client = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost("http://rest.nclc.lk/service/LeaderService.php");
                    StringEntity postingString = new StringEntity(gson.toJson(stringStringMap));//gson.tojson() converts your pojo to json
                    httpPost.setEntity(postingString);
                    httpPost.setHeader("Content-type", "application/json");
                    String seeionId = event.getSessionID();
                    httpPost.setHeader("Cookie",seeionId);
                    HttpResponse response = client.execute(httpPost);

                    InputStream is = response.getEntity().getContent();
                    String res  = event.convertStreamToString(is);
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
                    System.out.println(" : "+response+" : >>>>>>>>>>>>>>>>>>>>>>>>>>");
                    JsonParser jsonParser = new JsonParser();
                    JsonObject jsonObject = new JsonObject();
                    jsonObject = jsonParser.parse(response).getAsJsonObject();
                    int state = jsonObject.get("state").getAsInt();
                    String message = jsonObject.get("message").getAsString();

                    if(state == 1){
                        JsonArray dataJsonArray = jsonObject.get("data").getAsJsonArray();
                            myList = new ArrayList<Notice>();
                            for(int a = 0;a<dataJsonArray.size();a++){
                                Notice notice = new Notice();
                                notice.setUpCommingDate(dataJsonArray.get(a).getAsJsonObject().get("up_coming_date").getAsString());
                                notice.setType(dataJsonArray.get(a).getAsJsonObject().get("title").getAsString());
                                notice.setDescription(dataJsonArray.get(a).getAsJsonObject().get("description").getAsString());
                                notice.setNoticeId(dataJsonArray.get(a).getAsJsonObject().get("id").getAsInt());
                                notice.setValid(dataJsonArray.get(a).getAsJsonObject().get("is_valid").getAsBoolean());

                                myList.add(notice);
                            }
                        listView.setAdapter(new noticeAdapter());
                    }else{
                        Toast.makeText(getApplicationContext(), message,
                                Toast.LENGTH_LONG).show();
                    }
                }
            }
        }

        new MyAsync().execute();

    }

    private View.OnClickListener onClickListenerMarkEventMembers= new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            TextView noticeId = view.findViewById(R.id.noticeId);
            String noticeGetId =noticeId.getText().toString();
            searchEventMembersService(noticeGetId);
        }
    };


    private void searchEventMembersService(final String eventId) {


        class MyAsync extends AsyncTask<String,String,String> {

            ProgressDialog prograss = new ProgressDialog(ActivityEvent.this);

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
                    stringStringMap.put("get_event_members","sd");
                    stringStringMap.put("leader_id",event.getLeaderID());
                    stringStringMap.put("event_id",eventId);
                    String d = gson.toJson(stringStringMap);
                    strings = d.split("");
                    HttpClient client = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost("http://rest.nclc.lk/service/LeaderService.php");
                    StringEntity postingString = new StringEntity(gson.toJson(stringStringMap));//gson.tojson() converts your pojo to json
                    httpPost.setEntity(postingString);
                    httpPost.setHeader("Content-type", "application/json");
                    String seeionId = event.getSessionID();
                    httpPost.setHeader("Cookie",seeionId);
                    HttpResponse response = client.execute(httpPost);

                    InputStream is = response.getEntity().getContent();
                    String res  = event.convertStreamToString(is);
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
                    System.out.print("<<<<<<<<<<<<<<<<<<<<<<<<<<<< "+response+" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                    JsonParser jsonParser = new JsonParser();
                    JsonObject jsonObject = new JsonObject();
                    jsonObject = jsonParser.parse(response).getAsJsonObject();
                    int state = jsonObject.get("state").getAsInt();
                    String message = jsonObject.get("message").getAsString();
                    if(state == 1){
                        finish();
                        Intent intent=new Intent(ActivityEvent.this,ActivityEventMembers.class);
                        intent.putExtra("EVENT_ID",eventId);
                        startActivity(intent);

                    }else{
                        Toast.makeText(getApplicationContext(), message,
                                Toast.LENGTH_LONG).show();
                    }
                }
            }
        }

        new MyAsync().execute();

    }
}
