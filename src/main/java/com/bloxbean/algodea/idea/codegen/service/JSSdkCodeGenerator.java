package com.bloxbean.algodea.idea.codegen.service;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.transaction.Transaction;
import com.bloxbean.algodea.idea.codegen.service.exception.CodeGenerationException;
import com.bloxbean.algodea.idea.configuration.model.NodeInfo;
import com.bloxbean.algodea.idea.nodeint.service.LogListener;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class JSSdkCodeGenerator implements SdkCodeGenerator {
    private final static String JS_CREATE_APP_TEMPLATE = "_sdk_js.createApp.js";
    private final static String JS_CALL_APP_TEMPLATE = "_sdk_js.callApp.js";
    private final static String JS_OPTIN_APP_TEMPLATE = "_sdk_js.optInApp.js";
    private static final String JS_DELETE_APP_TEMPLATE = "_sdk_js.deleteApp.js";
    private static final String JS_UPDATE_APP_TEMPLATE = "_sdk_js.updateApp.js";
    private static final String JS_CLOSE_OUT_APP_TEMPLATE = "_sdk_js.closeOutApp.js";
    private static final String JS_CLEAR_STATE_APP_TEMPLATE = "_sdk_js.clearStateApp.js";

    private final static String JS_PACKAGE_JSON = "_sdk_js.package.json";

    private final static String TARGET_JS_FILE = "TARGET_JS_FILE";

    @Override
    public List<FileContent> generateCode(Transaction transaction, TxnType type, Account signer, NodeInfo nodeInfo,
                                          String targetFileName, LogListener logListener) throws CodeGenerationException {
        if (type == TxnType.APP_CREATE) {
            if (targetFileName == null)
                targetFileName = "createApp";
            return createAppTxnFromTemplate(JS_CREATE_APP_TEMPLATE, transaction, signer, nodeInfo, targetFileName, logListener);
        } else if (type == TxnType.APP_CALL) {
            if (targetFileName == null)
                targetFileName = "callApp";
            return createAppTxnFromTemplate(JS_CALL_APP_TEMPLATE, transaction, signer, nodeInfo, targetFileName, logListener);
        } else if (type == TxnType.APP_OPTIN) {
            if (targetFileName == null)
                targetFileName = "optInApp";
            return createAppTxnFromTemplate(JS_OPTIN_APP_TEMPLATE, transaction, signer, nodeInfo, targetFileName, logListener);
        } else if (type == TxnType.APP_DELETE) {
            if (targetFileName == null)
                targetFileName = "deleteApp";
            return createAppTxnFromTemplate(JS_DELETE_APP_TEMPLATE, transaction, signer, nodeInfo, targetFileName, logListener);
        } else if (type == TxnType.APP_UPDATE) {
            if (targetFileName == null)
                targetFileName = "updateApp";
            return createAppTxnFromTemplate(JS_UPDATE_APP_TEMPLATE, transaction, signer, nodeInfo, targetFileName, logListener);
        } else if (type == TxnType.APP_CLOSEOUT) {
            if (targetFileName == null)
                targetFileName = "closeOutApp";
            return createAppTxnFromTemplate(JS_CLOSE_OUT_APP_TEMPLATE, transaction, signer, nodeInfo, targetFileName, logListener);
        } else if (type == TxnType.APP_CLEARSTATE) {
            if (targetFileName == null)
                targetFileName = "clearStateApp";
            return createAppTxnFromTemplate(JS_CLEAR_STATE_APP_TEMPLATE, transaction, signer, nodeInfo, targetFileName, logListener);
        } else
            throw new CodeGenerationException("Code generation no supported for the txn type: " + type);
    }

    @NotNull
    private List<FileContent> createAppTxnFromTemplate(String template, Transaction transaction, Account signer, NodeInfo nodeInfo, String targetFileName, LogListener logListener) throws CodeGenerationException {
        String content = createAppTxn(template, transaction, signer, nodeInfo, logListener);

        String packageJsonContent = getPackageJsonContent(targetFileName);

        return Arrays.asList(
                new FileContent(targetFileName, ".js", content, false),
                new FileContent("package.json", ".json", packageJsonContent, true)
        );
    }

    private String getPackageJsonContent(String targetFileName) {
        //package json content
        Map<String, String> pkgJsonProps = new HashMap<>();
        if (targetFileName.endsWith(".js"))
            pkgJsonProps.put(TARGET_JS_FILE, targetFileName);
        else
            pkgJsonProps.put(TARGET_JS_FILE, targetFileName + ".js");
        String packageJsonContent = mergeContent(JS_PACKAGE_JSON, pkgJsonProps);
        return packageJsonContent;
    }

    private String  createAppTxn(String template, Transaction transaction, Account signer, NodeInfo nodeInfo, LogListener logListener) throws CodeGenerationException {
        Map<String, String> props = getProperties(transaction, signer, nodeInfo, logListener);

        try {
            return mergeContent(template, props);
        } catch (Exception exception) {
            throw new CodeGenerationException("Could not generate JS code", exception);
        }
    }

}
