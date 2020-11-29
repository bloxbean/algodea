package com.bloxbean.algodea.idea.pyteal;

import com.bloxbean.algodea.idea.nodeint.service.LogListener;
import com.bloxbean.algodea.idea.toolwindow.AlgoConsole;

public class PyTealHelpMessagePrinter {

    public static void pythonPluginNotInstalled(AlgoConsole console) {
        console.showInfoMessage("******* Please make sure to do the following steps to use PyTeal *******");
        console.showInfoMessage("");
        console.showInfoMessage("1. Install Python Plugin.");
        console.showInfoMessage("   - IntelliJ IDEA CE : Python Community Edition");
        console.showInfoMessage("   - IntelliJ IDEA Ultimate : Python");
        console.showInfoMessage("");
        console.showInfoMessage("2. Add/Configure Python SDK");
        console.showInfoMessage("   - Go to Project's Setting page (Right click on project > Open Module Settings)");
        console.showInfoMessage("   - Install pyteal package for the SDK from Project Settings panel\n");
        console.showInfoMessage("");
        console.showInfoMessage("3. Select Python SDK as Project's SDK\n");
        console.showInfoMessage("");
        console.showInfoMessage("************************************************************************");

    }
}
