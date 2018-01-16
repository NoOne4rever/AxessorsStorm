package com.noone4rever.axessors_storm;

import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.codeInsight.daemon.impl.HighlightInfoFilter;
import com.intellij.lang.annotation.HighlightSeverity;
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
    private final static Pattern UNUSED_FIELD_PATTERN = Pattern.compile("Unused private field \\$.+");
    private final static Pattern AXESSORS_COMMENT_PATTERN = Pattern.compile("#:.+");

    /**
     * Accepts or rejects IDE warnings.
     *
     * @param highlightInfo error description
     * @param file PSI file
     * @return the result of the error checkout
     */
    @Override
    @SuppressWarnings("SimplifiableIfStatement")
    public boolean accept(@NotNull HighlightInfo highlightInfo, @Nullable PsiFile file) {
        if (file == null) {
            return true;
        }
        HighlightSeverity severity = highlightInfo.getSeverity();
        if (severity.equals(HighlightSeverity.INFORMATION)) {
            return true;
        }
        return !isAxessorsAction(highlightInfo) && !isAxessorsField(highlightInfo, file);
    }

    /**
     * Checks if the error is caused by Axessors method invocation.
     *
     * @param highlightInfo error description
     * @return the result of the checkout
     */
    private boolean isAxessorsAction(@NotNull HighlightInfo highlightInfo) {
        Pattern pattern;
        String description = StringUtil.notNullize(highlightInfo.getDescription());
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
    @SuppressWarnings("ConstantConditions")
    private boolean isAxessorsField(@NotNull HighlightInfo highlightInfo, @NotNull PsiFile file) {
        String description = StringUtil.notNullize(highlightInfo.getDescription());
        if (!UNUSED_FIELD_PATTERN.matcher(description).matches()) {
            return false;
        }
        try {
            PsiElement variable = file.findElementAt(highlightInfo.startOffset);
            PsiElement nextElement = variable.getParent().getParent().getNextSibling().getNextSibling();
            String content = nextElement.getText();
            return AXESSORS_COMMENT_PATTERN.matcher(content).matches();
        } catch (NullPointerException error) {
            return false;
        }
    }
}
