package com.bloxbean.algodea.idea.nodeint.util;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.crypto.LogicsigSignature;
import com.algorand.algosdk.crypto.MultisigSignature;
import com.algorand.algosdk.util.Encoder;
import com.bloxbean.algodea.idea.nodeint.model.LogicSigType;
import com.intellij.openapi.util.io.FileUtil;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class AlgoLogicsigUtil {

    public static Address getAddressFromLogicSig(byte[] logicSigBytes) throws NoSuchAlgorithmException, IOException {
        LogicsigSignature logicsigSignature = Encoder.decodeFromMsgPack(logicSigBytes, LogicsigSignature.class);
        return logicsigSignature.toAddress();
    }

    public static LogicsigSignature getLogicSigFromFile(String lsigFilePath) throws IOException {
        byte[] bytes = FileUtil.loadFileBytes(new File(lsigFilePath));

        if(bytes == null)
            return null;

        LogicsigSignature logicsigSignature = Encoder.decodeFromMsgPack(bytes, LogicsigSignature.class);
        return logicsigSignature;
    }

    public static LogicSigType getType(LogicsigSignature logicsigSignature) {
        if(logicsigSignature == null)
            return null;

        if(logicsigSignature.sig == null && logicsigSignature.msig == null)
            return LogicSigType.CONTRACT_ACCOUNT;
        else
            return LogicSigType.DELEGATION_ACCOUNT;
    }

    public static List<MultisigSigner> getMultisigSigners(LogicsigSignature logicsigSignature) {
        LogicSigType type = getType(logicsigSignature);
        if(type == null || LogicSigType.CONTRACT_ACCOUNT.equals(type))
            return null;

        //account deligation
        boolean isMultiSig = logicsigSignature.msig != null;

        if(isMultiSig) {
            List<MultisigSigner> signers = new ArrayList<>();
            for(MultisigSignature.MultisigSubsig subSig: logicsigSignature.msig.subsigs) {
                if(subSig != null) {
                    MultisigSigner multisigSigner = new MultisigSigner();
                    multisigSigner.signer = new Address(subSig.key.getBytes());
                    if(subSig.sig != null)
                        multisigSigner.signature = subSig.sig.getBytes();
                }
            }
            return signers;
        } else {
            return null;
        }
    }

    public static boolean isMultisigDelegatedAccount(LogicsigSignature logicsigSignature) {
        LogicSigType type = getType(logicsigSignature);
        if(type == null || LogicSigType.CONTRACT_ACCOUNT.equals(type))
            return false;

        //account deligation
        return logicsigSignature.msig != null;
    }

    public static int getMultisigThreshold(LogicsigSignature logicsigSignature) {
        LogicSigType type = getType(logicsigSignature);
        if(type == null || LogicSigType.CONTRACT_ACCOUNT.equals(type))
            return 0;

        //account deligation
        boolean isMultiSig = logicsigSignature.msig != null;

        if(isMultiSig) {
            return logicsigSignature.msig.threshold;
        } else {
            return 0;
        }
    }

}
