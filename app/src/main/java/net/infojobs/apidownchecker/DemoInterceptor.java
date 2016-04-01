package net.infojobs.apidownchecker;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

public class DemoInterceptor implements Interceptor {

    private boolean forceApiDown;

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());
        if (forceApiDown && chain.request().url().host().equals("httpstat.us")) {
            return response.newBuilder().code(503).build();
        }
        return response;
    }


    public void forceApiDown(boolean forceApiDown) {
        this.forceApiDown = forceApiDown;
    }
}
