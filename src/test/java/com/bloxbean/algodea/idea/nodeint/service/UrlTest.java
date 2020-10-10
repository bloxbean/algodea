package com.bloxbean.algodea.idea.nodeint.service;

import com.squareup.okhttp.HttpUrl;
import org.junit.Test;

import static org.junit.Assert.*;

public class UrlTest {

    @Test
    public void testUrl() {
        HttpUrl url = HttpUrl.parse("http://localhost:4001");
        int port = url.port();
        String host = url.host();

        assertEquals(4001, port);
        assertEquals("localhost", host);
    }

    @Test
    public void testUrl2() {
        HttpUrl url = HttpUrl.parse("https://testnet-algorand.api.purestake.io/ps2");
        int port = url.port();
        String host = url.host();

        assertEquals(443, port);
        assertEquals("testnet-algorand.api.purestake.io", host);
        assertEquals("https", url.scheme());
    }

}
