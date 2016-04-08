package com.schibsted.spain.apidownchecker;

interface DateProvider {

    DateProvider SYSTEM = new DateProvider() {

        @Override
        public long now() {
            return System.currentTimeMillis();
        }
    };

    long now();
}