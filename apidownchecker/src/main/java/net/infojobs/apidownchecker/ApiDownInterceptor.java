package net.infojobs.apidownchecker;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

public class ApiDownInterceptor implements Interceptor {

    private final net.infojobs.apidownchecker.ApiDownChecker apiDownChecker;

    private ApiDownInterceptor(net.infojobs.apidownchecker.ApiDownChecker apiDownChecker) {
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
        private net.infojobs.apidownchecker.ApiDownChecker apiDownChecker;

        public ApiDownInterceptor build() {
            return new ApiDownInterceptor(apiDownChecker);
        }

        public Builder checkWith(net.infojobs.apidownchecker.ApiDownChecker apiDownChecker) {
            this.apiDownChecker = apiDownChecker;
            return this;
        }
    }

}
