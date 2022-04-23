package com.bloxbean.algodea.idea.codegen.model;

import com.algorand.algosdk.crypto.*;
import com.algorand.algosdk.logic.StateSchema;
import com.algorand.algosdk.transaction.Transaction;
import com.intellij.util.Base64;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class is a wrapper for {@link com.algorand.algosdk.transaction.Transaction} object.
 * This is a workaround to avoid Apache Velocity's limitation. (To access public field directly)
 */
public class TransactionEx extends Transaction {
    private Transaction txn;
    private AssetParamsEx assetParamsEx;

    public TransactionEx(Transaction transaction) {
        this.txn = transaction;
        this.assetParamsEx = new AssetParamsEx(this.txn.assetParams);
    }

    public String getType() {
        return txn.type.toValue();
    }

    public String getSender() {
        return toAddressString(txn.sender);
    }

    public BigInteger getFee() {
        return txn.fee;
    }

    public BigInteger getFirstValid() {
        return txn.firstValid;
    }

    public BigInteger getLastValid() {
        return txn.lastValid;
    }

    public String getNote() {
        if (txn.note != null && txn.note.length > 0) {
            return Base64.encode(txn.note);
        } else {
            return null;
        }
    }

    public String getGenesisID() {
        return txn.genesisID;
    }

    public Digest getGenesisHash() {
        return txn.genesisHash;
    }

    public Digest getGroup() {
        return txn.group;
    }

    public String getLease() {
        if (txn.lease != null && txn.lease.length > 0) {
            return Base64.encode(txn.lease);
        } else {
            return null;
        }
    }

    public String getRekeyTo() {
        return toAddressString(txn.rekeyTo);
    }

    public BigInteger getAmount() {
        return txn.amount;
    }

    public String getReceiver() {
        return toAddressString(txn.receiver);
    }

    public String getCloseRemainderTo() {
        return toAddressString(txn.closeRemainderTo);
    }

    public ParticipationPublicKey getVotePK() {
        return txn.votePK;
    }

    public VRFPublicKey getSelectionPK() {
        return txn.selectionPK;
    }

    public MerkleVerifier getStateProofKey() {
        return txn.stateProofKey;
    }

    public boolean isNonpart() {
        return txn.nonpart;
    }

    public BigInteger getVoteFirst() {
        return txn.voteFirst;
    }

    public BigInteger getVoteLast() {
        return txn.voteLast;
    }

    public BigInteger getVoteKeyDilution() {
        return txn.voteKeyDilution;
    }

    public AssetParamsEx getAssetParams() {
        return assetParamsEx;
    }

    public BigInteger getAssetIndex() {
        return txn.assetIndex;
    }

    public BigInteger getXferAsset() {
        return txn.xferAsset;
    }

    public BigInteger getAssetAmount() {
        return txn.assetAmount;
    }

    public String getAssetSender() {
        return toAddressString(txn.assetSender);
    }

    public String getAssetReceiver() {
        return toAddressString(txn.assetReceiver);
    }

    public String getAssetCloseTo() {
        return toAddressString(txn.assetCloseTo);
    }

    public String getFreezeTarget() {
        return toAddressString(txn.freezeTarget);
    }

    public BigInteger getAssetFreezeID() {
        return txn.assetFreezeID;
    }

    public boolean isFreezeState() {
        return txn.freezeState;
    }

    public List<String> getApplicationArgs() {
        if (txn.applicationArgs != null && txn.applicationArgs.size() > 0) {
            return txn.applicationArgs.stream()
                    .map(bytes -> Base64.encode(bytes))
                    .collect(Collectors.toList());
        } else
            return null;
    }

    public OnCompletion getOnCompletion() {
        return txn.onCompletion;
    }

    public TEALProgram getApprovalProgram() {
        return txn.approvalProgram;
    }

    public List<String> getAccounts() {
        if (txn.accounts == null)
            return Collections.EMPTY_LIST;
        else
            return txn.accounts.stream().map(address ->toAddressString(address))
                    .collect(Collectors.toList());
    }

    public List<Long> getForeignApps() {
        return txn.foreignApps;
    }

    public List<Long> getForeignAssets() {
        return txn.foreignAssets;
    }

    public StateSchema getGlobalStateSchema() {
        return txn.globalStateSchema;
    }

    public Long getApplicationId() {
        return txn.applicationId;
    }

    public StateSchema getLocalStateSchema() {
        return txn.localStateSchema;
    }

    public TEALProgram getClearStateProgram() {
        return txn.clearStateProgram;
    }

    public Long getExtraPages() {
        return txn.extraPages;
    }

    private String toAddressString(Address address) {
        try {
            if (address == null || address.encodeAsString().startsWith("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")) {
                return null;
            } else {
                return address.encodeAsString();
            }
        } catch (Exception e) {
            return null;
        }
    }
}
