package com.example.zeonit.nclc.actvity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
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

public class NoticeActivity extends Activity {
    private ListView listView;
    private int noticeId = 000;
    private Event event;
//    R.drawable drawable ;

    private List<Notice> myList = new ArrayList<Notice>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        event = new Event(getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_church_notice);
        listView = findViewById(R.id.churchNoticeListView);
//        drawable = findViewById(R.drawable.);
        Notice no = new Notice();
        myList.add(no);

        listView.setAdapter(new noticeAdapter());

        startService("s");
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
            View view1 = LayoutInflater.from(NoticeActivity.this).inflate(R.layout.activity_church_notice_row, null);
//            Layout layout = findViewById(R.id.notice)
            TextView upCommingDate = view1.findViewById(R.id.textUpcommingDate);
            TextView type = view1.findViewById(R.id.textType);
            TextView description = view1.findViewById(R.id.textDescription);
            TextView noticeId = view1.findViewById(R.id.noticeId);
            Notice notice = myList.get(position);
            upCommingDate.setText(notice.getUpCommingDate());
            type.setText(notice.getType());
            noticeId.setText(notice.getNoticeId()+"");

            if(notice.isExpire()){
                Toast.makeText(getApplicationContext(), "Expired on the Notice",
                        Toast.LENGTH_LONG).show();
            }else{
                if( notice.isValid() == true){
//                    int color  = Color.parseColor("#2efc0404");
//                view1.
//                View s = findViewById(R.drawable.notice_warning_list_style);
//                    view1.setBackgroundResource(R.drawable.notice_warning_list_style);
                    view1.setOnClickListener(onClickListenerAttemTrueToCreatingAttendance);
                    //f9e6e7
                }else{
                    view1.setOnClickListener(onClickListenerAttemUpdateToCreatingAttendance);
                }
            }
            description.setText(notice.getDescription());

            return view1;
        }
    }

    private void startService(final String search) {


        class MyAsync extends AsyncTask<String,String,String> {

            ProgressDialog prograss = new ProgressDialog(NoticeActivity.this);

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
                    stringStringMap.put("leader_get_all_notice","sd");
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
                    HttpResponse response = client.execute(httpPost);

                    InputStream is = response.getEntity().getContent();
                    String res  = event.convertStreamToString(is);
                    System.out.println(" <<<<<<<<<<<< :" +res + " : >>>>>>>>>");
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
                    JsonParser parser = new JsonParser();
                    JsonObject object = new JsonObject();
                    object = parser.parse(response).getAsJsonObject();
                    int state = object.get("state").getAsInt();
                    if(state == 1) {
                        JsonParser jsonParser = new JsonParser();
                        JsonArray noticeArray = object.get("notice").getAsJsonArray();
                        myList = new ArrayList<Notice>();
                        for (int x = 0; x < noticeArray.size(); x++) {
                            boolean isValid = noticeArray.get(x).getAsJsonObject().get("is_valid").getAsBoolean();
                            boolean isExpire =noticeArray.get(x).getAsJsonObject().get("is_expire").getAsBoolean();
                            JsonObject notice = noticeArray.get(x).getAsJsonObject().get("notice").getAsJsonObject();
                            int noticeId = notice.get("notice_id").getAsInt();
                            String noticeType = notice.get("type_of_notice").getAsString();
                            String up_comingDate = notice.get("up_coming_date").getAsString();
                            String description = notice.get("description").getAsString();
//                            boolean isValid = notice.get("is_valid").getAsBoolean();

                            Notice notice1 = new Notice();
                            notice1.setNoticeId(noticeId);
                            notice1.setType(noticeType);
                            notice1.setUpCommingDate(up_comingDate);
                            notice1.setDescription(description);
                            notice1.setValid(isValid);
                            notice1.setExpire(isExpire);
                            myList.add(notice1);
                        }
                        listView.setAdapter(new noticeAdapter());
                        System.out.println(response + "UUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUU");
                    }else{
                        Toast.makeText(getApplicationContext(), "No Attendance",
                                Toast.LENGTH_LONG).show();
                    }
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
        builder.create();
        builder.show();
    }

    private View.OnClickListener onClickListenerSearch= new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            EditText memberSearch = findViewById(R.id.seachMember);
            String search = memberSearch.getText().toString();
            startService(search);
        }
    };

    private View.OnClickListener onClickListenerAttemUpdateToCreatingAttendance= new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            TextView noticeId = view.findViewById(R.id.noticeId);
            String noticeGetId =noticeId.getText().toString();
            event.putNoticeID(noticeGetId);
            NoticeActivity.this.finish();
            Intent intent = new Intent(NoticeActivity.this, ActivityNoticeMembersUpdate.class);
            startActivity(intent);
        }
    };

    private View.OnClickListener onClickListenerAttemTrueToCreatingAttendance= new View.OnClickListener() {
        @Override
        public void onClick(View view) {
                TextView noticeId = view.findViewById(R.id.noticeId);
                String noticeGetId =noticeId.getText().toString();
                event.putNoticeID(noticeGetId);
                NoticeActivity.this.finish();
            Intent intent = new Intent(NoticeActivity.this, NoticeMemberAttendanceActivity.class);
            startActivity(intent);
        }
    };

    private void showDialog(String title,String note){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(note)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        builder.create();
        builder.show();
    }
}
