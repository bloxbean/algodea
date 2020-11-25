package com.bloxbean.algodea.idea.nodeint.model;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class DryRunContext {
    public List<Long> appIds = new ArrayList<>();
    public List<String> addresses = new ArrayList<>();
    public Long latestTimestamp;
    public BigInteger round;
    public String protocol;
    public List<Source> sources;

    public static class Source {
        public String code;
        public String type;
        public BigInteger appIndex;
        public long txnIndex;
    }
}
