package com.bloxbean.algodea.idea.core.util;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class AlgoSdkUtil {
    private final static Logger LOG = Logger.getInstance(AlgoSdkUtil.class);

    public static String getVersionString(String algoHome) {
        if (algoHome == null) return null;

        File file = new File(algoHome);
        VirtualFile home = LocalFileSystem.getInstance().findFileByIoFile(file);
        if (home != null) {
            VirtualFile bin = home.findChild("bin");
            if (bin != null) {
                String binPath = bin.getCanonicalPath();
                try {
                    String result = AlgoSdkUtil.runProcessAndExit(binPath + File.separator + "goal", "--version");
                    result.split(" ");
                    LOG.debug(result);
                    return result;
                } catch (IOException e) {
                    LOG.error(e);
                } catch (InterruptedException e) {
                    LOG.error(e);
                }
            }
        }
        return null;
    }

    public static String runProcessAndExit(String program, String command) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder();
//        if (isWindows) {
//            builder.command(, "/c", "dir");
//        } else {
        processBuilder.command(program, command);
        try {
            Process process = processBuilder.start();
            StringBuilder output = new StringBuilder();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            String line;
            int lineCount = 1;
            String version = null;
            while ((line = reader.readLine()) != null) {
                output.append(line + "\n");
                if(lineCount == 2) {
                    version = line.split(" ")[0];
                }
                lineCount++;
            }

            int exitVal = process.waitFor();
            if (exitVal == 0) {
                LOG.debug("Getting Algorand SDK version. Success!");
                LOG.debug(output.toString());
                return version;
            } else {
                //abnormal...
            }

        } catch (IOException e) {
            LOG.error(e);
        } catch (InterruptedException e) {
            LOG.error(e);
        }
        return null;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        String version = AlgoSdkUtil.runProcessAndExit("/Users/satya/Downloads/node_stable_darwin-amd64_2.1.5/bin/goal", "--version");
        System.out.println("Version: " + version);
    }
}
