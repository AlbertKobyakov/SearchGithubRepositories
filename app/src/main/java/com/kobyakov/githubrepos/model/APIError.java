package com.kobyakov.githubrepos.model;

import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class APIError {
    @SerializedName("message")
    @Expose
    private String message;

    private String request;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    @NonNull
    @Override
    public String toString() {
        return "APIError{" +
                "message='" + message + '\'' +
                ", request='" + request + '\'' +
                '}';
    }
}