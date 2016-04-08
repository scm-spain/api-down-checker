package com.schibsted.spain.apidownchecker;

public interface Logger {
    Logger NONE = new Logger() {
        @Override
        public void log(String message) {
            // NA
        }
    };

    void log(String message);
}
