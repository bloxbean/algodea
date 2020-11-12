package com.bloxbean.algodea.idea.atomic.ui;

import com.algorand.algosdk.transaction.Transaction;
import com.bloxbean.algodea.idea.atomic.model.AtomicTransaction;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TxnListDataModel extends DefaultListModel<AtomicTransaction> {

    public List<AtomicTransaction> getAtomicTransactions() {
        int size = getSize();
        if(size == 0)
            return Collections.EMPTY_LIST;

        List<AtomicTransaction> atomicTransactions = new ArrayList<>();
        for(int i=0; i<size; i++) {
            atomicTransactions.add(getElementAt(i));
        }

        return atomicTransactions;
    }

    public void fireUpdate() {
        fireContentsChanged(this, 0, getSize()-1);
    }
}
