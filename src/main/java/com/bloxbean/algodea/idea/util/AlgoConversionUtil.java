package com.bloxbean.algodea.idea.util;

import java.math.BigDecimal;
import java.math.BigInteger;

public class AlgoConversionUtil {
    private static final BigInteger ONE_ALGO = new BigInteger("1000000"); //1 Algo

    public static BigInteger algoTomAlgo(double aion) {
        BigDecimal bigDecimalAmt = new BigDecimal(aion);
        BigDecimal nAmp = new BigDecimal(ONE_ALGO).multiply(bigDecimalAmt);

        return nAmp.toBigInteger();
    }

    public static float mAlgoToAlgo(double mAlgo) {
        BigDecimal bigDecimalAmt = new BigDecimal(mAlgo);
        float algo = bigDecimalAmt.divide(new BigDecimal(ONE_ALGO)).floatValue();

        return algo;
    }

    public static float mAlgoToAlgo(BigInteger mAlogo) {
        BigDecimal bigDecimalAmt = new BigDecimal(mAlogo);
        float algo = bigDecimalAmt.divide(new BigDecimal(ONE_ALGO)).floatValue();

        return algo;
    }
}
