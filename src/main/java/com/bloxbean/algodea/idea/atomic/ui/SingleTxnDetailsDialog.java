package com.bloxbean.algodea.idea.atomic.ui;

import com.algorand.algosdk.transaction.Transaction;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nullable;

public class SingleTxnDetailsDialog extends ShowTxnDetailsDialog {

    public SingleTxnDetailsDialog(@Nullable Project project, Transaction transaction, String content) {
        super(project, transaction, content);

        setOKButtonText("Sign & Send");
    }

    public void setOkButtonLabel(String txt) {
        setOKButtonText(txt);
    }
}
