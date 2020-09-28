package com.bloxbean.algorand.idea.action.util;

import com.bloxbean.algorand.idea.action.ui.VarParam;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class VarTmplUtil {
    private final static String VAR_TMPL_PATTERN = "(VAR_TMPL_[a-zA-Z0-9_&$]*)";

    public static List<VarParam> getListOfVarTmplInTEALFile(VirtualFile tealFile) throws IOException {
        if(tealFile == null)
            return Collections.EMPTY_LIST;

        StringBuilder content = getFileContent(tealFile, true);

        if(content.toString().isEmpty())
            return Collections.EMPTY_LIST;

        return getVarTmplParams(content.toString()).stream()
                .map(t -> new VarParam(t, ""))
                .collect(Collectors.toList());

    }

    @NotNull
    private static StringBuilder getFileContent(VirtualFile tealFile, boolean ignoreComment) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader br
                     = new BufferedReader(new InputStreamReader(tealFile.getInputStream()))) {
            String line = null;
            while ((line = br.readLine()) != null) {
                if(ignoreComment && line.trim().startsWith("//")) {
                    //ignore this comment line
                } else {
                    content.append(line).append("\n");
                }
            }

        }
        return content;
    }

    public static List<String> getVarTmplParams(String content) {
        List<String> tokens = new ArrayList<>();
        Pattern pattern = Pattern.compile(VAR_TMPL_PATTERN);
        Matcher matcher =pattern.matcher(content);
        while (matcher.find()) {
            String token = matcher.group(1);
            if(!tokens.contains(token))
                tokens.add(token);
        }

        return tokens;
    }

    public static File createMergeSourceFile(Object requestor, VirtualFile sourceFile, VirtualFile outFolder, List<VarParam> varParamsValues) throws IOException {
        Map<String, String> map = new HashMap<>();
        varParamsValues.stream()
                .forEach(v -> map.put(v.getName(), v.getValue()));

        StringBuilder sb = getFileContent(sourceFile, false);
        Pattern pattern = Pattern.compile(VAR_TMPL_PATTERN);

        String newContent = replaceTokens(sb.toString(), pattern, matcher ->
            map.get(matcher.group(1))
        );
  //      String newString = strSubstitutor.replace(sb.toString());

//        VirtualFile mergedVirtualFile = LocalFileSystem.getInstance().createChildFile(requestor, outFolder, sourceFile.getName());

//        try (PrintWriter printWriter
//                     = new PrintWriter(new OutputStreamWriter(mergedVirtualFile.getOutputStream(requestor)))) {

        File mergedFile = new File(outFolder.getCanonicalPath() + File.separator + sourceFile.getName());

        if(mergedFile.exists())
            mergedFile.delete();

        try(FileWriter fileWriter = new FileWriter(mergedFile)) {
            fileWriter.write(newContent);
            fileWriter.flush();
        }

        return mergedFile;

    }

    public static String replaceTokens(String original, Pattern tokenPattern,
                                       Function<Matcher, String> converter) {
        int lastIndex = 0;
        StringBuilder output = new StringBuilder();
        Matcher matcher = tokenPattern.matcher(original);
        while (matcher.find()) {
            output.append(original, lastIndex, matcher.start())
                    .append(converter.apply(matcher));

            lastIndex = matcher.end();
        }
        if (lastIndex < original.length()) {
            output.append(original, lastIndex, original.length());
        }
        return output.toString();
    }

    public static void main(String[] args) {
        String content = "Hello sdfdsf \n test VAR_TMPL_NAME hwere dfsdfdf  \n VAR_TMPL_AGE sdfsdfs VAR_TMPL_ADD";

        getVarTmplParams(content).stream().forEach(System.out::println);
        System.out.println("hello");
    }
}
