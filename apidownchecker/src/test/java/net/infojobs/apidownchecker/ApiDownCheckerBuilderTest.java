package net.infojobs.apidownchecker;

import org.junit.Test;

import okhttp3.OkHttpClient;
import okhttp3.Request;

import static org.junit.Assert.assertEquals;
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

        ApiDownChecker checker = new ApiDownChecker.Builder()
                .check(apiValidator)
                .trust(trustedValidator)
                .build();

        assertTrue(checker.getTrustedValidator() == trustedValidator);
        assertTrue(checker.getUntrustedValidator() == apiValidator);
    }

    @Test
    public void testBuilderWithEndpoints() throws Exception {
        ApiDownChecker checker = new ApiDownChecker.Builder()
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
        ApiDownChecker checker = new ApiDownChecker.Builder()
          .check("http://my.api")
          .build();

        ApiValidator trustedValidator = checker.getTrustedValidator();
        String endpoint = ((HttpValidator) trustedValidator).getEndpoint();
        assertEquals("https://google.com", endpoint);
    }

    @Test
    public void testBuilderWithTrustGoogle() throws Exception {
        ApiDownChecker checker = new ApiDownChecker.Builder()
                .check("http://my.api")
                .trustGoogle()
                .build();

        ApiValidator trustedValidator = checker.getTrustedValidator();
        String endpoint = ((HttpValidator) trustedValidator).getEndpoint();
        assertEquals("https://google.com", endpoint);
    }

    @Test
    public void testBuilderWithTrustGoogleAmen() throws Exception {
        ApiDownChecker checker = new ApiDownChecker.Builder()
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

        ApiDownChecker checker = new ApiDownChecker.Builder()
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
        new ApiDownChecker.Builder()
          .trust("http://trusted.api")
          .build();
    }
}
