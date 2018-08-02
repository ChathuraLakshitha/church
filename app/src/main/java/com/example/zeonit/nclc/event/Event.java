package com.example.zeonit.nclc.event;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

import static android.content.Context.MODE_PRIVATE;

public class Event {

    private final SharedPreferences pref;
    public Event(Context context){
        this.pref = context.getSharedPreferences("ChurchLogin", MODE_PRIVATE);
    }

    public String getSessionID(){

        if(pref.contains("SESSION_ID")){
            return pref.getString("SESSION_ID",null);
        }else{
            SharedPreferences.Editor editor = pref.edit();
            String sessionID = "0000";
            editor.putString("SESSION_ID","PHPSESSID="+sessionID);
            editor.commit();
            return sessionID;
        }

    }

    public void putSessionID(String sessionID){

        SharedPreferences.Editor editor = pref.edit();
        editor.putString("SESSION_ID",sessionID);
        editor.commit();
    }

    public String convertStreamToString(InputStream is) throws IOException {
        if (is != null) {
            Writer writer = new StringWriter();

            char[] buffer = new char[1024];
            try {
                Reader reader = new BufferedReader(
                        new InputStreamReader(is, "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            } finally {
                is.close();
            }
            return writer.toString();
        } else {
            return "";
        }
    }

    public void putLeaderID(String leaderID){

        SharedPreferences.Editor editor = pref.edit();
        editor.putString("LEADER_ID",leaderID);
        editor.commit();
    }

    public String getLeaderID(){

        if(pref.contains("LEADER_ID")){
            return pref.getString("LEADER_ID",null);
        }else{
            SharedPreferences.Editor editor = pref.edit();
            String leaderID = "0000";
            editor.putString("LEADER_ID",leaderID);
            editor.commit();
            return leaderID;
        }

    }

    public String getNoticeID(){

        if(pref.contains("NOTICE_ID")){
            return pref.getString("NOTICE_ID",null);
        }else{
            SharedPreferences.Editor editor = pref.edit();
            String noticeID = "0000";
            editor.putString("NOTICE_ID",noticeID);
            editor.commit();
            return noticeID;
        }

    }

    public void putNoticeID(String noticeID){

        SharedPreferences.Editor editor = pref.edit();
        editor.putString("NOTICE_ID",noticeID);
        editor.commit();
    }
}
