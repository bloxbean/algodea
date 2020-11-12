package com.bloxbean.algodea.idea.atomic.ui;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.transaction.SignedTransaction;
import com.bloxbean.algodea.idea.account.model.AlgoAccount;
import com.bloxbean.algodea.idea.account.service.AccountService;
import com.bloxbean.algodea.idea.atomic.model.AtomicTransaction;
import com.bloxbean.algodea.idea.common.AlgoConstants;
import com.bloxbean.algodea.idea.core.action.util.AlgoContractModuleHelper;
import com.bloxbean.algodea.idea.nodeint.service.AtomicTransactionService;
import com.bloxbean.algodea.idea.util.IdeaUtil;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AtomicTransferDialog extends DialogWrapper {
    private final static Logger LOG = Logger.getInstance(AtomicTransferDialog.class);

    private JList txnList;
    private TextFieldWithBrowseButton selectTxnFile;
    private JButton signButton;
    private JPanel mainPanel;
    private JScrollPane txnFileScrollPane;
    private JButton groupBtn;
    private JButton resetButton;
    private JTextField txnFileTf;

    private String txnFileFolder;
    private String buildFolder;
    private TxnListDataModel txnFileListModel;

    public AtomicTransferDialog(@Nullable Project project, Module module) {
        super(project, true);
        init();
        setTitle("Atomic Transfer");

        txnList.setModel(txnFileListModel);

        try {
            buildFolder = AlgoContractModuleHelper.getBuildFolder(project);

            VirtualFile txnFileFolderVf = AlgoContractModuleHelper.getTxnOutputFolder(module);
            if(txnFileFolderVf != null)
                txnFileFolder = txnFileFolderVf.getCanonicalPath();
            else
                txnFileFolder = buildFolder;
        } catch (Exception exception) {

        }

        reset();
        attachGroupBtnListener(project);
        attachSignListener(project);
        attachResetBtnListener(project);
    }

    private void attachResetBtnListener(Project project) {
        resetButton.addActionListener(e -> {
            reset();
        });
    }

    private void attachGroupBtnListener(Project project) {
        groupBtn.addActionListener(e -> {
            List<AtomicTransaction> atomicTransactions = txnFileListModel.getAtomicTransactions();
            if(atomicTransactions == null || atomicTransactions.size() == 0)
                return;

            try {
                AtomicTransactionService.assignGroup(atomicTransactions.stream().map(at -> at.getTransaction()).collect(Collectors.toList()));
                atomicTransactions.stream().forEach(atomicTransaction -> atomicTransaction.setSignedTransaction(null)); //Clear signed transactions
                groupingDone();
                Messages.showInfoMessage("Group id successfully assigned", "Transaction grouping");
            } catch (IOException ioException) {
                IdeaUtil.showNotification(project, "Atomic Transaction",
                        "Group assignment failed :  " + ioException.getMessage(), NotificationType.ERROR, null);
                return;
            }
        });
    }

    private void attachSignListener(@Nullable Project project) {
        signButton.addActionListener(e -> {
            int index = txnList.getSelectedIndex();
            if(index == -1 || txnFileListModel.size() < index + 1) {
                Messages.showWarningDialog("Please select a transaction to sign", "Sign Transaction");
                return;
            }

            AtomicTransaction atomicTransaction = (AtomicTransaction)txnFileListModel.getElementAt(index);
            if(atomicTransaction ==  null || atomicTransaction.getTransaction() == null) return;

            Address address = atomicTransaction.getTransaction().sender;
            AccountService accountService = AccountService.getAccountService();
            AlgoAccount algoAccount = accountService.getAccountByAddress(address.toString());

            SigningAccountInputDialog singingAccDialog = new SigningAccountInputDialog(project);
            if(algoAccount != null) {
                singingAccDialog.getAccountEntryInputForm().setMnemonic(algoAccount.getMnemonic());
            }

            boolean ok = singingAccDialog.showAndGet();
            if(ok) {
                Account account = singingAccDialog.getAccount();
                try {
                    SignedTransaction signedTransaction = account.signTransaction(atomicTransaction.getTransaction());
                    atomicTransaction.setSignedTransaction(signedTransaction);
                    txnFileListModel.fireUpdate();
                } catch (Exception exception) {
                    if(LOG.isDebugEnabled()) {
                        LOG.warn(exception);
                    }
                }
            }
        });
    }

    private void groupingDone() {
        groupBtn.setEnabled(false);
        signButton.setEnabled(true);
        selectTxnFile.setEnabled(false);
    }

    private void reset() {
        txnFileListModel.clear();
        txnFileListModel.fireUpdate();

        groupBtn.setEnabled(true);
        signButton.setEnabled(false);
        selectTxnFile.setEnabled(true);
    }

    public List<SignedTransaction> getSignedTransactions() {
        int count = txnFileListModel.getSize();
        List<SignedTransaction> transactions = new ArrayList<>();
        for(int i=0;i<count; i++) {
            AtomicTransaction atomicTransaction = (AtomicTransaction) txnFileListModel.getElementAt(i);
            SignedTransaction signedTransaction = atomicTransaction.getSignedTransaction();
            if(signedTransaction == null)
                return null;
            transactions.add(signedTransaction);
        }

        return transactions;
    }

    public byte[] getGroupTransactionBytes() throws IOException {
        List<AtomicTransaction> atomicTransactions = txnFileListModel.getAtomicTransactions();
        if(atomicTransactions == null || atomicTransactions.size() == 0)
            return null;

        byte[] groupTxnBytes = AtomicTransactionService.assembleTransactionGroup(
                atomicTransactions.stream().map(atomicTransaction -> atomicTransaction.getSignedTransaction()).collect(Collectors.toList()));
        return groupTxnBytes;
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return mainPanel;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        txnFileTf = new JTextField();
        selectTxnFile = new TextFieldWithBrowseButton(txnFileTf, e -> {
            JFileChooser fc = new JFileChooser();
            if(txnFileFolder != null) {
                File txnFileFolderObj = new File(txnFileFolder);
                if(txnFileFolderObj.exists()) {
                    fc.setCurrentDirectory(txnFileFolderObj);
                } else {
                    fc.setCurrentDirectory(new File(buildFolder));
                }
            }
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fc.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    if(f.getName().endsWith(AlgoConstants.ALGO_TXN_FILE_EXT))
                        return true;
                    else
                        return false;
                }

                @Override
                public String getDescription() {
                    return "Algorand Txn file";
                }
            });
            fc.showDialog(mainPanel, "Select");
            File file = fc.getSelectedFile();
            if (file == null) {
                return;
            }

            try {
                AtomicTransaction atomicTransaction = AtomicTransaction.loadTransaction(file);
                txnFileListModel.addElement(atomicTransaction);
            } catch (IOException ioException) {
                Messages.showErrorDialog("Invalid transaction file", "Transaction Parse Error");
                return;
            }

        });

        txnFileListModel = new TxnListDataModel();
        txnList = new JBList();
        txnFileScrollPane = new JBScrollPane(txnList);
    }
}
