package com.bloxbean.algodea.idea.stateless.action;

import com.bloxbean.algodea.idea.module.filetypes.LSigFileType;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

public class LogicSigSendTransactionFileNodeAction extends LogicSigSendTransactionAction {
    private final static Logger LOG = Logger.getInstance(LogicSigSendTransactionFileNodeAction.class);

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);

        if(isAlgoProject(e)) {
            VirtualFile file = e.getDataContext().getData(CommonDataKeys.VIRTUAL_FILE);
            if(LSigFileType.EXTENSION.equals(file.getExtension())) {
                e.getPresentation().setEnabledAndVisible(true);
            } else {
                e.getPresentation().setEnabledAndVisible(false);
            }
        } else {
            e.getPresentation().setEnabledAndVisible(false);
        }

    }

}
