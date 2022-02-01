package com.bloxbean.algodea.idea.core.action.ui;

import com.bloxbean.algodea.idea.nodeint.common.RequestMode;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;

public abstract class TxnDialogWrapper extends DialogWrapper {
    protected RequestMode requestMode = RequestMode.TRANSACTION;
    protected Action exportSignedAction;
    protected Action exportUnsignedAction;
    protected Action dryRunAction;
    protected Action debugAction;
    protected Action dryrunDumpAction;
    protected Action codegenAction;

    private boolean enableDryRun;
    private boolean enableDryRunDump;
    private boolean enableDebug;
    private boolean enableCodegen;

    protected TxnDialogWrapper(@Nullable Project project, boolean canBeParent) {
        super(project, canBeParent);
        exportSignedAction = new RequestAction("Export signed Tx", RequestMode.EXPORT_SIGNED);
        exportUnsignedAction = new RequestAction("Export Tx", RequestMode.EXPORT_UNSIGNED);
        dryRunAction = new RequestAction("Dry Run", RequestMode.DRY_RUN);
        dryRunAction.setEnabled(false);

        debugAction = new RequestAction("Debug", RequestMode.DEBUG);
        debugAction.setEnabled(false);

        dryrunDumpAction = new RequestAction("Dry Run Dump", RequestMode.DRYRUN_DUMP);
        dryrunDumpAction.setEnabled(false);

        codegenAction = new RequestAction("Generate Code", RequestMode.CODE_GENERATE);
    }

    protected TxnDialogWrapper(@Nullable Project project) {
        this(project, true);
    }

    @Override
    protected @NotNull Action[] createLeftSideActions() {

        return new Action[]{
                codegenAction,
                exportUnsignedAction,
                exportSignedAction,
                dryRunAction,
                dryrunDumpAction,
                debugAction,
        };
    }

    public RequestMode getRequestMode() {
        return requestMode;
    }

    @Override
    protected void doOKAction() {
        super.doOKAction();
    }

    @Override
    protected @Nullable ValidationInfo doValidate() {
        ValidationInfo validationInfo = doTransactionInputValidation();
        if(validationInfo == null) {
            exportUnsignedAction.setEnabled(true);
            exportSignedAction.setEnabled(true);

            if(enableDryRun) {
                dryRunAction.setEnabled(true);
            }
            if(enableDryRunDump) {
                dryrunDumpAction.setEnabled(true);
            }
            if(enableDebug) {
                debugAction.setEnabled(true);
            }

            return null;
        } else {
            exportUnsignedAction.setEnabled(false);
            exportSignedAction.setEnabled(false);

            if(enableDryRun) {
                dryRunAction.setEnabled(false);
            }
            if(enableDryRunDump) {
                dryrunDumpAction.setEnabled(false);
            }
            if(enableDebug) {
                debugAction.setEnabled(false);
            }
            return validationInfo;
        }
    }

    public void enableDryRun() {
        enableDryRun = true;
        dryRunAction.setEnabled(enableDryRun);
    }

    public void enableDryRunDump() {
        enableDryRunDump = true;
        dryrunDumpAction.setEnabled(enableDryRunDump);
    }

    public void enableDebug() {
        enableDebug = true;
        debugAction.setEnabled(enableDebug);
    }

    protected abstract ValidationInfo doTransactionInputValidation();

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
                if(infoList.stream().anyMatch(info1 -> !info1.okEnabled)) return;
            }
            requestMode = reqMode;
            doOKAction();
        }
    }

}
