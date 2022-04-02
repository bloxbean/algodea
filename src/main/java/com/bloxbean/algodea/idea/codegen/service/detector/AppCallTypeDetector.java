package com.bloxbean.algodea.idea.codegen.service.detector;

import com.algorand.algosdk.transaction.Transaction;
import com.bloxbean.algodea.idea.codegen.service.TxnType;

public class AppCallTypeDetector implements TransactionTypeDetector{
    @Override
    public TxnType detect(Transaction transaction) {
        if (transaction.type != Transaction.Type.ApplicationCall)
            return null;

        if (transaction.applicationId == 0) {
            return TxnType.APP_CREATE;
        } else if(transaction.applicationId != 0 && transaction.onCompletion == Transaction.OnCompletion.NoOpOC) {
            return TxnType.APP_CALL;
        } else if(transaction.applicationId != 0 && transaction.onCompletion == Transaction.OnCompletion.OptInOC) {
            return TxnType.APP_OPTIN;
        } else if(transaction.applicationId != 0 && transaction.onCompletion == Transaction.OnCompletion.DeleteApplicationOC) {
            return TxnType.APP_DELETE;
        } else if(transaction.applicationId != 0 && transaction.onCompletion == Transaction.OnCompletion.UpdateApplicationOC) {
            return TxnType.APP_UPDATE;
        } else if(transaction.applicationId != 0 && transaction.onCompletion == Transaction.OnCompletion.CloseOutOC) {
            return TxnType.APP_CLOSEOUT;
        } else if(transaction.applicationId != 0 && transaction.onCompletion == Transaction.OnCompletion.ClearStateOC) {
            return TxnType.APP_CLEARSTATE;
        } else
            return null;
    }

}
