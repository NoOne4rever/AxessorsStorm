package com.noone4rever.axessors_storm;

import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.codeInsight.daemon.impl.HighlightInfoFilter;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;

public class AxessorsHighlightErrorFilter implements HighlightInfoFilter {

    private final static Pattern ACCESSOR_NOT_FOUND = Pattern.compile("Method '(get|set).*' not found in .+");

    @Override
    public boolean accept(@NotNull HighlightInfo highlightInfo, @Nullable PsiFile file) {
        String description = StringUtil.notNullize(highlightInfo.getDescription());
        return !ACCESSOR_NOT_FOUND.matcher(description).matches();
    }
}
