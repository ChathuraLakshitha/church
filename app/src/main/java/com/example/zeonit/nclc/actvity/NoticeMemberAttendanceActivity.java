package com.example.zeonit.nclc.actvity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.example.zeonit.nclc.R;
import com.example.zeonit.nclc.event.Event;
import com.example.zeonit.nclc.response.Member;
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

public class NoticeMemberAttendanceActivity extends Activity {
    private ListView listView;
    private Event event;

    private List<Member> memberLis = new ArrayList<Member>();
    private List<Map> memberAttendMap = new ArrayList<Map>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        event = new Event(getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_church_notice_attendance);
        listView = findViewById(R.id.churchNoticeAttendanseListView);
        Button submitButton = findViewById(R.id.attendaceNoticeSubmitButton);
        submitButton.setOnClickListener(onClickListenerSubmitButton);
        startService("");
        listView.setAdapter(new myAdapter());
    }

    private class myAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return memberLis.size();
        }

        @Override
        public Object getItem(int i) {
            return memberLis.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            View view1 = LayoutInflater.from(NoticeMemberAttendanceActivity.this).inflate(R.layout.activity_church_notice_attendance_row, null);
            TextView memberName =  view1.findViewById(R.id.churchMemberName);
            Member member = memberLis.get(position);
            memberName.setText(member.getName());

            CheckBox stateeCheckBox = view1.findViewById(R.id.checkBoxAttendance);
            stateeCheckBox.setId(position);

            System.out.println(stateeCheckBox.getId()+" checked id.....................");
            return view1;
        }
    }

    private void startService(final String search) {


        class MyAsync extends AsyncTask<String,String,String> {

            ProgressDialog prograss = new ProgressDialog(NoticeMemberAttendanceActivity.this);

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
                    stringStringMap.put("is_find_member","sd");
                    stringStringMap.put("leader_id",event.getLeaderID());
                    stringStringMap.put("member",search);
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
                    JsonParser jsonParser = new JsonParser();
                    JsonObject jsonObject = new JsonObject();
                    jsonObject = jsonParser.parse(response).getAsJsonObject();
                    int state = jsonObject.get("state").getAsInt();
                    if(state == 1){
                        JsonArray membersArray = jsonObject.get("members").getAsJsonArray();
                        memberLis = new ArrayList<Member>();
                        for (int x = 0;x< membersArray.size();x++){
                            JsonObject resMember = membersArray.get(x).getAsJsonObject();
                            resMember = resMember.get("member").getAsJsonObject();
                            Member member = new Member();
                            member.setAddress(resMember.get("address").getAsString());
                            member.setAge(resMember.get("age_limit").getAsString());
                            member.setName(resMember.get("name").getAsString());
                            member.setTel(resMember.get("tel").getAsInt());
                            member.setMemberId(resMember.get("member_id").getAsInt());
                            System.out.println(resMember.get("member_id").getAsInt()+" ASSSSSSSS");
//
                            memberLis.add(member);
                        }
                        listView.setAdapter(new myAdapter());

                    }
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
        builder.create();
        builder.show();
    }

    private View.OnClickListener onClickListenerSubmitButton= new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(memberLis.size() >0) {
                memberAttendMap = new ArrayList<Map>();
                for (int i = 0; i < memberLis.size(); i++) {
                    int state = 0;
                    CheckBox checkBox = findViewById(i);
                    if(checkBox.isChecked()){
                        state = 1;
                    }
                    Map<String,String> temp = new HashMap<>();
                    temp.put("memmber_id",memberLis.get(i).getMemberId()+"");
                    temp.put("state",state+"");
                    memberAttendMap.add(temp);
                }
                submitAttendace();
            }
        }
    };


    private void submitAttendace() {


        class MyAsync extends AsyncTask<String,String,String> {

            ProgressDialog prograss = new ProgressDialog(NoticeMemberAttendanceActivity.this);

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
                    stringStringMap.put("is_accept_note","sd");
                    stringStringMap.put("leader_id",event.getLeaderID());
                    stringStringMap.put("notice_id",event.getNoticeID());
                    stringStringMap.put("note","Attendance");
                    stringStringMap.put("members",new Gson().toJson(memberAttendMap));
                    Gson gson2 = new Gson();
                    String members = gson2.toJson(memberAttendMap);
//                    stringStringMap.put("member",members);
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
                    System.out.println(response+"llllllllllllllllllllllll");
                    System.out.println(response+"Notice........LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL");
                    JsonParser jsonParser = new JsonParser();
                    JsonObject jsonObject = new JsonObject();
                    jsonObject = jsonParser.parse(response).getAsJsonObject();
                    int state = jsonObject.get("state").getAsInt();
                    if(state == 1){
                        NoticeMemberAttendanceActivity.this.finish();
                        showDialog("Attendance","Success.....");
                        Intent intent = new Intent(NoticeMemberAttendanceActivity.this, NoticeActivity.class);
                        startActivity(intent);

                    }else{
                        String message = jsonObject.get("message").getAsString();
                        showDialog("Attendance",message);
                    }
                }
            }
        }

        new MyAsync().execute();

    }
}
