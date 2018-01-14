package com.noone4rever.axessors_storm;

import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.codeInsight.daemon.impl.HighlightInfoFilter;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;

/**
 * Axessors error filter.
 */
public class AxessorsHighlightErrorFilter implements HighlightInfoFilter {

    private final static String METHOD_NOT_FOUND_PATTERN = "Method '%s.*' not found in.*";
    private final static String[] ACTIONS = {"get", "set", "add", "delete", "count", "increment", "decrement"};
    private final static Pattern AXESSORS_COMMENT = Pattern.compile("#:.+");

    @Override
    public boolean accept(@NotNull HighlightInfo highlightInfo, @Nullable PsiFile file) {
        String description = StringUtil.notNullize(highlightInfo.getDescription());
        return !isAxessorsAction(description) && !isAxessorsField(highlightInfo, file);
    }

    /**
     * Checks if an error is caused by Axessors method invocation.
     *
     * @param description error description
     * @return the result of the checkout
     */
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

    /**
     * Checks if an error is caused by "unused" field with Axessors comment.
     *
     * @param highlightInfo information about error
     * @param file PSI file
     * @return the result of the checkout
     */
    private boolean isAxessorsField(HighlightInfo highlightInfo, PsiFile file) {
        try {
            PsiElement field = file.findElementAt(highlightInfo.startOffset);
            PsiElement nextElement = field.getParent().getParent().getNextSibling().getNextSibling();
            String content = nextElement.getText();
            return AXESSORS_COMMENT.matcher(content).matches();
        } catch (NullPointerException e) {
            return false;
        }
    }
}
