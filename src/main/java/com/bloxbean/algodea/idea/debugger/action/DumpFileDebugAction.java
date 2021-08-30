package com.bloxbean.algodea.idea.debugger.action;

import com.bloxbean.algodea.idea.core.action.AlgoBaseAction;
import com.bloxbean.algodea.idea.debugger.service.DebugHandler;
import com.bloxbean.algodea.idea.toolwindow.AlgoConsole;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import java.io.File;

import static com.bloxbean.algodea.idea.common.AlgoConstants.ALGO_DEBUGGER_CONTEXT_FILE_EXT;

public class DumpFileDebugAction extends AlgoBaseAction {
    private final static Logger LOG = Logger.getInstance(DumpFileDebugAction.class);

    public DumpFileDebugAction() {
        super("Start TEAL Debugger", "Start TEAL Debugger", AllIcons.Actions.StartDebugger);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);

        if (isAlgoProject(e)) {
            VirtualFile file = e.getDataContext().getData(CommonDataKeys.VIRTUAL_FILE);
            if (file.getName().endsWith(ALGO_DEBUGGER_CONTEXT_FILE_EXT)) {
                e.getPresentation().setEnabledAndVisible(true);
            } else {
                e.getPresentation().setEnabledAndVisible(false);
            }
        } else {
            e.getPresentation().setEnabledAndVisible(false);
        }

    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        if (LOG.isDebugEnabled())
            LOG.debug("OptIn Logic sig transaction");

        Project project = CommonDataKeys.PROJECT.getData(e.getDataContext());
        if (project == null) {
            return;
        }

        final AlgoConsole console = AlgoConsole.getConsole(project);
        console.clearAndshow();

        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        if (psiFile == null) {
            console.showErrorMessage("Unable read dryrun dump file");
            return;
        }

        //Build txn object
        VirtualFile dryRunDumpVirtualFile = psiFile.getVirtualFile();
        if (dryRunDumpVirtualFile == null || !dryRunDumpVirtualFile.exists()) {
            console.showErrorMessage("Unable read dryrun dump file");
            return;
        }

        File dryRunDumpFile = VfsUtil.virtualToIoFile(dryRunDumpVirtualFile);

        DebugHandler debugHandler = new DebugHandler();
        debugHandler.startDebuggerForDryDumpFile(project, dryRunDumpFile, console);
    }

    @Override
    public boolean isDumbAware() {
        return true;
    }
}
