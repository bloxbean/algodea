package com.bloxbean.algodea.idea.codegen.service;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.transaction.Transaction;
import com.bloxbean.algodea.idea.codegen.service.exception.CodeGenerationException;
import com.bloxbean.algodea.idea.configuration.model.NodeInfo;
import com.bloxbean.algodea.idea.nodeint.service.LogListener;

import java.util.List;

public class PythonSdkCodeGenerator implements SdkCodeGenerator {

    @Override
    public List<FileContent> generateCode(Transaction transaction, TxnType type, Account signer, NodeInfo nodeInfo, String targetFileName, LogListener logListener) throws CodeGenerationException {
        return null;
    }
}
