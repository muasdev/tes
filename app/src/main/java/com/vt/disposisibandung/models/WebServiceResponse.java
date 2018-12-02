package com.vt.disposisibandung.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by irvan on 6/25/15.
 */
public class WebServiceResponse<T> {

    @SerializedName("response")
    private String status;

    @SerializedName("statusCode")
    private int statusCode;

    @SerializedName("message")
    private String message;

    @SerializedName("result")
    private T result;

    public String getStatus() {
        return status;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    public T getResult() {
        return result;
    }
}
