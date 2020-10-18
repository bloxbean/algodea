package com.bloxbean.algodea.idea.core.util;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.crypto.Ed25519PublicKey;
import com.algorand.algosdk.crypto.MultisigAddress;
import com.bloxbean.algodea.idea.account.model.AlgoMultisigAccount;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class AlgoAccountUtil {
    public static MultisigAddress getMultisigAddress(AlgoMultisigAccount algoMultisigAccount) throws NoSuchAlgorithmException {
        List<Ed25519PublicKey> accPubKeys = new ArrayList<>();
        if(algoMultisigAccount.getAccounts() != null && algoMultisigAccount.getAccounts().size() > 0) {
            for(String acc: algoMultisigAccount.getAccounts()) {
                Ed25519PublicKey publicKey = new Ed25519PublicKey(new Address(acc).getBytes());
                accPubKeys.add(publicKey);
            }
        }

        MultisigAddress multisigAddress = new MultisigAddress(algoMultisigAccount.getVersion(), algoMultisigAccount.getThreshold(), accPubKeys);

        return multisigAddress;
    }
}
