package com.bloxbean.algodea.idea.util;

import com.intellij.openapi.diagnostic.Logger;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

public class PluginConfig {

    private final static Logger log = Logger.getInstance(PluginConfig.class);

    public static String CONFIG_FILE = ".algorand-idea.conf";

    public static String ENCRYPTION_KEY = "encryption.key";
    public static String targetFolder = System.getProperty("user.home");

    /**
     * Get encryption key. If there is no encryption key, create one and store.
     * @return
     */
    public static String getEncryptionKey() {
        String encryptionKey = getPropertyValue(ENCRYPTION_KEY);
        if(encryptionKey == null || encryptionKey.isEmpty()) {
            //Generate a new encryption key
            try {
                encryptionKey = AESEncryptionHelper.generateKey();
            } catch (NoSuchAlgorithmException e) {
                //Set a default key
                encryptionKey = "RAekhoC#$#1HUbXJhkwei@786&&423";
            }
            updateProperty(ENCRYPTION_KEY, encryptionKey);
        }

        return encryptionKey;
    }

    private static void updateProperty(String propertyName, String value) {
        Properties props = readResults(targetFolder);

        if(props == null)
            props = new Properties();

        props.setProperty(propertyName, value);

        writeResults(targetFolder, props);
    }

    private static String getPropertyValue(String propertyName) {
        Properties props = readResults(targetFolder);

        if(props == null)
            props = new Properties();

        return props.getProperty(propertyName);
    }

    private static void writeResults(String targetFolder, Properties props) {
        Properties prop = new Properties();
        OutputStream output = null;

        try {

            output = new FileOutputStream(new File(targetFolder, CONFIG_FILE));

            props.store(output, null);

        } catch (Exception io) {
           if(log.isDebugEnabled())
               log.warn(io);
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
//                    e.printStackTrace();
                }
            }
        }
    }

    private static Properties readResults(String targetFolder) {
        InputStream input = null;

        if(log.isDebugEnabled()) {
            log.debug("Plugin config is stored at folder >> " + targetFolder);
        }

        try {

            File deployResultFile = new File(targetFolder, CONFIG_FILE);

            if(!deployResultFile.exists())
                return new Properties();

            input = new FileInputStream(new File(targetFolder, CONFIG_FILE));

            Properties properties = new Properties();
            properties.load(input);

            return properties;

        } catch (Exception io) {
            if(log.isDebugEnabled())
                log.warn(io);
            return new Properties();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
//                    e.printStackTrace();
                }
            }

        }
    }

}
