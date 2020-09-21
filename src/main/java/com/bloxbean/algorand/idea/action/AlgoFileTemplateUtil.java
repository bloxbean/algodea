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

package com.bloxbean.algorand.idea.action;

import com.bloxbean.algorand.idea.language.TEALFileType;
import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.openapi.util.Condition;
import com.intellij.util.SmartList;
import com.intellij.util.containers.ContainerUtil;

import java.util.List;

public class AlgoFileTemplateUtil {
  private final static String ALGORAND_TEMPLATE_PREFIX = "Algorand ";

  public static List<FileTemplate> getApplicableTemplates() {
    return getApplicableTemplates(new Condition<FileTemplate>() {
      @Override
      public boolean value(FileTemplate fileTemplate) {
        return TEALFileType.INSTANCE.getDefaultExtension().equals(fileTemplate.getExtension());
      }
    });
  }

  public static List<FileTemplate> getApplicableTemplates(Condition<FileTemplate> filter) {
    final List<FileTemplate> applicableTemplates = new SmartList<FileTemplate>();
    applicableTemplates.addAll(ContainerUtil.findAll(FileTemplateManager.getDefaultInstance().getInternalTemplates(), filter));
    //applicableTemplates.addAll(ContainerUtil.findAll(FileTemplateManager.getInstance().getAllTemplates(), filter));
    return applicableTemplates;
  }

  public static String getTemplateShortName(String templateName) {
    if (templateName.startsWith(ALGORAND_TEMPLATE_PREFIX)) {
      return templateName.substring(ALGORAND_TEMPLATE_PREFIX.length());
    }
    return templateName;
  }
}
