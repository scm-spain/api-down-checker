package com.schibsted.spain.apidownchecker;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ApiDownCheckerTest {

    private static final int FIRST_CHECK_TIME = 110 * 1000;
    private static final int SECOND_CHECK_AFTER_10_SECONDS = 120 * 1000;
    private static final int SECOND_CHECK_AFTER_11_SECONDS = 121 * 1000;
    @Mock
    ApiValidator untrustedApi;

    @Mock
    ApiValidator trustedApi;

    private ApiDownChecker apiDownChecker;
    private MockDateProvider date;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        date = new MockDateProvider();
        apiDownChecker = new ApiDownChecker(untrustedApi, trustedApi, Logger.NONE, date);
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

    @Test
    public void checks_once_when_two_request_made_in_10_seconds() throws Exception {
        when(untrustedApi.isOk()).thenReturn(false);
        when(trustedApi.isOk()).thenReturn(true);
        date.setFixed(FIRST_CHECK_TIME);

        apiDownChecker.isApiDown();
        date.setFixed(SECOND_CHECK_AFTER_10_SECONDS);
        apiDownChecker.isApiDown();

        verify(untrustedApi, times(1)).isOk();
        verify(trustedApi, times(1)).isOk();
    }

    @Test
    public void checks_twice_when_two_request_made_in_11_seconds() throws Exception {
        when(untrustedApi.isOk()).thenReturn(false);
        when(trustedApi.isOk()).thenReturn(true);
        date.setFixed(FIRST_CHECK_TIME);

        apiDownChecker.isApiDown();
        date.setFixed(SECOND_CHECK_AFTER_11_SECONDS);
        apiDownChecker.isApiDown();

        verify(untrustedApi, times(2)).isOk();
        verify(trustedApi, times(2)).isOk();
    }

    @Test
    public void is_up_on_second_check_when_made_in_10_seconds_if_was_up() throws Exception {
        when(untrustedApi.isOk()).thenReturn(true);
        date.setFixed(FIRST_CHECK_TIME);

        boolean firstCheck = apiDownChecker.isApiDown();
        date.setFixed(SECOND_CHECK_AFTER_10_SECONDS);
        boolean secondCheck = apiDownChecker.isApiDown();

        assertFalse(firstCheck);
        assertFalse(secondCheck);
    }

    @Test
    public void is_down_on_second_check_when_made_in_10_seconds_if_was_down() throws Exception {
        when(untrustedApi.isOk()).thenReturn(false);
        when(trustedApi.isOk()).thenReturn(true);
        date.setFixed(FIRST_CHECK_TIME);

        boolean firstCheck = apiDownChecker.isApiDown();
        date.setFixed(SECOND_CHECK_AFTER_10_SECONDS);
        boolean secondCheck = apiDownChecker.isApiDown();

        assertTrue(firstCheck);
        assertTrue(secondCheck);
    }
}