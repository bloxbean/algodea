package com.bloxbean.algodea.idea.util;

import com.intellij.openapi.util.text.StringUtil;

public class StringUtility {

    public static String padRight(String s, int n) {
        if(s == null)
            return null;
        if(s != null && s.length() > n) {
            s = StringUtil.trimLog(s,n);
        }
        return String.format("%1$" + n + "s", s);
    }

    public static String padLeft(String s, int n) {
        if(s == null)
            return null;
        if(s != null && s.length() > n) {
            s = StringUtil.trimLog(s, n);
        }

        return String.format("%1$-" + n + "s", s);
    }
}
