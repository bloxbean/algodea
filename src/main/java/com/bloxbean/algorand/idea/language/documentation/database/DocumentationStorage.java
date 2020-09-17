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
