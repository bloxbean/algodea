package com.bloxbean.algodea.idea.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;

public class AlgoConversionUtil {
    private static final BigInteger ONE_ALGO = new BigInteger("1000000"); //1 Algo
    private static final int ALGO_DECIMAL = 6;

    public static BigInteger algoTomAlgo(BigDecimal amount) {
        return assetFromDecimal(amount, ALGO_DECIMAL);
    }

    public static BigDecimal mAlgoToAlgo(BigInteger amount) {
        return assetToDecimal(amount, ALGO_DECIMAL);
    }

    public static String mAlgoToAlgoFormatted(BigInteger mAlgoAmt) {
        BigDecimal amtInAlgo = assetToDecimal(mAlgoAmt, ALGO_DECIMAL); //1 Algo = 1000000
        return formatBigDecimal(amtInAlgo, ALGO_DECIMAL);
    }

//    @Deprecated
//    /*
//     * Use assetToDecimal instead
//     */
//    public static float mAlgoToAlgo(double mAlgo) {
//        BigDecimal bigDecimalAmt = new BigDecimal(mAlgo);
//        float algo = bigDecimalAmt.divide(new BigDecimal(ONE_ALGO)).floatValue();
//
//        return algo;
//    }
//
//    @Deprecated
//    /**
//     * Use assetToDecimal instead
//     */
//    public static double mAlgoToAlgo(BigInteger mAlogo) {
//        BigDecimal bigDecimalAmt = new BigDecimal(mAlogo);
//        double algo = bigDecimalAmt.divide(new BigDecimal(ONE_ALGO)).doubleValue();
//
//        return algo;
//    }

    public static BigDecimal assetToDecimal(BigInteger amount, long decimals) {
        if(decimals == 0)
            return new BigDecimal(amount);

        double oneUnit = Math.pow(10, decimals);

        BigDecimal bigDecimalAmt = new BigDecimal(amount);
        BigDecimal decimalAmt = bigDecimalAmt.divide(new BigDecimal(oneUnit));

        return decimalAmt;
    }

    public static BigInteger assetFromDecimal(BigDecimal doubleAmout, long decimals) {
        if(decimals == 0)
            return doubleAmout.toBigInteger();

        double oneUnit = Math.pow(10, decimals);

        BigDecimal amount = new BigDecimal(oneUnit).multiply(doubleAmout);

        return amount.toBigInteger();
    }

    public static String formatBigDecimal(BigDecimal amount, int decimals) {
        if(amount == null) return null;

        amount = amount.setScale(decimals, BigDecimal.ROUND_DOWN);

        DecimalFormat df = new DecimalFormat();

        df.setMaximumFractionDigits(decimals);

        df.setMinimumFractionDigits(0);

        df.setGroupingUsed(true);

        String result = df.format(amount);
        return result;
    }

    public static String toAssetDecimalAmtFormatted(BigInteger assetAmt, int decimals) {
        BigDecimal assetAmtInDecimals = assetToDecimal(assetAmt, decimals);
        return formatBigDecimal(assetAmtInDecimals, decimals);
    }
}
