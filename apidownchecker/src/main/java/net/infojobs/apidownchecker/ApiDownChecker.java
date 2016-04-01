package net.infojobs.apidownchecker;

import okhttp3.OkHttpClient;

public class ApiDownChecker {

    private final ApiValidator untrustedApiValidator;
    private final ApiValidator trustedValidator;

    public ApiDownChecker(ApiValidator untrustedApiValidator, ApiValidator trustedValidator) {
        this.untrustedApiValidator = untrustedApiValidator;
        this.trustedValidator = trustedValidator;
    }

    public boolean isApiDown() {
        return !untrustedApiValidator.isOk() && trustedValidator.isOk();
    }

    public ApiValidator getTrustedValidator() {
        return trustedValidator;
    }

    public ApiValidator getUntrustedValidator() {
        return untrustedApiValidator;
    }

    public static class Builder {

        private ApiValidator untrustedValidator;
        private ApiValidator trustedValidator;
        private OkHttpClient okHttpClient;
        private String trustedUrl;
        private String untrustedUrl;

        public Builder check(ApiValidator untrustedValidator) {
            this.untrustedValidator = untrustedValidator;
            return this;
        }

        public Builder check(String untrustedUrl) {
            this.untrustedUrl = untrustedUrl;
            return this;
        }

        public Builder trust(ApiValidator trustedValidator) {
            this.trustedValidator = trustedValidator;
            return this;
        }

        public Builder trust(String trustedUrl) {
            this.trustedUrl = trustedUrl;
            return this;
        }

        public Builder trustGoogle() {
            trustedUrl = "https://google.com";
            return this;
        }

        public Builder inGoogleWeTrust() {
            return trustGoogle();
        }

        public Builder withClient(OkHttpClient client) {
            this.okHttpClient = client;
            return this;
        }

        public ApiDownChecker build() {
            if (untrustedValidator == null && untrustedUrl == null) {
                throw new IllegalStateException("You must provide an untrusted validator or url");
            }
            if (trustedValidator == null && trustedUrl == null) {
                this.trustGoogle();
            }
            if (untrustedValidator == null) {
                untrustedValidator = new HttpValidator(getHttpClient(), untrustedUrl);
            }
            if (trustedValidator == null) {
                trustedValidator = new HttpValidator(getHttpClient(), trustedUrl);
            }
            return new ApiDownChecker(untrustedValidator, trustedValidator);
        }

        private OkHttpClient getHttpClient() {
            if (okHttpClient == null) {
                okHttpClient = new OkHttpClient();
            }
            return okHttpClient;
        }
    }
}
