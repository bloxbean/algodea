package com.bloxbean.algodea.idea.util;

import junit.framework.TestCase;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.NumberFormat;

public class AlgoConversionUtilTest extends TestCase {

    public void testAssetToDecimal() {
        BigInteger amount = new BigInteger("5000000000001");
        int decimal = 3;

        BigDecimal decimalAmt =  AlgoConversionUtil.assetToDecimal(amount, decimal);
        assertEquals(5000000000.001, decimalAmt);
    }

    public void testAssetFromDecimal() {
        BigDecimal decimalAmt = BigDecimal.valueOf(6000.670);

        BigInteger amount = AlgoConversionUtil.assetFromDecimal(decimalAmt, 3);

        System.out.println(amount);
        assertEquals(new BigInteger("6000670"), amount);
    }

    public void testAssetToDecimalWithZeroDecimal() {
        BigInteger amount = new BigInteger("5000000000051");
        int decimal = 0;

        BigDecimal decimalAmt =  AlgoConversionUtil.assetToDecimal(amount, decimal);
        assertEquals(new BigInteger("5000000000051").doubleValue(),  decimalAmt);
    }

    public void testAssetFromDecimalZeroDecimal() {
        BigDecimal decimalAmt = new BigDecimal(6000670);

        BigInteger amount = AlgoConversionUtil.assetFromDecimal(decimalAmt, 0);

        System.out.println(amount);
        assertEquals(new BigInteger("6000670"), amount);
    }
}