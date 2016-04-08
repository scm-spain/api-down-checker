package com.schibsted.spain.apidownchecker.demo;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.GET;

public interface MyAPI {

    @GET("/200")
    void get200(Callback<Response> callback);

    @GET("/503")
    void get503(Callback<Response> callback);

}
