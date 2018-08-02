package com.example.zeonit.nclc.response;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {

    @SerializedName("state")
    public int state;

    @SerializedName("message")
    public String message;

    @SerializedName("leader_id")
    public int leaderId;
}
