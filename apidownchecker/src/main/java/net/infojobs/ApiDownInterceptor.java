package net.infojobs;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

public class ApiDownInterceptor implements Interceptor {

    private final ApiDownChecker apiDownChecker;

    private ApiDownInterceptor(ApiDownChecker apiDownChecker) {
        this.apiDownChecker = apiDownChecker;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response;
        try {
            response = chain.proceed(chain.request());
        } catch (IOException e) {
            if (apiDownChecker.isApiDown()) {
                throw new ApiDownException();
            } else {
                throw e;
            }
        }
        if (!response.isSuccessful()) {
            if (apiDownChecker.isApiDown()) {
                throw new ApiDownException();
            }
        }
        return response;
    }

    public static Builder create() {
        return new Builder();
    }

    public static class Builder {
        private ApiDownChecker apiDownChecker;

        public ApiDownInterceptor build() {
            return new ApiDownInterceptor(apiDownChecker);
        }

        public Builder checkWith(ApiDownChecker apiDownChecker) {
            this.apiDownChecker = apiDownChecker;
            return this;
        }
    }

}
