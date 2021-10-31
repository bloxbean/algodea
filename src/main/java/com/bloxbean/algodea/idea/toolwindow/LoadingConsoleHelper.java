package com.bloxbean.algodea.idea.toolwindow;

import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;

public class LoadingConsoleHelper {
    private String lastLine = "";
    private ConsoleView view;

    public LoadingConsoleHelper(ConsoleView view) {
        this.view = view;
    }

    public void print(String line) {
        //clear the last line if longer
        if (lastLine.length() > line.length()) {
            String temp = "";
            for (int i = 0; i < lastLine.length(); i++) {
                temp += " ";
            }
            if (temp.length() > 1)
                view.print("\r" + temp, ConsoleViewContentType.NORMAL_OUTPUT);
        }

        view.print("\r" + line, ConsoleViewContentType.NORMAL_OUTPUT);
        lastLine = line;
    }

    private byte anim;

    public void animate(String line) {
        switch (anim) {
            case 1:
                print("[ \\ ] " + line);
                break;
            case 2:
                print("[ | ] " + line);
                break;
            case 3:
                print("[ / ] " + line);
                break;
            default:
                anim = 0;
                print("[ - ] " + line);
        }
        anim++;
    }

}
