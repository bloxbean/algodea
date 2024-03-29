package com.bloxbean.algodea.idea.atomic.ui;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.crypto.Digest;
import com.algorand.algosdk.crypto.LogicsigSignature;
import com.algorand.algosdk.transaction.SignedTransaction;
import com.algorand.algosdk.util.Encoder;
import com.bloxbean.algodea.idea.account.model.AlgoAccount;
import com.bloxbean.algodea.idea.account.service.AccountService;
import com.bloxbean.algodea.idea.atomic.model.AtomicTransaction;
import com.bloxbean.algodea.idea.common.AlgoConstants;
import com.bloxbean.algodea.idea.common.AlgoIcons;
import com.bloxbean.algodea.idea.configuration.action.ConfigurationAction;
import com.bloxbean.algodea.idea.core.action.util.AlgoContractModuleHelper;
import com.bloxbean.algodea.idea.dryrun.ui.DryRunContextEntryDialog;
import com.bloxbean.algodea.idea.nodeint.common.RequestMode;
import com.bloxbean.algodea.idea.nodeint.exception.DeploymentTargetNotConfigured;
import com.bloxbean.algodea.idea.nodeint.model.DryRunContext;
import com.bloxbean.algodea.idea.nodeint.service.AtomicTransactionService;
import com.bloxbean.algodea.idea.util.IdeaUtil;
import com.bloxbean.algodea.idea.util.JsonUtil;
import com.intellij.icons.AllIcons;
import com.intellij.ide.DataManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class AtomicTransferDialog extends DialogWrapper {
    private final static Logger LOG = Logger.getInstance(AtomicTransferDialog.class);

    private JList txnList;
    private JButton signButton;
    private JPanel mainPanel;
    private JScrollPane txnFileScrollPane;
    private JButton groupBtn;
    private JButton resetButton;
    private JButton addTxnBtn;
    private JTextField groupIdTf;

    private String txnFileFolder;
    private String buildFolder;
    private TxnListDataModel txnFileListModel;

    protected RequestMode requestMode = RequestMode.TRANSACTION;
    protected Action dryRunAction;
    protected Action dryRunDumpAction;
    protected Action debugAction;

    private Project project;
    private Module module;

    public AtomicTransferDialog(@Nullable Project project, Module module) {
        super(project, true);
        dryRunAction = new RequestAction("Dry Run", RequestMode.DRY_RUN);
        dryRunDumpAction = new RequestAction("Dry Run Dump", RequestMode.DRYRUN_DUMP);
        debugAction = new RequestAction("Debug", RequestMode.DEBUG);

        init();
        setTitle("Atomic Transfer");

        this.project = project;
        this.module = module;

        txnList.setModel(txnFileListModel);

        try {
            buildFolder = AlgoContractModuleHelper.getBuildFolder(project, module);

            VirtualFile txnFileFolderVf = AlgoContractModuleHelper.getTxnOutputFolder(project, module);
            if(txnFileFolderVf != null)
                txnFileFolder = txnFileFolderVf.getCanonicalPath();
            else
                txnFileFolder = buildFolder;
        } catch (Exception exception) {

        }
        reset();
        attachAddTxnBtnListener();
        attachGroupBtnListener();
        attachSignListener();
        attachResetBtnListener();
        attachTxnListPopupListener();
    }

    private void attachAddTxnBtnListener() {
        addTxnBtn.addActionListener(e -> {
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
    }

    private void attachResetBtnListener() {
        resetButton.addActionListener(e -> {
            reset();
        });
    }

    private void attachGroupBtnListener() {
        groupBtn.addActionListener(e -> {
            List<AtomicTransaction> atomicTransactions = txnFileListModel.getAtomicTransactions();
            if(atomicTransactions == null || atomicTransactions.size() == 0)
                return;

            try {
                Digest gid = AtomicTransactionService.assignGroup(atomicTransactions.stream().map(at -> at.getTransaction()).collect(Collectors.toList()));
                atomicTransactions.stream().forEach(atomicTransaction -> atomicTransaction.setSignedTransaction(null)); //Clear signed transactions
                groupingDone();
                if(gid != null)
                    groupIdTf.setText(Encoder.encodeToBase64(gid.getBytes()));

                Messages.showInfoMessage("Group id successfully assigned", "Transaction grouping");
            } catch (IOException ioException) {
                IdeaUtil.showNotification(project, "Atomic Transaction",
                        "Group assignment failed :  " + ioException.getMessage(), NotificationType.ERROR, null);
                return;
            }
        });
    }

    private void attachSignListener() {
        signButton.addActionListener(e -> {
            AtomicTransaction atomicTransaction = getSelectedTxn();
            try {
                signTransaction(atomicTransaction);
            } catch (DeploymentTargetNotConfigured ex) {
                IdeaUtil.showNotification(project, "Sign Transaction", "Algorand Node for deployment node is not configured. Click here to configure.",
                        NotificationType.ERROR, ConfigurationAction.ACTION_ID);
            }
        });
    }

    private void signTransaction(AtomicTransaction atomicTransaction) throws DeploymentTargetNotConfigured {
        if (atomicTransaction == null) {
            Messages.showWarningDialog("Please select a transaction to sign", "Sign Transaction");
            return;
        }
        if(atomicTransaction ==  null || atomicTransaction.getTransaction() == null) return;

        Address address = atomicTransaction.getTransaction().sender;
        AccountService accountService = AccountService.getAccountService();
        AlgoAccount algoAccount = accountService.getAccountByAddress(address.toString());

        SigningAccountInputDialog singingAccDialog = new SigningAccountInputDialog(project, module);
        if(algoAccount != null) {
            singingAccDialog.getAccountEntryInputForm().setSenderAddress(project, algoAccount);
        }

        boolean ok = singingAccDialog.showAndGet();
        if(ok) {
            atomicTransaction.setSignedTransaction(null);
            if(singingAccDialog.isAccountType()) {
                Account account = singingAccDialog.getAccount();
                if (account != null) {
                    try {
                        SignedTransaction signedTransaction = account.signTransaction(atomicTransaction.getTransaction());
                        atomicTransaction.setSignedTransaction(signedTransaction);
                    } catch (Exception exception) {
                        if(LOG.isDebugEnabled())
                            LOG.warn(exception);
                        Messages.showErrorDialog("Signing by account failed. Please select a valid account. \nReason: "
                                + exception.getMessage(), "Trasaction signing");
                    }
                } else {
                    Messages.showErrorDialog("Signing by account failed. Please select a valid account", "Trasaction signing");
                }
            } else {
                LogicsigSignature logicsigSignature = singingAccDialog.getLogicSignature();
                if(logicsigSignature != null) {
                    try {
                        SignedTransaction signedTransaction
                                = Account.signLogicsigTransaction(logicsigSignature, atomicTransaction.getTransaction());
                        atomicTransaction.setSignedTransaction(signedTransaction);
                    } catch (Exception exception) {
                        if(LOG.isDebugEnabled())
                            LOG.warn(exception);
                        Messages.showErrorDialog("Signing by logic sig file failed. Please select a valid logic sig file. \nReason: "
                                + exception.getMessage(), "Trasaction signing");
                    }
                } else {
                    Messages.showErrorDialog("Please select a valid logic sig file", "Trasaction signing");
                }
            }
            txnFileListModel.fireUpdate();
        }
    }

    @Nullable
    private AtomicTransaction getSelectedTxn() {
        int index = txnList.getSelectedIndex();
        if(index == -1 || txnFileListModel.size() < index + 1) {
            return null;
        }

        AtomicTransaction atomicTransaction = (AtomicTransaction)txnFileListModel.getElementAt(index);
        return atomicTransaction;
    }

    private void groupingDone() {
        addTxnBtn.setEnabled(false);
        groupBtn.setEnabled(false);
        signButton.setEnabled(true);
    }

    private void reset() {
        txnFileListModel.clear();
        txnFileListModel.fireUpdate();
        groupIdTf.setText("");

        addTxnBtn.setEnabled(true);
        groupBtn.setEnabled(true);
        signButton.setEnabled(false);
    }

    private boolean groupAssignmentDone() {
        if(!StringUtil.isEmpty(groupIdTf.getText()))
            return true;
        else
            return false;
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

    public String getGroupId() {
        return groupIdTf.getText();
    }

    public List<DryRunContext.Source> getDryContextSources() {
        int count = txnFileListModel.getSize();
        List<DryRunContext.Source> sources = new ArrayList<>();
        for(int i=0;i<count; i++) {
            AtomicTransaction atomicTransaction = (AtomicTransaction) txnFileListModel.getElementAt(i);
            DryRunContext.Source source = atomicTransaction.getDryRunSource();
            if(source == null)
                continue;

            sources.add(source);
        }

        return sources;
    }

    protected @Nullable ValidationInfo doAtomicTransactionInputValidation() {
        if(txnFileListModel.getAtomicTransactions() == null ||
                txnFileListModel.getAtomicTransactions().size() == 0) {
            return new ValidationInfo("No transaction is found. Please add transactions to create a group", txnList);
        }

        if(StringUtil.isEmpty(groupIdTf.getText())) {
            return new ValidationInfo("Group id is not found. Please click \"Create Group\" to assign group id.", groupIdTf);
        }

        List<AtomicTransaction> emptySignedTxnList
                = txnFileListModel.getAtomicTransactions().stream().filter(at -> at.getSignedTransaction() == null).collect(Collectors.toList());
        if(emptySignedTxnList != null && emptySignedTxnList.size() > 0) {
            return new ValidationInfo("Please sign all transactions before submitting the group transaction.", txnList);
        }

        return null;
    }

    @Override
    protected @Nullable ValidationInfo doValidate() {
        ValidationInfo validationInfo = doAtomicTransactionInputValidation();
        if(validationInfo == null) {
            dryRunAction.setEnabled(true);
            dryRunDumpAction.setEnabled(true);
            debugAction.setEnabled(true);
            return null;
        } else {
            dryRunAction.setEnabled(false);
            dryRunDumpAction.setEnabled(false);
            debugAction.setEnabled(false);
            return validationInfo;
        }
    }

    @Override
    protected @NotNull Action[] createLeftSideActions() {

        return new Action[]{
                dryRunAction,
                dryRunDumpAction,
                debugAction
        };
    }

    public RequestMode getRequestMode() {
        return requestMode;
    }

    //Table row popup
    private void attachTxnListPopupListener() {
        txnList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                tableRowPopupMenuHandler(e);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                tableRowPopupMenuHandler(e);
            }
        });
    }

    private void tableRowPopupMenuHandler(MouseEvent e) {
        if (e.isPopupTrigger() && e.getComponent() instanceof JBList ) {
            AtomicTransaction atomicTransaction = getSelectedTxn();
            if(atomicTransaction == null)
                return;

            int selectedIndex = txnList.getSelectedIndex();
            ListPopup popup = createPopup(atomicTransaction, selectedIndex);
            RelativePoint relativePoint = new RelativePoint(e.getComponent(), new Point(e.getX(), e.getY()));
            popup.show(relativePoint);
        }
    }

    private ListPopup createPopup(AtomicTransaction atomicTransaction, int selectedIndex) {
        final DefaultActionGroup group = new DefaultActionGroup();

        if(groupAssignmentDone()) {
            group.add(signTxnAction(atomicTransaction));
        }
        group.add(showTxnDetailsAction(atomicTransaction));

        if(!groupAssignmentDone()) {
            group.add(removeTxnFromListAction(atomicTransaction));
        }

        group.add(captureDryRunSourceAction(atomicTransaction, selectedIndex));

        DataContext dataContext = DataManager.getInstance().getDataContext(txnList);
        return JBPopupFactory.getInstance().createActionGroupPopup("",
                group, dataContext, JBPopupFactory.ActionSelectionAid.MNEMONICS, true);
    }

    private AnAction showTxnDetailsAction(AtomicTransaction atomicTransaction) {
        return new AnAction("Show Details", "Show Details", AllIcons.General.InspectionsEye) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                if(atomicTransaction == null || atomicTransaction.getTransaction() == null)
                    return;
                Object toShow = null;
                if(atomicTransaction.getSignedTransaction() != null)
                    toShow = atomicTransaction.getSignedTransaction();
                else {
                    toShow = atomicTransaction.getTransaction();
                }

                ShowTxnDetailsDialog showTxnDetailsDialog
                        = new ShowTxnDetailsDialog(project, atomicTransaction.getTransaction(), JsonUtil.getPrettyJson(toShow));
                showTxnDetailsDialog.setEnableContentField(false);
                showTxnDetailsDialog.show();
            }
        };
    }

    private AnAction signTxnAction(AtomicTransaction atomicTransaction) {
        return new AnAction("Sign", "Sign", AllIcons.Actions.Edit) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                if(atomicTransaction != null) {
                    try {
                        signTransaction(atomicTransaction);
                    } catch (DeploymentTargetNotConfigured ex) {
                        if(LOG.isDebugEnabled()) {
                            LOG.error("Error in signing process", ex);
                        }
                        IdeaUtil.showNotification(project, "Sign Transaction", "Algorand Node for deployment node is not configured. Click here to configure.",
                                NotificationType.ERROR, ConfigurationAction.ACTION_ID);
                    }
                }
            }
        };
    }

    private AnAction removeTxnFromListAction(AtomicTransaction atomicTransaction) {
        return new AnAction("Remove", "Remove", AllIcons.General.Remove) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                if(atomicTransaction == null) return;
                txnFileListModel.removeElement(atomicTransaction);
            }
        };
    }

    private AnAction captureDryRunSourceAction(AtomicTransaction atomicTransaction, int selectedIndex) {
        return new AnAction("DryRun Source", "Configure DryRun Source", AlgoIcons.TEAL_FILE_ICON) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                if(atomicTransaction == null) return;

                DryRunContext.Source source = atomicTransaction.getDryRunSource();

                Long appId = atomicTransaction.getTransaction().applicationId;
                List<Long> appIds = appId != null ? Arrays.asList(appId): Collections.EMPTY_LIST;
                DryRunContextEntryDialog dialog = new DryRunContextEntryDialog(project, appIds, Collections.EMPTY_LIST, Collections.EMPTY_LIST,
                        appId != null && appId != 0, false, true);

                if(source != null) {
                    dialog.getDryRunSourceInputForm().setSource(source);
                }

                if(selectedIndex != -1) {
                    dialog.getDryRunSourceInputForm().setTxnIndex(selectedIndex);
                    dialog.getDryRunSourceInputForm().disableTxnIndex();
                }

                boolean ok = dialog.showAndGet();
                if(!ok) return;

                source = dialog.getDryRunSource();
                if(source != null)
                    atomicTransaction.setDryRunSource(source);
            }
        };
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return mainPanel;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code herer
        txnFileListModel = new TxnListDataModel();
        txnList = new JBList();
        txnFileScrollPane = new JBScrollPane(txnList);
    }

    class RequestAction extends DialogWrapperAction {
        private RequestMode reqMode;

        protected RequestAction(String label, RequestMode reqMode) {
            super(label);
            this.reqMode = reqMode;
        }

        @Override
        protected void doAction(ActionEvent e) {
//            recordAction("DialogOkAction", EventQueue.getCurrentEvent());
            List<ValidationInfo> infoList = doValidateAll();
            if (!infoList.isEmpty()) {
                ValidationInfo info = infoList.get(0);

                startTrackingValidation();
                if (infoList.stream().anyMatch(info1 -> !info1.okEnabled)) return;
            }
            requestMode = reqMode;
            doOKAction();
        }
    }
}
