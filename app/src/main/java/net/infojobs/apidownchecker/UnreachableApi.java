package net.infojobs.apidownchecker;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.GET;

public interface UnreachableApi {

    @GET("/something")
    void getSomething(Callback<Response> callback);
}
