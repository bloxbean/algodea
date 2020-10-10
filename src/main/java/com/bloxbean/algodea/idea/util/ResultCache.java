/*
 * Copyright (c) 2020 BloxBean Project
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.bloxbean.algodea.idea.util;

import com.intellij.openapi.util.text.StringUtil;

import java.io.*;
import java.util.Properties;

public class ResultCache {

//    public static String statusFileName = ".algo.conf";
//
//    public static String DEPLOY_ADDRESS = "deploy.address";
//    public static String DEPLOY_TX_RECEIPT = "deploy.tx.receipt";
//    public static String LAST_DEPLOY_DEBUG_ENABLE = "last.deploy.debug.enabled";
//    public static String TX_RECEIPT = "last.tx.receipt";
//
//    private String projectName;
//    private String targetFolder;
//
//    public ResultCache(String projectName, String targetFolder) {
//        this.projectName = projectName;
//        this.targetFolder = targetFolder;
//    }
//
//    public void updateDeployAddress(String address) {
//        updateProperty(DEPLOY_ADDRESS, address);
//
//        //update debug enabled or not
//        boolean debugEnabled = ConfigUtil.getAvmConfigurationBooleanProps(ConfigUtil.PRESERVE_DEBUGGABILITY, false);
//        updateDebugEnabledInLastDeploy(debugEnabled);
//    }
//
//    public void updateDeployTxnReceipt(String txHash) {
//        updateProperty(DEPLOY_TX_RECEIPT, txHash);
//        updateProperty(TX_RECEIPT, txHash); //also set lastTxn receipt
//        updateProperty(DEPLOY_ADDRESS, ""); //reset deploy address as it's a new deployment.
//    }
//
//    private void updateDebugEnabledInLastDeploy(boolean flag) {
//        updateProperty(LAST_DEPLOY_DEBUG_ENABLE, String.valueOf(flag));
//    }
//
//    public void updateTxnReceipt(String txHash) {
//        updateProperty(TX_RECEIPT, txHash);
//    }
//
//    private void updateProperty(String propertyName, String address) {
//        Properties props = this.readResults(targetFolder);
//
//        if(props == null)
//            props = new Properties();
//
//        props.setProperty(resolvePropertyName(projectName, propertyName), address);
//
//        writeResults(targetFolder, props);
//    }
//
//    public String getLastDeployedAddress() {
//        return getPropertyValue(DEPLOY_ADDRESS);
//    }
//
//    public String getLastDeployTxnReceipt() {
//        return getPropertyValue(DEPLOY_TX_RECEIPT);
//    }
//
//    public boolean getDebugEnabledInLastDeploy() {
//        String flag = getPropertyValue(LAST_DEPLOY_DEBUG_ENABLE);
//        if(StringUtil.isEmpty(flag))
//            return false;
//        else
//            return Boolean.parseBoolean(flag);
//    }
//
//    public String getLastTxnReceipt() {
//        return getPropertyValue(TX_RECEIPT);
//    }
//
//    private String getPropertyValue(String propertyName) {
//        Properties props = this.readResults(targetFolder);
//
//        if(props == null)
//            props = new Properties();
//
//        return props.getProperty(resolvePropertyName(projectName, propertyName));
//    }
//
//    private void writeResults(String targetFolder, Properties props) {
//        Properties prop = new Properties();
//        OutputStream output = null;
//
//        try {
//
//            output = new FileOutputStream(new File(targetFolder, statusFileName));
//
//            props.store(output, null);
//
//        } catch (Exception io) {
//            io.printStackTrace();
//        } finally {
//            if (output != null) {
//                try {
//                    output.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//
//    private Properties readResults(String targetFolder) {
//        InputStream input = null;
//
//        try {
//
//            File deployResultFile = new File(targetFolder, statusFileName);
//
//            if(!deployResultFile.exists())
//                return new Properties();
//
//            input = new FileInputStream(new File(targetFolder, statusFileName));
//
//            Properties properties = new Properties();
//            properties.load(input);
//
//            return properties;
//
//        } catch (Exception io) {
//            io.printStackTrace();
//            return new Properties();
//        } finally {
//            if (input != null) {
//                try {
//                    input.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//
//        }
//    }
//
//    private String resolvePropertyName(String projectName, String propName) {
//        return projectName + "." + propName;
//    }

}
