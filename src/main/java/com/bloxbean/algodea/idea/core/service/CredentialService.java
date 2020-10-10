/*
 * Copyright (c) 2019 Aion4j Project
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

package com.bloxbean.algodea.idea.core.service;

import com.bloxbean.algodea.idea.util.PluginConfig;
import com.bloxbean.algodea.idea.util.AESEncryptionHelper;
import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.credentialStore.CredentialAttributesKt;
import com.intellij.ide.passwordSafe.PasswordSafe;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.text.StringUtil;

public class CredentialService {
    private static final Logger log = Logger.getInstance(CredentialService.class);
    private static final String ENCRYPTION_KEY = "encryption.key";

    public static String getEncryptionKey(boolean useCredentialStore) {

        try {
            if (useCredentialStore) {
                String encryptionKey = getCredential(ENCRYPTION_KEY);
                if (StringUtil.isEmpty(encryptionKey)) {
                    try {
                        storeCredential(ENCRYPTION_KEY, AESEncryptionHelper.generateKey());
                    } catch (Exception e) {
                        if(log.isDebugEnabled())
                            log.debug("Error storing credential in credential store", e);
                    }
                }
                encryptionKey = getCredential(ENCRYPTION_KEY);
                return encryptionKey;
            } else {
                return PluginConfig.getEncryptionKey();
            }
        } catch (Exception e) {
            if(log.isDebugEnabled())
                log.debug("Error getting encryption key", e);
            return null;
        }
    }

    public static String getCredential(String key) {
        CredentialAttributes credentialAttributes = createCredentialAttributes(key);

        String password = PasswordSafe.getInstance().getPassword(credentialAttributes);
        return password;
    }

    public static void storeCredential(String key, String password) {
        CredentialAttributes credentialAttributes = createCredentialAttributes(key);
        PasswordSafe.getInstance().setPassword(credentialAttributes, password);
    }

    private static CredentialAttributes createCredentialAttributes(String key) {
        return new CredentialAttributes(CredentialAttributesKt.generateServiceName("algorand-idea", key));
    }
}
