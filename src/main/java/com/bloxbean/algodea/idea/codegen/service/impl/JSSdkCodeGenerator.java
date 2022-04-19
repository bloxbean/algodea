package com.bloxbean.algodea.idea.codegen.service.impl;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.transaction.Transaction;
import com.bloxbean.algodea.idea.codegen.model.CodeGenInfo;
import com.bloxbean.algodea.idea.codegen.service.SdkCodeGenerator;
import com.bloxbean.algodea.idea.codegen.service.exception.CodeGenerationException;
import com.bloxbean.algodea.idea.codegen.service.util.FileContent;
import com.bloxbean.algodea.idea.codegen.service.util.TxnType;
import com.bloxbean.algodea.idea.configuration.model.NodeInfo;
import com.bloxbean.algodea.idea.nodeint.service.LogListener;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

public class JSSdkCodeGenerator implements SdkCodeGenerator {
    private final static String JS_CREATE_APP_TEMPLATE = "_sdk_js.createApp.js";
    private final static String JS_CALL_APP_TEMPLATE = "_sdk_js.callApp.js";
    private final static String JS_OPTIN_APP_TEMPLATE = "_sdk_js.optInApp.js";
    private static final String JS_DELETE_APP_TEMPLATE = "_sdk_js.deleteApp.js";
    private static final String JS_UPDATE_APP_TEMPLATE = "_sdk_js.updateApp.js";
    private static final String JS_CLOSE_OUT_APP_TEMPLATE = "_sdk_js.closeOutApp.js";
    private static final String JS_CLEAR_STATE_APP_TEMPLATE = "_sdk_js.clearStateApp.js";

    //Stateless contracts
    private static final String JS_LOGIC_SIG_CONTRACT_TEMPLATE = "_sdk_js.logicSigTxn.js";
    private static final String JS_LOGIC_SIG_DELEGATION_TEMPLATE = "_sdk_js.logicSigTxn.js";

    //Asset txns
    private static final String JS_ASSET_TXN_TEMPLATE = "_sdk_js.assetTxn.js";

    //Payment txn (Algo & ASA)
    private static final String JS_PAYMENT_TXN_TEMPLATE = "_sdk_js.paymentTxn.js";

    private final static String JS_PACKAGE_JSON = "_sdk_js.package.json";

    private final static String TARGET_JS_FILE = "TARGET_JS_FILE";

