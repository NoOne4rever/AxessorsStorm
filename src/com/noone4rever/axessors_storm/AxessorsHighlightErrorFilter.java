package com.noone4rever.axessors_storm;

import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.codeInsight.daemon.impl.HighlightInfoFilter;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;

public class AxessorsHighlightErrorFilter implements HighlightInfoFilter {

    private final static String METHOD_NOT_FOUND_PATTERN = "Method '%s.*' not found in.*";
    private final static String[] ACTIONS = {"get", "set", "add", "delete", "count", "increment", "decrement"};

    @Override
    public boolean accept(@NotNull HighlightInfo highlightInfo, @Nullable PsiFile file) {
        String description = StringUtil.notNullize(highlightInfo.getDescription());
        return !isAxessorsAction(description);
    }

    private boolean isAxessorsAction(String description) {
        Pattern pattern;
        for (String action : ACTIONS) {
            pattern = Pattern.compile(String.format(METHOD_NOT_FOUND_PATTERN, action));
            if (pattern.matcher(description).matches()) {
                return true;
            }
        }
        return false;
    }
}
