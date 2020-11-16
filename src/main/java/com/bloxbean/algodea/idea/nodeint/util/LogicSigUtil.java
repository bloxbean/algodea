package com.bloxbean.algodea.idea.nodeint.util;

import com.bloxbean.algodea.idea.compile.model.LogicSigMetaData;
import com.bloxbean.algodea.idea.util.IOUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;

import java.io.File;
import java.io.IOException;

public class LogicSigUtil {

    public static byte[] readSourceBytesIfAvailable(String lsigPath) {
        LogicSigMetaData logicSigMetaData = getLogicSigMetaData(lsigPath);

        if(logicSigMetaData == null)
            return null;

        try {
            String sourcePath = logicSigMetaData.sourcePath;
            if(StringUtil.isEmpty(sourcePath))
                return null;

            return FileUtil.loadFileBytes(new File(sourcePath));
        } catch (IOException e) {
        }

        return null;
    }

    public static LogicSigMetaData getLogicSigMetaData(String lsigPath) {
        if(StringUtil.isEmpty(lsigPath))
            return null;

        String lsigMetaDataFilePath = IOUtil.getNameWithoutExtension(lsigPath) + "-metadata.json";

        File lsigMetaDataFile = new File(lsigMetaDataFilePath);
        if(!lsigMetaDataFile.exists())
            return null;

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            LogicSigMetaData logicSigMetaData = objectMapper.readValue(lsigMetaDataFile, LogicSigMetaData.class);
            if(logicSigMetaData == null)
                return null;
            else
                return logicSigMetaData;

        } catch (IOException e) {
        }
        return null;
    }
}
