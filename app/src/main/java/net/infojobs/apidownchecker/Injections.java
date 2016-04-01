package net.infojobs.apidownchecker;

import com.jakewharton.retrofit.Ok3Client;

import net.infojobs.ApiDownChecker;
import net.infojobs.ApiDownInterceptor;

import okhttp3.OkHttpClient;
import retrofit.RestAdapter;
import retrofit.android.AndroidLog;

/**
 * This class represents what your dependency injector configuration should look like.
 */
public class Injections {

    private static OkHttpClient okHttpClient;
    private static DemoInterceptor demoInterceptor;
    private static MyAPI myApi;

    public static OkHttpClient getOkHttpClient() {
        if (okHttpClient == null) {
            ApiDownChecker checker = new ApiDownChecker.Builder()
              .withClient(getBreakableHttpClient())
              .check("http://httpstat.us/200")
              .trustGoogle()
              .build();

            okHttpClient = new OkHttpClient.Builder()
              .addInterceptor(ApiDownInterceptor.create()
                .checkWith(checker)
                .build())
              .build();
        }
        return okHttpClient;
    }

    /**
     * We use this just for the demo, normally we wouldn't need it.
     */
    private static OkHttpClient getBreakableHttpClient() {
        return new OkHttpClient.Builder().addInterceptor(getDemoInterceptor()).build();
    }

    public static DemoInterceptor getDemoInterceptor() {
        if (demoInterceptor == null) {
            demoInterceptor = new DemoInterceptor();
        }
        return demoInterceptor;
    }

    public static MyAPI getMyApi() {
        if (myApi == null) {
            myApi = new RestAdapter.Builder()
              .setClient(new Ok3Client(getOkHttpClient()))
              .setEndpoint("http://httpstat.us/")
              .setLogLevel(RestAdapter.LogLevel.BASIC)
              .setLog(new AndroidLog("DEMO"))
              .build().create(MyAPI.class);
        }
        return myApi;
    }
}
