package com.vt.disposisibandung.utils;

import com.vt.disposisibandung.models.WebServiceResponse;

import retrofit.client.Response;

/**
 * Created by irvan on 6/25/15.
 */
public abstract class Callback<T> implements retrofit.Callback<WebServiceResponse<T>> {

    public abstract void success(T t);

    public abstract void failure(WebServiceResponse error);

    @Override
    public void success(WebServiceResponse<T> tWebServiceResponse, Response response) {
        if (tWebServiceResponse.getStatusCode() == 200) {
            success(tWebServiceResponse.getResult());
        } else {
            failure(tWebServiceResponse);
        }
    }


}
