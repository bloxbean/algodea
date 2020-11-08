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

    protected TxnDialogWrapper(@Nullable Project project, boolean canBeParent) {
        super(project, canBeParent);
        exportSignedAction = new ExportTransactionAction("Export (Signed)", RequestMode.EXPORT_SIGNED);
        exportUnsignedAction = new ExportTransactionAction("Export (Unsigned)", RequestMode.EXPORT_UNSIGNED);
    }

    protected TxnDialogWrapper(@Nullable Project project) {
        super(project, true);
    }

    @Override
    protected @NotNull Action[] createLeftSideActions() {
        return new Action[] {
               exportSignedAction,
                exportUnsignedAction
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
            return null;
        } else {
            exportUnsignedAction.setEnabled(false);
            exportSignedAction.setEnabled(false);
            return validationInfo;
        }
    }

    protected abstract ValidationInfo doTransactionInputValidation();

    class ExportTransactionAction extends DialogWrapperAction {
        private RequestMode reqMode;
        protected ExportTransactionAction(String label, RequestMode reqMode) {
            super(label);
            this.reqMode = reqMode;
        }

        @Override
        protected void doAction(ActionEvent e) {
//            recordAction("DialogOkAction", EventQueue.getCurrentEvent());
            List<ValidationInfo> infoList = doValidateAll();
            if (!infoList.isEmpty()) {
                ValidationInfo info = infoList.get(0);
//                if (info.component != null && info.component.isVisible()) {
//                    IdeFocusManager.getInstance(null).requestFocus(info.component, true);
//                }
//
//                if (!Registry.is("ide.inplace.validation.tooltip")) {
//                    DialogEarthquakeShaker.shake(getPeer().getWindow());
//                }

                startTrackingValidation();
                if(infoList.stream().anyMatch(info1 -> !info1.okEnabled)) return;
            }
            requestMode = reqMode;
            doOKAction();
        }

//        @Override
//        protected void doAction(ActionEvent e) {
////            if (doValidate() == null) {
////                getOKAction().setEnabled(true);
////            } else {
////                getOKAction().setEnabled(false);
////                this.setEnabled(false);
////            }
//            // set implementation specific values to signal that this custom button was the cause for closing the dialog
//            // .....
//            requestMode = reqMode;
//            myOKAction.actionPerformed(e);
//        }
    }
}
