package net.infojobs.apidownchecker;

import okhttp3.OkHttpClient;

public class ApiDownChecker {

    private final ApiValidator untrustedApiValidator;
    private final ApiValidator trustedValidator;
    private final Logger logger;

    public ApiDownChecker(ApiValidator untrustedApiValidator, ApiValidator trustedValidator, Logger logger) {
        this.untrustedApiValidator = untrustedApiValidator;
        this.trustedValidator = trustedValidator;
        this.logger = logger;
    }

    public boolean isApiDown() {
        logger.log("Failure intercepted. Checking whether your API is down...");
        boolean isUntrustedOk = untrustedApiValidator.isOk();
        if (isUntrustedOk) {
            logger.log("Untrusted validator is OK. False alarm.");
            return false;
        } else {
            logger.log("Untrusted validator is not OK. Now checking trusted validator...");
            boolean isTrustedOk = trustedValidator.isOk();
            if (isTrustedOk) {
                logger.log("Trusted validator is OK. Your API seems to be down!!");
                return true;
            } else {
                logger.log("Trusted validator is not OK. Looks like it's not your API's fault.");
                return false;
            }
        }
    }

    public ApiValidator getTrustedValidator() {
        return trustedValidator;
    }

    public ApiValidator getUntrustedValidator() {
        return untrustedApiValidator;
    }

    public Logger getLogger() {
        return logger;
    }

    public static class Builder {

        private ApiValidator untrustedValidator;
        private ApiValidator trustedValidator;
        private OkHttpClient okHttpClient;
        private String trustedUrl;
        private String untrustedUrl;
        private Logger logger;

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
            if (logger == null) {
                logger = Logger.NONE;
            }
            return new ApiDownChecker(untrustedValidator, trustedValidator, logger);
        }

        private OkHttpClient getHttpClient() {
            if (okHttpClient == null) {
                okHttpClient = new OkHttpClient();
            }
            return okHttpClient;
        }

        public Builder logWith(Logger logger) {
            this.logger = logger;
            return this;
        }
    }
}
