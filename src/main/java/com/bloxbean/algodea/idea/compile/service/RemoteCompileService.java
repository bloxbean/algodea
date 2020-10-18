package com.bloxbean.algodea.idea.compile.service;

import com.algorand.algosdk.util.Encoder;
import com.bloxbean.algodea.idea.configuration.model.NodeInfo;
import com.bloxbean.algodea.idea.nodeint.service.AlgoBaseService;
import com.bloxbean.algodea.idea.nodeint.service.LogListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import com.twelvemonkeys.lang.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class RemoteCompileService extends BaseCompileService {
    private NodeInfo nodeInfo;
    private String cwd;
    private Project project;

    public RemoteCompileService(@NotNull Project project, NodeInfo remoteNode) {
        this.project = project;
        this.nodeInfo = remoteNode;
        this.cwd = project.getBasePath();
    }

    @Override
    public void compile(String source, String destination, CompilationResultListener compilationResultListener) {

        LogListener logListener = new LogListener() {
            @Override
            public void info(String msg) {
                compilationResultListener.info(msg);
            }

            @Override
            public void error(String msg) {
                compilationResultListener.error(msg);
            }

            @Override
            public void warn(String msg) {
                compilationResultListener.warn(msg);
            }
        };

        compilationResultListener.info(String.format("Compiling TEAL file using remote Algorand Node [%s]", nodeInfo.getNodeAPIUrl()));

        AlgoBaseService algoBaseService = new AlgoBaseService(project, nodeInfo, logListener);

        String code = null;
        try {
            code = FileUtil.loadFile(new File(source));
        } catch (IOException e) {
            failed(compilationResultListener, source, "File not found : " + source);
            return;
        }

        if(StringUtil.isEmpty(code)) {
            failed(compilationResultListener, source, "Can't compile. Empty code");
        }

        byte[] bytes = code.getBytes(StandardCharsets.UTF_8);

        String result = algoBaseService.compileProgram(bytes);

        if(result == null) {
            failed(compilationResultListener, source, "Compilation Failed");
        } else {
            //compilationResultListener.info(result);
            byte[] outputBytes = Encoder.decodeFromBase64(result);

            try {
                FileUtil.writeToFile(new File(destination), outputBytes);
            } catch (IOException e) {
                failed(compilationResultListener, source, "Error writing output to : " + destination);
            }
            compilationResultListener.onSuccessful(source, destination);
        }
    }
}
