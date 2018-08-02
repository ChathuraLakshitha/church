package com.example.zeonit.nclc.actvity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ActivityEventCreate extends Activity{
    private Event event;
    EditText editText;
    Calendar myCalendar = Calendar.getInstance();
    EditText eventTitle;
    EditText eventDescription;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_form);
        event = new Event(getApplicationContext());
        editText= findViewById(R.id.editText4);
        eventTitle= findViewById(R.id.evetTitle);
        eventDescription= findViewById(R.id.eventDescription);
        editText.setOnClickListener(onClickListenerNewEvent);
        Button submintButton = findViewById(R.id.submintBtn);
        submintButton.setOnClickListener(onClickListenerSubmit);

        eventTitle.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        eventDescription.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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
    private View.OnClickListener onClickListenerNewEvent= new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            new DatePickerDialog(ActivityEventCreate.this, date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
        }
    };

    private  View.OnClickListener onClickListenerSubmit = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            startService("");
        }
    };

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }

    };

    private void updateLabel() {
        String myFormat = "yyyy/MM/dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        editText.setText(sdf.format(myCalendar.getTime()));
    }

    private void startService(final String search) {

        class MyAsync extends AsyncTask<String,String,String> {

            ProgressDialog prograss = new ProgressDialog(ActivityEventCreate.this);

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
                    String title = eventTitle.getText().toString();
                    String description = eventDescription.getText().toString();
                    String upcommingDate = editText.getText().toString();
                    stringStringMap.put("is_add_event","sd");
                    stringStringMap.put("leader_d",event.getLeaderID());
                    stringStringMap.put("up_coming_date",upcommingDate);
                    stringStringMap.put("title",title);
                    stringStringMap.put("description",description);
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
                    String message = jsonObject.get("message").getAsString();
                    if(state == 1){

                        Intent intent = new Intent(ActivityEventCreate.this,ActivityEvent.class);
                        startActivity(intent);
                        finish();
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
