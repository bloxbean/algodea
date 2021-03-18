package com.bloxbean.algodea.idea.util;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.Base64;

public class ArgConversionUtilTest {

    @Test
    public void intToBase64() {
        int intVal = Integer.parseInt("123");
        byte[] bytes = ByteUtil.bigIntegerToBytes(new BigInteger("123"), 8);
        String str = Base64.getEncoder().encodeToString(bytes);

        System.out.println(str);
        Assert.assertEquals("AAAAAAAAAHs=", str);
    }
}
