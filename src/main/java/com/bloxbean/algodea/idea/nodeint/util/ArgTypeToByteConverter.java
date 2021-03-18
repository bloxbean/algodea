package com.bloxbean.algodea.idea.nodeint.util;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.util.Encoder;
import com.bloxbean.algodea.idea.nodeint.exception.InvalidInputParamException;
import com.bloxbean.algodea.idea.nodeint.model.ArgType;
import com.bloxbean.algodea.idea.util.ByteUtil;
import com.twelvemonkeys.lang.StringUtil;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;

public class ArgTypeToByteConverter {
    public static byte[] convert(ArgType type, String value) throws UnsupportedEncodingException,
            NoSuchAlgorithmException, InvalidInputParamException {
        if(StringUtil.isEmpty(value))
            return null;

        try {
            if (type == ArgType.String) {
                return value.getBytes(StandardCharsets.UTF_8);
            } else if (type == ArgType.Integer) {
                try {
                    return ByteUtil.bigIntegerToBytes(new BigInteger(value), 8);
                } catch (NumberFormatException ex) {
                    throw new InvalidInputParamException("Invalid Argument value for integer type : " + value);
                }
            } else if (type == ArgType.Address) {
                return new Address(value).getBytes();
            } else if (type == ArgType.Base64) {
                return Encoder.decodeFromBase64(value);
            } else {
                return null;
            }

        } catch (Exception e) {
            throw new InvalidInputParamException("Invalid argument value for type : " + type + ", value : " + value);
        }
    }
}
