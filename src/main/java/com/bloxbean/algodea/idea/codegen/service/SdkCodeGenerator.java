package com.bloxbean.algodea.idea.codegen.service;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.logic.StateSchema;
import com.algorand.algosdk.transaction.Transaction;
import com.bloxbean.algodea.idea.codegen.model.CodeGenInfo;
import com.bloxbean.algodea.idea.codegen.model.StateSchemaEx;
import com.bloxbean.algodea.idea.codegen.model.TransactionEx;
import com.bloxbean.algodea.idea.codegen.service.exception.CodeGenerationException;
import com.bloxbean.algodea.idea.codegen.service.util.FileContent;
import com.bloxbean.algodea.idea.codegen.service.util.TxnType;
import com.bloxbean.algodea.idea.configuration.model.NodeInfo;
import com.bloxbean.algodea.idea.nodeint.service.LogListener;
import com.bloxbean.algodea.idea.util.JsonUtil;
import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.ide.fileTemplates.FileTemplateUtil;
import com.intellij.util.Base64;

import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.bloxbean.algodea.idea.codegen.service.util.TransactionPropConstant.*;

public interface SdkCodeGenerator {

    List<FileContent> generateCode(Transaction transaction, TxnType type, Account signer, NodeInfo nodeInfo, CodeGenInfo codeGenInfo, String targetFileName, LogListener logListener) throws CodeGenerationException;

    default String mergeContent(String templateName, Map<String, Object> props) {
        final FileTemplate template = FileTemplateManager.getDefaultInstance().getInternalTemplate(templateName);
        String content = FileTemplateUtil.mergeTemplate(props, template.getText(), true);
        return content;
    }

    default Map<String, Object> getProperties(Transaction txn, Account signer, NodeInfo nodeInfo, CodeGenInfo codeGenInfo,
                                              LogListener logListener) {
        Map<String, Object> props = new HashMap<>();

        if (nodeInfo != null) {
            try {
                URL url = new URL(nodeInfo.getNodeAPIUrl());

                props.put(ALGOD_SERVER, url.getProtocol() + "://" + url.getHost());
                if (url.getPort() >= 0) {
                    props.put(ALGOD_PORT, String.valueOf(url.getPort()));
                } else {
                    props.put(ALGOD_PORT, "");
                }
                props.put(ALGOD_TOKEN, nodeInfo.getApiKey());
            } catch (Exception e) {
                props.put(ALGOD_SERVER, "");
                props.put(ALGOD_PORT, "");
                props.put(ALGOD_TOKEN, "");
                logListener.warn("algod host and prot cannot be parsed from the url : " + nodeInfo.getNodeAPIUrl());
            }
        }

        if (signer != null) {
            props.put(SIGNER_MNEMONIC, signer.toMnemonic());
        } else {
            props.put(SIGNER_MNEMONIC, "");
        }

        try {
            props.put(SENDER, txn.sender.encodeAsString());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Sender address could not be retrieved", e);
        }

        if (txn.receiver != null) {
            try {
                props.put(RECEIVER, txn.receiver.encodeAsString());
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException("Receiver address could not be retrieved", e);
            }
        }

        if (txn.amount != null)
            props.put(AMOUNT, String.valueOf(txn.amount));

        if (txn.globalStateSchema != null) {
            StateSchemaEx globalStateSchemaEx = getStateSchemaEx(txn.globalStateSchema);
            String globalInts = String.valueOf(globalStateSchemaEx.getNumUint());
            String globalBytes = String.valueOf(globalStateSchemaEx.getNumByteSlice());

            props.put(GLOBAL_INTS, globalInts);
            props.put(GLOBAL_BYTES, globalBytes);
        }

        if (txn.localStateSchema != null) {
            StateSchemaEx localStateSchemaEx = getStateSchemaEx(txn.localStateSchema);
            String localInts = String.valueOf(localStateSchemaEx.getNumUint());
            String localBytes = String.valueOf(localStateSchemaEx.getNumByteSlice());

            props.put(LOCAL_INTS, localInts);
            props.put(LOCAL_BYTES, localBytes);
        }

        if (txn.approvalProgram != null)
            props.put(APPROVAL_PROG_COMPILED, Base64.encode(txn.approvalProgram.getBytes()));

        if (txn.clearStateProgram != null)
            props.put(CLEAR_STATE_PROG_COMPILED, Base64.encode(txn.clearStateProgram.getBytes()));

        if (txn.fee != null)
            props.put(FEE, txn.fee.toString());
        else
            props.put(FEE, "1000");

        props.put(GENESIS_ID, txn.genesisID);
        props.put(GENESIS_HASH, txn.genesisHash.toString());

        props.put(FIRST_VALID, txn.firstValid.toString());
        props.put(LAST_VALID, txn.lastValid.toString());

        props.put(TYPE, txn.type.toString());

        if (txn.applicationId != 0) {
            props.put(APP_ID, String.valueOf(txn.applicationId));
        } else {
            props.put(APP_ID, "");
        }

        props.put("org_txn", txn);
        props.put("txn", new TransactionEx(txn));

        if (codeGenInfo != null) {
            if (codeGenInfo.getTealFile() != null) {
                props.put(TEAL_FILE, codeGenInfo.getTealFile());
            }

            props.put("codegen_info", codeGenInfo);
        }

        return props;
    }

    default StateSchemaEx getStateSchemaEx(StateSchema stateSchema) {
        String json = JsonUtil.getPrettyJson(stateSchema);
        StateSchemaEx stateSchemaEx = JsonUtil.parseJson(json, StateSchemaEx.class);
        return stateSchemaEx;
    }

}
