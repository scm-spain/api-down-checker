package net.infojobs.apidownchecker;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class ApiDownCheckerTest {

    @Mock
    ApiValidator untrustedApi;

    @Mock
    ApiValidator trustedApi;

    private ApiDownChecker apiDownChecker;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        apiDownChecker = new ApiDownChecker(untrustedApi, trustedApi);
    }

    @Test
    public void is_down_when_api_not_ok_and_trusted_is_ok() throws Exception {
        when(untrustedApi.isOk()).thenReturn(false);
        when(trustedApi.isOk()).thenReturn(true);

        boolean isDown = apiDownChecker.isApiDown();

        assertTrue(isDown);
    }

    @Test
    public void is_up_when_api_ok_and_trusted_ok() throws Exception {
        when(untrustedApi.isOk()).thenReturn(true);
        when(trustedApi.isOk()).thenReturn(true);

        boolean isDown = apiDownChecker.isApiDown();

        assertFalse(isDown);
    }

    @Test
    public void is_up_when_api_ok_and_trusted_not_ok() throws Exception {
        when(untrustedApi.isOk()).thenReturn(true);
        when(trustedApi.isOk()).thenReturn(false);

        boolean isDown = apiDownChecker.isApiDown();

        assertFalse(isDown);
    }

    @Test
    public void is_up_when_api_not_ok_and_trusted_not_ok() throws Exception {
        when(untrustedApi.isOk()).thenReturn(false);
        when(trustedApi.isOk()).thenReturn(false);

        boolean isDown = apiDownChecker.isApiDown();

        assertFalse(isDown);
    }
}