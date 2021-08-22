package com.bloxbean.algodea.idea.debugger.service;

import com.bloxbean.algodea.idea.configuration.model.AlgoLocalSDK;
import com.bloxbean.algodea.idea.core.exception.LocalSDKNotConfigured;
import com.bloxbean.algodea.idea.nodeint.AlgoServerConfigurationHelper;
import com.github.kklisura.cdt.launch.ChromeArguments;
import com.github.kklisura.cdt.launch.ChromeLauncher;
import com.github.kklisura.cdt.launch.config.ChromeLauncherConfiguration;
import com.github.kklisura.cdt.protocol.commands.Debugger;
import com.github.kklisura.cdt.protocol.commands.Inspector;
import com.github.kklisura.cdt.protocol.commands.Network;
import com.github.kklisura.cdt.protocol.commands.Page;
import com.github.kklisura.cdt.protocol.types.page.Navigate;
import com.github.kklisura.cdt.services.ChromeDevToolsService;
import com.github.kklisura.cdt.services.ChromeService;
import com.github.kklisura.cdt.services.types.ChromeTab;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessListener;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class DebugService implements Disposable {
    private AlgoLocalSDK localSDK;
    private String cwd;
    private OSProcessHandler handler;
    private Project project;

    public DebugService(Project project) {
        this.project = project;
        this.cwd = project.getBasePath();
    }

    public void startDebugger(String[] sourceTeals, File txnFile, File dryRunReqDump, DebugResultListener listener) throws LocalSDKNotConfigured {
        localSDK = AlgoServerConfigurationHelper.getCompilerLocalSDK(project);
        if(localSDK == null) {
            throw new LocalSDKNotConfigured("Algorand Local SDK is not configured.");
        }

        DebugConfigState debugConfig = DebugConfigState.getInstance();
        String chromeExecPath = debugConfig.getChromeExecPath();
        String debugPort = debugConfig.getDebugPort();

        if(debugConfig.isAutoDetectChromePath())
            chromeExecPath = null;

        if(StringUtil.isEmpty(debugPort))
            debugPort = "9229";

        List<String> cmd = new ArrayList<>();
        cmd.add(localSDK.getHome() + File.separator + getTealDbgCmd());
        cmd.add("debug");
        cmd.add("--remote-debugging-port");
        cmd.add(debugPort);

        if(txnFile != null) {
            cmd.add("--txn");
            cmd.add(txnFile.getAbsolutePath());
        }

        if(dryRunReqDump != null) {
            cmd.add("--dryrun-req");
            cmd.add(dryRunReqDump.getAbsolutePath());
        }

        if(sourceTeals != null && sourceTeals.length > 0) {
            for(String sourceTeal: sourceTeals) {
                if(sourceTeal != null)
                    cmd.add(sourceTeal);
            }
        }

        if(handler != null) {
            listener.info("Stopping previous debug session ...");
            handler.destroyProcess();
        }

        if(handler != null && !handler.isProcessTerminated()) {
            listener.info("Another debugger process is still running...Try after sometime..");
            return;
        }

        try {
            handler = new OSProcessHandler(
                    new GeneralCommandLine(cmd).withWorkDirectory(cwd)
            );

        } catch (ExecutionException ex) {
            //ex.printStackTrace();
            failed(listener, "Debug failed : " + ex.getMessage(), ex);
            return;
        }

        listener.info("Starting TEAL debugger ...");
        listener.attachProcess(handler);

        final String chromeBinPath = chromeExecPath;
        handler.addProcessListener(new ProcessListener() {
            @Override
            public void startNotified(@NotNull ProcessEvent event) {
                listener.info("Debugger started ...");
                try {
                    listener.info("Starting Chrome ...");
                    openChrome(chromeBinPath, listener);
                } catch (Exception e) {
                    listener.error("Could not start Chrome Browser", e);
                }
            }

            @Override
            public void processTerminated(@NotNull ProcessEvent event) {
                if(event.getExitCode() == 0) {
                    listener.info("Debugger stopped.");
                } else {
                    failed(listener,"Debugger stopped", new Exception("Debugger failed. " + event.getText()));
                }
            }

            @Override
            public void onTextAvailable(@NotNull ProcessEvent event, @NotNull Key outputType) {
                //listener.info(event.getText());
            }
        });

        return;
    }

    private void openChrome(String chromeExecPath, DebugResultListener listener) {
        ChromeLauncherConfiguration configuration = new ChromeLauncherConfiguration();

        // Create chrome launcher.
        final ChromeLauncher launcher = new ChromeLauncher();
        final ChromeService chromeService ;

        if(!StringUtil.isEmpty(chromeExecPath)) {
            Path path = Paths.get(chromeExecPath);
            listener.info("Chrome path: " + path.toString());
            chromeService = launcher.launch(path, ChromeArguments.builder().headless(false).build());
        } else {
            chromeService = launcher.launch(false);
        }

        // Create empty tab ie about:blank.
        final ChromeTab tab = chromeService.createTab();

        // Get DevTools service to this tab
        final ChromeDevToolsService devToolsService = chromeService.createDevToolsService(tab);


        // Get individual commands
        final Page page = devToolsService.getPage();
        final Network network = devToolsService.getNetwork();
        Debugger debugger = devToolsService.getDebugger();

        Inspector inspector = devToolsService.getInspector();

        network.onLoadingFinished(
                event -> {
                    // Close the tab and close the browser when loading finishes.
                    //   chromeService.closeTab(tab);
                    //  launcher.close();
                });

        // Enable network events.
        network.enable();
        debugger.enable();
        inspector.enable();

        // Navigate to github.com.
        Navigate navigate = page.navigate("chrome://inspect");

       // devToolsService.waitUntilClosed();
    }

    public void stopDebugger() {
        if(handler != null) {
            handler.destroyProcess();
        }
    }

    public boolean isDebuggerRunning() {
        if(handler != null) {
            return !handler.isProcessTerminated();
        } else {
            return false;
        }
    }

    protected void failed(DebugResultListener resultListener, String message, Throwable t) {
        resultListener.error(message);
//        resultListener.onFailure(source, t);
    }

    private String getTealDbgCmd() {
        String goalCmd = "tealdbg";
        if(SystemInfo.isWindows)
            goalCmd = "tealdbg.exe";

        return goalCmd;
    }

    @Override
    public void dispose() {
        stopDebugger();
    }
}
