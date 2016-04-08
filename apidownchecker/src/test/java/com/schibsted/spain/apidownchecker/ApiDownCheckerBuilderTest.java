package com.schibsted.spain.apidownchecker;

import org.junit.Test;

import okhttp3.OkHttpClient;
import okhttp3.Request;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class ApiDownCheckerBuilderTest {

    @Test
    public void testBuilderWithValidators() throws Exception {
        ApiValidator apiValidator = mock(ApiValidator.class);
        ApiValidator trustedValidator = mock(ApiValidator.class);

        ApiDownChecker checker = ApiDownChecker.create()
          .check(apiValidator)
          .trust(trustedValidator)
          .build();

        assertTrue(checker.getTrustedValidator() == trustedValidator);
        assertTrue(checker.getUntrustedValidator() == apiValidator);
    }

    @Test
    public void testBuilderWithEndpoints() throws Exception {
        ApiDownChecker checker = ApiDownChecker.create()
          .check("http://my.api")
          .trust("http://trusted.api")
          .build();

        HttpValidator trustedValidator = ((HttpValidator) checker.getTrustedValidator());
        HttpValidator untrustedValidator = ((HttpValidator) checker.getUntrustedValidator());

        assertEquals("http://my.api", untrustedValidator.getEndpoint());
        assertEquals("http://trusted.api", trustedValidator.getEndpoint());
    }

    @Test
    public void testBuilderDefaultsToGoogleWithoutTrustParameter() throws Exception {
        ApiDownChecker checker = ApiDownChecker.create()
          .check("http://my.api")
          .build();

        ApiValidator trustedValidator = checker.getTrustedValidator();
        String endpoint = ((HttpValidator) trustedValidator).getEndpoint();
        assertEquals("https://google.com", endpoint);
    }

    @Test
    public void testBuilderWithTrustGoogle() throws Exception {
        ApiDownChecker checker = ApiDownChecker.create()
          .check("http://my.api")
          .trustGoogle()
          .build();

        ApiValidator trustedValidator = checker.getTrustedValidator();
        String endpoint = ((HttpValidator) trustedValidator).getEndpoint();
        assertEquals("https://google.com", endpoint);
    }

    @Test
    public void testBuilderWithTrustGoogleAmen() throws Exception {
        ApiDownChecker checker = ApiDownChecker.create()
          .check("http://my.api")
          .inGoogleWeTrust()
          .build();

        ApiValidator trustedValidator = checker.getTrustedValidator();
        String endpoint = ((HttpValidator) trustedValidator).getEndpoint();
        assertEquals("https://google.com", endpoint);
    }

    @Test
    public void testBuilderWithCustomOkClient() throws Exception {
        OkHttpClient client = spy(new OkHttpClient());

        ApiDownChecker checker = ApiDownChecker.create()
          .check("http://my.api")
          .trust("http://trusted.api")
          .withClient(client)
          .build();

        checker.getTrustedValidator().isOk();
        checker.getUntrustedValidator().isOk();

        verify(client, times(2)).newCall(any(Request.class));
    }

    @Test(expected = IllegalStateException.class)
    public void testBuilderFailsWithoutCheckParameter() throws Exception {
        ApiDownChecker.create()
          .trust("http://trusted.api")
          .build();
    }

    @Test
    public void testBuilderWithLog() throws Exception {
        Logger logger = new DummyLogger();
        ApiDownChecker checker = ApiDownChecker.create()
          .check("http://my.api")
          .logWith(logger)
          .build();

        assertSame(logger, checker.getLogger());
    }

    @Test
    public void testBuilderWithNoneLogByDefault() throws Exception {
        ApiDownChecker checker = ApiDownChecker.create()
          .check("http://my.api")
          .build();

        assertEquals(Logger.NONE, checker.getLogger());
    }

    private class DummyLogger implements Logger {
        @Override
        public void log(String message) {
            //NA
        }
    }
}
