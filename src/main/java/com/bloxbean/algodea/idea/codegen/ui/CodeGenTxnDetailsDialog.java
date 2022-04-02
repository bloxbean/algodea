package com.bloxbean.algodea.idea.codegen.ui;

import com.algorand.algosdk.transaction.Transaction;
import com.bloxbean.algodea.idea.atomic.ui.ShowTxnDetailsDialog;
import com.bloxbean.algodea.idea.codegen.CodeGenLang;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class CodeGenTxnDetailsDialog extends ShowTxnDetailsDialog {
    protected CodeGenAction jsCodeGenAction;
    protected CodeGenAction pythonCodeGenAction;
    protected CodeGenLang selectedLang = CodeGenLang.JS;

    public CodeGenTxnDetailsDialog(@Nullable Project project, Transaction transaction, String content) {
        super(project, transaction, content);
    }

    @Override
    protected void createCustomActions() {
        jsCodeGenAction = new CodeGenAction("Generate JS Code", CodeGenLang.JS);
        pythonCodeGenAction = new CodeGenAction("Generate Python Code", CodeGenLang.PYTHON);
        setOkButtonLabel("Generate JS Code");
    }

    @Override
    protected Action @NotNull [] createLeftSideActions() {
        return new Action[]{
                jsCodeGenAction,
                pythonCodeGenAction
        };
    }

    @Override
    protected @NotNull Action[] createActions() {
        return new Action[]{
                getCancelAction()
        };
    }

    public CodeGenLang getSelectedLang() {
        return selectedLang;
    }

    public void setOkButtonLabel(String txt) {
        setOKButtonText(txt);
    }

    @Override
    protected void doOKAction() {
        super.doOKAction();
    }

    class CodeGenAction extends DialogWrapperAction {
        private CodeGenLang lang;

        protected CodeGenAction(String label, CodeGenLang lang) {
            super(label);
            this.lang = lang;
        }

        @Override
        protected void doAction(ActionEvent e) {
            selectedLang = lang;
            doOKAction();
        }
    }

}
