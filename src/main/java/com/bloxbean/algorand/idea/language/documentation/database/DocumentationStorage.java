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

package com.bloxbean.algorand.idea.language.documentation.database;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.intellij.openapi.diagnostic.Logger;

import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class DocumentationStorage {

    private static final Logger LOG = Logger.getInstance(DocumentationStorage.class);
    private final String documentationDir;
    private final Collection<String> names;
    private Map<String, String> cache;

    public DocumentationStorage(String documentationDir, Collection<String> names) {
        this.documentationDir = documentationDir;
        this.names = names;

    }

    public Optional<String> lookup(String name) {
        initialize();
        return Optional.ofNullable(cache.get(name.toLowerCase()));
    }

    private synchronized void initialize() {
        if (cache != null) {
            return;
        }

        cache = new ConcurrentHashMap<>();
        for (String name : names) {
            if("/".equals(name))
                name = "div";

            String filePath = documentationDir + "/" + name + ".html";
            try {
                URL documentationFile = DocumentationStorage.class.getResource(filePath);
                String documentation = Resources.toString(documentationFile, Charsets.UTF_8);

                cache.put(name.toLowerCase(), documentation);
            } catch (Exception e) {
                LOG.warn("Unable to load " + filePath + " documentation", e);
            }
        }
    }
}
