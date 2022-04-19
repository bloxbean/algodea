package com.bloxbean.algodea.idea.codegen.model;

import com.algorand.algosdk.crypto.LogicsigSignature;
import com.algorand.algosdk.crypto.MultisigSignature;
import com.algorand.algosdk.util.Encoder;
import com.bloxbean.algodea.idea.codegen.service.util.TxnType;
import com.bloxbean.algodea.idea.nodeint.model.LogicSigType;
import com.intellij.util.Base64;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class CodeGenInfo {
    private String tealFile;
    private String approvalFile;
    private String clearStateFile;

    private byte[] logic;
    private List<byte[]> logicArgs;

    private byte[] sig;
    private MultisigSignature msig;

    private LogicSigType logicSigType;

    private TxnType txnType;

    public String getTealFile() {
        return tealFile;
    }

    public void setTealFile(String tealFile) {
        this.tealFile = tealFile;
    }

    public String getApprovalFile() {
        return approvalFile;
    }

    public void setApprovalFile(String approvalFile) {
        this.approvalFile = approvalFile;
    }

    public String getClearStateFile() {
        return clearStateFile;
    }

    public void setClearStateFile(String clearStateFile) {
        this.clearStateFile = clearStateFile;
    }

    //Set logic signature
    public void setLogicSig(byte[] logicSig, LogicSigType logicSigType) {
        this.logicSigType = logicSigType;
        try {
            LogicsigSignature logicsigSignature = Encoder.decodeFromMsgPack(logicSig, LogicsigSignature.class);
            logic = logicsigSignature.logic;
            logicArgs = logicsigSignature.args;

            if (logicsigSignature.sig != null)
                sig = logicsigSignature.sig.getBytes();

            if (logicsigSignature.msig != null)
                msig = logicsigSignature.msig;

        } catch (IOException e) {
            throw new RuntimeException("Unable to create LogicSignature from logicsig bytes", e);
        }
    }

    public String getLogicSigType() {
        if (logicSigType != null)
            return logicSigType.toString();
        else
            return null;
    }

    //As base64
    public String getLogic() {
        if (logic == null || logic.length == 0)
            return null;
        else
            return Base64.encode(logic);
    }

    public List<String> getLogicArgs() {
        if (logicArgs == null || logicArgs.size() == 0)
            return null;
        else
            return logicArgs.stream().map(bytes -> Base64.encode(bytes)).collect(Collectors.toList());
    }

    public String getSig() {
        if (sig == null || sig.length == 0)
            return null;
        else
            return Base64.encode(sig);
    }

    public MultisigSignature getMsig() {
        return msig;
    }

    public String getTxnType() {
        if (txnType != null)
            return txnType.toString();
        else
            return null;
    }

    public void setTxnType(TxnType txnType) {
        this.txnType = txnType;
    }
}
