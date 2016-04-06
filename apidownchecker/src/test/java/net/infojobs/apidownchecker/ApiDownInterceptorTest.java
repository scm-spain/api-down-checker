package net.infojobs.apidownchecker;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

public class ApiDownInterceptorTest {

    private static final String MY_API_ENDPOINT = "http://localhost:8080";
    private static final String WORKING_API_URL = "http://localhost:8080/working";
    private static final String BROKEN_API_URL = "http://localhost:8080/broken";

    @Rule
    public WireMockRule wireMockRule = new WireMockRule();

    @Before
    public void setUp() throws Exception {
        stubFor(get(urlEqualTo("/working"))
          .willReturn(aResponse()
            .withStatus(200))
        );
        stubFor(get(urlEqualTo("/broken"))
          .willReturn(aResponse()
            .withStatus(500))
        );
    }

    @Test(expected = net.infojobs.apidownchecker.ApiDownException.class)
    public void default_interceptor_throws_exception_when_request_unsuccessful_if_api_down() throws Exception {
        ApiDownInterceptor interceptor = ApiDownChecker.create()
          .check(BROKEN_API_URL)
          .trust(WORKING_API_URL)
          .buildInterceptor();

        executeUnsuccessfulRequest(interceptor);
    }

    @Test
    public void default_interceptor_doesnt_throw_exception_when_request_unsuccessful_if_api_up() throws Exception {
        ApiDownInterceptor interceptor = ApiDownChecker.create()
          .check(WORKING_API_URL)
          .trust(WORKING_API_URL)
          .buildInterceptor();

        executeUnsuccessfulRequest(interceptor);
    }

    @Test
    public void default_interceptor_doesnt_throw_exception_when_request_successful() throws Exception {
        ApiDownInterceptor interceptor = ApiDownChecker.create()
          .check(WORKING_API_URL)
          .trust(WORKING_API_URL)
          .buildInterceptor();

        executeWorkingRequest(interceptor);
    }

    @Test(expected = net.infojobs.apidownchecker.ApiDownException.class)
    public void default_interceptor_throws_exception_when_request_fails_if_api_down() throws Exception {
        ApiDownInterceptor interceptor = ApiDownChecker.create()
          .check(BROKEN_API_URL)
          .trust(WORKING_API_URL)
          .buildInterceptor();

        executeFailingRequest(interceptor);
    }

    private void executeWorkingRequest(ApiDownInterceptor interceptor) throws IOException {
        stubFor(get(urlEqualTo("/method"))
          .willReturn(aResponse()
            .withStatus(200))
        );
        executeRequest(interceptor);
    }

    private void executeUnsuccessfulRequest(ApiDownInterceptor interceptor) throws IOException {
        stubFor(get(urlEqualTo("/method"))
          .willReturn(aResponse()
            .withStatus(500))
        );
        executeRequest(interceptor);
    }

    private void executeRequest(ApiDownInterceptor interceptor) throws IOException {
        OkHttpClient client = new OkHttpClient.Builder()
          .addInterceptor(interceptor)
          .build();

        Request request = new Request.Builder().get().url(MY_API_ENDPOINT + "/method").build();
        client.newCall(request).execute();
    }

    private void executeFailingRequest(ApiDownInterceptor interceptor) throws IOException {
        OkHttpClient client = new OkHttpClient.Builder()
          .addInterceptor(interceptor)
          .build();

        Request request = new Request.Builder().get().url("http://unreachable.server").build();
        client.newCall(request).execute();
    }
}