package com.bloxbean.algodea.idea.compile.model;

import com.algorand.algosdk.crypto.LogicsigSignature;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LogicSigMetaData {
    public String contractAddress;
    public boolean isMultiDelegatedSignature;
    public boolean isDelegatedSignature;
    public List<String> signingAddresses;
    public String multisigAddress;
    public LogicsigSignature logicsigSignature;
    public String sourcePath;

    public void addSigningAddress(String signingAddress) {
        if(signingAddresses == null)
            signingAddresses = new ArrayList<>();

        signingAddresses.add(signingAddress);
    }
}