    @Override
    public List<FileContent> generateCode(Transaction transaction, TxnType type, Account signer, NodeInfo nodeInfo, CodeGenInfo codeGenInfo,
                                          String targetFileName, LogListener logListener) throws CodeGenerationException {
        if (type == TxnType.APP_CREATE) {
            if (targetFileName == null)
                targetFileName = "createApp";
            return createTxnFromTemplate(JS_CREATE_APP_TEMPLATE, transaction, signer, nodeInfo, codeGenInfo, targetFileName, logListener);
        } else if (type == TxnType.APP_CALL) {
            if (targetFileName == null)
                targetFileName = "callApp";
            return createTxnFromTemplate(JS_CALL_APP_TEMPLATE, transaction, signer, nodeInfo, codeGenInfo, targetFileName, logListener);
        } else if (type == TxnType.APP_OPTIN) {
            if (targetFileName == null)
                targetFileName = "optInApp";
            return createTxnFromTemplate(JS_OPTIN_APP_TEMPLATE, transaction, signer, nodeInfo, codeGenInfo, targetFileName, logListener);
        } else if (type == TxnType.APP_DELETE) {
            if (targetFileName == null)
                targetFileName = "deleteApp";
            return createTxnFromTemplate(JS_DELETE_APP_TEMPLATE, transaction, signer, nodeInfo, codeGenInfo, targetFileName, logListener);
        } else if (type == TxnType.APP_UPDATE) {
            if (targetFileName == null)
                targetFileName = "updateApp";
            return createTxnFromTemplate(JS_UPDATE_APP_TEMPLATE, transaction, signer, nodeInfo, codeGenInfo, targetFileName, logListener);
        } else if (type == TxnType.APP_CLOSEOUT) {
            if (targetFileName == null)
                targetFileName = "closeOutApp";
            return createTxnFromTemplate(JS_CLOSE_OUT_APP_TEMPLATE, transaction, signer, nodeInfo, codeGenInfo, targetFileName, logListener);
        } else if (type == TxnType.APP_CLEARSTATE) {
            if (targetFileName == null)
                targetFileName = "clearStateApp";
            return createTxnFromTemplate(JS_CLEAR_STATE_APP_TEMPLATE, transaction, signer, nodeInfo, codeGenInfo, targetFileName, logListener);
        } else if (type == TxnType.LOGIC_SIG_CONTRACT) {
            if (targetFileName == null) {
                targetFileName = getStatelessTargetFileName(codeGenInfo);
                targetFileName += "_contract";
            }
            return createTxnFromTemplate(JS_LOGIC_SIG_CONTRACT_TEMPLATE, transaction, signer, nodeInfo, codeGenInfo, targetFileName, logListener);
        } else if (type == TxnType.LOGIC_SIG_DELEGATION) {
            if (targetFileName == null) {
                targetFileName = getStatelessTargetFileName(codeGenInfo);
                targetFileName += "_delegation";
            }
            return createTxnFromTemplate(JS_LOGIC_SIG_DELEGATION_TEMPLATE, transaction, signer, nodeInfo, codeGenInfo, targetFileName, logListener);
        } else if (type == TxnType.ASSET_CFG_CREATE
                || type == TxnType.ASSET_CFG_MODIFY
                || type == TxnType.ASSET_CFG_DESTROY
                || type == TxnType.ASSET_OPTIN
                || type == TxnType.ASSET_FRZ
                || type == TxnType.ASSET_UNFRZ
                || type == TxnType.ASSET_REVOKE
        ) {
            if (targetFileName == null)
                targetFileName = type.toString().toLowerCase();
            return createTxnFromTemplate(JS_ASSET_TXN_TEMPLATE, transaction, signer, nodeInfo, codeGenInfo, targetFileName, logListener);

        } else if (type == TxnType.TRANSFER_ALGO
                || type == TxnType.TRANSFER_ASA) {

            if (targetFileName == null)
                targetFileName = type.toString().toLowerCase();
            return createTxnFromTemplate(JS_PAYMENT_TXN_TEMPLATE, transaction, signer, nodeInfo, codeGenInfo, targetFileName, logListener);
        } else
            throw new CodeGenerationException("Code generation is not supported for the txn type: " + type);
    }

    @NotNull
    private List<FileContent> createTxnFromTemplate(String template, Transaction transaction, Account signer, NodeInfo nodeInfo,
                                                    CodeGenInfo codeGenInfo, String targetFileName, LogListener logListener)
            throws CodeGenerationException {
        String content = createTxn(template, transaction, signer, nodeInfo, codeGenInfo, logListener);

        String packageJsonContent = getPackageJsonContent(targetFileName);

        return Arrays.asList(
                new FileContent(targetFileName, ".js", content, false),
                new FileContent("package.json", ".json", packageJsonContent, true)
        );
    }

    private String getPackageJsonContent(String targetFileName) {
        //package json content
        Map<String, Object> pkgJsonProps = new HashMap<>();
        if (targetFileName.endsWith(".js"))
            pkgJsonProps.put(TARGET_JS_FILE, targetFileName);
        else
            pkgJsonProps.put(TARGET_JS_FILE, targetFileName + ".js");
        String packageJsonContent = mergeContent(JS_PACKAGE_JSON, pkgJsonProps);
        return packageJsonContent;
    }

    private String createTxn(String template, Transaction transaction, Account signer, NodeInfo nodeInfo,
                             CodeGenInfo codeGenInfo, LogListener logListener) throws CodeGenerationException {
        Map<String, Object> props = getProperties(transaction, signer, nodeInfo, codeGenInfo, logListener);

        try {
            return mergeContent(template, props);
        } catch (Exception exception) {
            throw new CodeGenerationException("Could not generate JS code. " + exception.getMessage(), exception);
        }
    }

    private String getStatelessTargetFileName(CodeGenInfo codeGenInfo) {
        if (codeGenInfo.getTealFile() == null) return "stateless";

        String tealFile = codeGenInfo.getTealFile();

        String tealFileName = new File(tealFile).getName();
        return tealFileName.replaceAll(".teal", "");
    }

}
