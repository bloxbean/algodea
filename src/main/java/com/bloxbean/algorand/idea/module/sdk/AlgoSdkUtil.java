package com.bloxbean.algorand.idea.module.sdk;

import com.intellij.openapi.diagnostic.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class AlgoSdkUtil {
    private final static Logger LOG = Logger.getInstance(AlgoSdkUtil.class);

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
