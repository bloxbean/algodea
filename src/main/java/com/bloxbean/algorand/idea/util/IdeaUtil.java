package com.bloxbean.algorand.idea.util;

import com.bloxbean.algorand.idea.module.AlgorandModuleType;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationAction;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;

import java.util.Collection;

public class IdeaUtil {
    public final static String PLUGIN_ID = "com.bloxbean.algorand";

    public final static String ACCOUNT_LIST_ACTION = "Algo.account.list";
    public final static String MULTISIG_ACCOUNT_LIST_ACTION = "Algo.multisig-account.list";

    public static void showNotification(Project project, String title, String content, NotificationType notificationType, String actionId) {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            public void run() {
                Notification notification = new Notification(IdeaUtil.PLUGIN_ID,
                        title, content,
                        notificationType);

                if(actionId != null) {
                    ActionManager am = ActionManager.getInstance();
                    AnAction action = am.getAction(actionId);
                    notification.addAction(action);
                }

                Notifications.Bus.notify(notification, project);
            }
        });
    }

    public static void showNotificationWithAction(Project project, String title, String content, NotificationType notificationType, NotificationAction action) {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            public void run() {
                Notification notification = new Notification(IdeaUtil.PLUGIN_ID,
                        title, content,
                        notificationType);

                notification.addAction(action);

                Notifications.Bus.notify(notification, project);
            }
        });
    }
}
