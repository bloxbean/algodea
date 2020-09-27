package com.bloxbean.algorand.idea.module.sdk;

import com.bloxbean.algorand.idea.common.AlgoIcons;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.projectRoots.*;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.xmlb.XmlSerializer;
import org.jdom.Element;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

import static com.bloxbean.algorand.idea.common.AlgoConstants.SDK_TYPE_ID;

public class AlgoSdkType extends SdkType {
    private final static Logger LOG = Logger.getInstance(AlgoSdkType.class);

    public AlgoSdkType() {
        super(SDK_TYPE_ID);
    }

    @Override
    public @Nullable String suggestHomePath() {
        return System.getenv().get("ALGORAND_HOME");
    }

    @Override
    public boolean isValidSdkHome(String s) {
        VirtualFile home = LocalFileSystem.getInstance().findFileByIoFile(new File(s));
        if (home != null && home.exists() && home.isDirectory()) {
            VirtualFile bin = home.findChild("bin");
            VirtualFile goal = null;

            if(bin != null)
                goal = bin.findChild("goal");
            if (bin != null && bin.isDirectory() && goal != null && goal.isInLocalFileSystem()) {

                if (!new File(goal.getCanonicalPath()).canExecute()) {
                    LOG.debug("goal binary cannot be executed: " + goal.getCanonicalPath());
                    return false;
                } else {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public String suggestSdkName(String s, String s2) {
        return "Algorand SDK";
    }

    @Nullable
    @Override
    public AdditionalDataConfigurable createAdditionalDataConfigurable(SdkModel sdkModel, SdkModificator sdkModificator) {
        return null;
    }

    @Override
    public String getPresentableName() {
        return "Algorand SDK";
    }

    @Override
    public SdkAdditionalData loadAdditionalData(Element additional) {
        return XmlSerializer.deserialize(additional, AlgoSdkData.class);
    }

    @Override
    public Icon getIcon() {
        return AlgoIcons.ALGO_ICON;
    }

    @Override
    public Icon getIconForAddAction() {
        return getIcon();
    }

    @Override
    public void saveAdditionalData(SdkAdditionalData additionalData, Element additional) {
        if (additionalData instanceof AlgoSdkData) {
            XmlSerializer.serializeInto(additionalData, additional);
        }
    }

    public static SdkTypeId getInstance() {
        return SdkType.findInstance(AlgoSdkType.class);
    }

    @Nullable
    @Override
    public String getVersionString(Sdk sdk) {
        String path = sdk.getHomePath();
        if (path == null) return null;

        File file = new File(path);
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
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
