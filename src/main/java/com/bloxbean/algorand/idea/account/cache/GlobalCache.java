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

package com.bloxbean.algorand.idea.account.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.text.StringUtil;

import javax.crypto.SecretKey;
import java.io.*;
import java.util.Properties;

public class GlobalCache {
    private final static Logger log = Logger.getInstance(GlobalCache.class);

    public static String ACCOUNT_CACHE = ".algorand.account.conf";

    //props in key file
    private final static String SECRET_KEY = "secret-key";
    private final static String PROTECTION_MODE = "protection-mode";

    private final String targetFolder;

    private ObjectMapper objectMapper;

    public GlobalCache(String targetFolder) {
        this.targetFolder = targetFolder;

        this.objectMapper = new ObjectMapper();
    }

    public void setAccountCache(AccountCache accountCache) {
        File file = getAccountCacheFile();

        SecretKey secretKey = getSecretKeyFromFile();
        if(secretKey == null) { //Create a secret key
            secretKey = generateNewSecretAndStore();
        }

        if(secretKey == null) { //If secret is still null. write cache in plain text
            getAccountCacheKeyFile().delete();
            writeAccountCacheToFile(accountCache, file);
        } else {
            try {
                String jsonContent = writeAccountCacheToJson(accountCache);

                FileEncrypterDecrypter fileEncrypterDecrypter = new FileEncrypterDecrypter(secretKey);
                fileEncrypterDecrypter.encrypt(jsonContent, getAccountCacheFile().getAbsolutePath());
            } catch (Exception e) {
                log.debug("Error writing encrypted account cache content.", e);
                //Let's try to write plain content and delete secret file as fallback
                getAccountCacheKeyFile().delete();
                writeAccountCacheToFile(accountCache, file);
            }
        }
    }

    public AccountCache getAccountCache() {
        File file = getAccountCacheFile();
        File keyFile = getAccountCacheKeyFile();

        if(!file.exists())
            return new AccountCache();

        if(keyFile.exists()) {
            SecretKey secretKey = getSecretKeyFromFile();
            if(secretKey == null) {
                return new AccountCache();
            } else {
                try {
                    FileEncrypterDecrypter fileEncrypterDecrypter = new FileEncrypterDecrypter(secretKey);
                    String encContent = fileEncrypterDecrypter.decrypt(file);
                    return readAccountCacheFromJson(encContent);
                } catch (Exception e) {
                    log.warn("Account cache could not be read", e);
                    return new AccountCache();
                }
            }
        } else { //If not encrypted. Just to support older version and migration
            AccountCache accountCache = readAccountCacheFromFile(file);

            return accountCache;
        }
    }

    private AccountCache readAccountCacheFromJson(String content) {
        AccountCache accountCache = null;

        try {
            accountCache = objectMapper.readValue(content, AccountCache.class);
        } catch (Exception e) {
            accountCache = new AccountCache();
            e.printStackTrace();
            log.warn("Could not read from account cache: " + e.getMessage());
            if(log.isDebugEnabled()) {
                log.error("Could not read from account cache", e);
            }
        }
        return accountCache;
    }

    private AccountCache readAccountCacheFromFile(File file) {
        AccountCache accountCache = null;

        try {
            accountCache = objectMapper.readValue(file, AccountCache.class);
        } catch (Exception e) {
            accountCache = new AccountCache();
            //e.printStackTrace();
            log.warn("Could not read from account cache: " + e.getMessage());
            if(log.isDebugEnabled()) {
                log.error("Could not read from account cache", e);
            }
        }
        return accountCache;
    }

    private String writeAccountCacheToJson(AccountCache accountCache) {
        try {
            StringWriter writer = new StringWriter();
            objectMapper.writeValue(writer, accountCache);
            return writer.toString();
        } catch (Exception e) {
            log.warn("Could not convert account cache to json", e);
            if (log.isDebugEnabled()) {
                log.debug("Could not convert account cache to json", e);
            }
        }

        return null;
    }

    private void writeAccountCacheToFile(AccountCache accountCache, File file) {
        try {
            objectMapper.writeValue(file, accountCache);
        } catch (Exception e) {
            log.warn("Could not write to account cache", e);
            if (log.isDebugEnabled()) {
                log.debug("Could not write to account cache", e);
            }
        }
    }

    public void clearAccountCache() {
        File file = getAccountCacheFile();
        file.delete();

        File keyFile = getAccountCacheKeyFile();
        keyFile.delete();
    }

    private SecretKey getSecretKeyFromFile() {
        try {
            File keyFile = getAccountCacheKeyFile();
            if (!keyFile.exists()) {
                return null;
            }

            Properties props = readKeyProperties(keyFile);
            String secretKey = (String) props.get(SECRET_KEY);

            if (!StringUtil.isEmpty(secretKey)) {
                return FileEncrypterDecrypter.getSecretKeyFromEncodedKey(secretKey);
            } else {
                return null;
            }
        } catch (Exception e) {
            log.warn("Invalid secret key.");
            log.debug("Invalid key content", e);
            return null;
        }
    }

    private SecretKey generateNewSecretAndStore() {
        String key = null;
        try {
            key = FileEncrypterDecrypter.generateKey();
        } catch (Exception e) {
            log.debug("Error generating secret key. Something is really wrong.", e);
            log.warn("Secret generation failed. Keys will be stored in plain text");
            return null;
        }

        if(key == null) {
            return null;
        }

        Properties props = new Properties();
        props.put(SECRET_KEY, key);
        props.put(PROTECTION_MODE, "none");

        File keyFile = getAccountCacheKeyFile();
        writeKeyProperties(keyFile, props);

        return FileEncrypterDecrypter.getSecretKeyFromEncodedKey(key);
    }

    private File getAccountCacheFile() {
        return new File(targetFolder, ACCOUNT_CACHE);
    }

    private File getAccountCacheKeyFile() {
        return new File(targetFolder, ACCOUNT_CACHE + ".key");
    }

    private void writeKeyProperties(File file, Properties props) {
        Properties prop = new Properties();
        OutputStream output = null;

        try {
            output = new FileOutputStream(file);
            props.store(output, null);

        } catch (Exception io) {
            io.printStackTrace();
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Properties readKeyProperties(File file) {
        InputStream input = null;

        try {

            input = new FileInputStream(file);

            Properties properties = new Properties();
            properties.load(input);

            return properties;

        } catch (Exception io) {
            io.printStackTrace();
            return new Properties();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}

