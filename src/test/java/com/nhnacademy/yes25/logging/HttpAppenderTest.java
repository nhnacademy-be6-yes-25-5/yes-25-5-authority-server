package com.nhnacademy.yes25.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import okhttp3.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class HttpAppenderTest {

    private HttpAppender httpAppender;
    private OkHttpClient mockClient;
    private Call mockCall;
    private ILoggingEvent mockEvent;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        mockClient = mock(OkHttpClient.class);
        mockCall = mock(Call.class);
        mockEvent = mock(ILoggingEvent.class);

        httpAppender = new HttpAppender();
        httpAppender.setUrl("http://example.com");
        httpAppender.setProjectName("TestProject");
        httpAppender.setProjectVersion("1.0");
        httpAppender.setLogVersion("1.0");
        httpAppender.setLogSource("TestSource");
        httpAppender.setLogType("TestType");
        httpAppender.setHost("localhost");
        httpAppender.setSecretKey("secret");
        httpAppender.setPlatform("Java");

        // Use reflection to set the mock OkHttpClient
        Field clientField = HttpAppender.class.getDeclaredField("client");
        clientField.setAccessible(true);
        clientField.set(httpAppender, mockClient);

        when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);
    }

    @Test
    void testAppend() throws IOException {
        when(mockEvent.getFormattedMessage()).thenReturn("Test log message");
        when(mockEvent.getLevel()).thenReturn(ch.qos.logback.classic.Level.INFO);

        ArgumentCaptor<Request> requestCaptor = ArgumentCaptor.forClass(Request.class);

        doAnswer(invocation -> {
            Callback callback = invocation.getArgument(0);
            Response mockResponse = new Response.Builder()
                    .request(new Request.Builder().url("http://example.com").build())
                    .protocol(Protocol.HTTP_1_1)
                    .code(200)
                    .message("OK")
                    .body(ResponseBody.create("", MediaType.get("application/json")))
                    .build();
            callback.onResponse(mockCall, mockResponse);
            return null;
        }).when(mockCall).enqueue(any(Callback.class));

        httpAppender.append(mockEvent);

        verify(mockClient).newCall(requestCaptor.capture());
        Request request = requestCaptor.getValue();
        assertEquals("POST", request.method());
        assertEquals("http://example.com/", request.url().toString());
    }

    @Test
    void testAppendFailure() throws IOException {
        when(mockEvent.getFormattedMessage()).thenReturn("Test log message");
        when(mockEvent.getLevel()).thenReturn(ch.qos.logback.classic.Level.INFO);

        doAnswer(invocation -> {
            Callback callback = invocation.getArgument(0);
            callback.onFailure(mockCall, new IOException("Failed to send log event"));
            return null;
        }).when(mockCall).enqueue(any(Callback.class));

        httpAppender.append(mockEvent);

        verify(mockClient).newCall(any(Request.class));
    }
}
