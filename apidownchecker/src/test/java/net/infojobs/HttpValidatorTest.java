package net.infojobs;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.addRequestProcessingDelay;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class HttpValidatorTest {

    private static final int CLIENT_TIMEOUT = 1;

    private HttpValidator httpValidator;
    private OkHttpClient okHttpClient;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule();

    @Before
    public void setUp() throws Exception {
        okHttpClient = new OkHttpClient.Builder().readTimeout(CLIENT_TIMEOUT, TimeUnit.SECONDS).build();
        httpValidator = new HttpValidator(okHttpClient, "http://localhost:8080/ping");
    }

    @Test
    public void is_ok_when_response_is_200() throws Exception {
        stubFor(get(urlEqualTo("/ping"))
                .willReturn(aResponse()
                        .withStatus(200))
        );

        boolean isOk = httpValidator.isOk();

        assertTrue(isOk);
    }

    @Test
    public void is_not_ok_when_response_is_500() throws Exception {
        stubFor(get(urlEqualTo("/ping"))
                .willReturn(aResponse()
                        .withStatus(500))
        );

        boolean isOk = httpValidator.isOk();

        assertFalse(isOk);
    }

    @Test
    public void is_not_ok_when_response_timeout() throws Exception {
        addRequestProcessingDelay((int) TimeUnit.SECONDS.toMillis(5));

        stubFor(get(urlEqualTo("/ping"))
                .willReturn(aResponse()
                        .withStatus(200))
        );

        boolean isOk = httpValidator.isOk();

        assertFalse(isOk);
    }

    @Test
    public void is_not_ok_when_host_not_found() throws Exception {
        // throws UnknownHostException
        httpValidator = new HttpValidator(okHttpClient, "http://nonexistinghost/ping");

        boolean isOk = httpValidator.isOk();

        assertFalse(isOk);
    }
}