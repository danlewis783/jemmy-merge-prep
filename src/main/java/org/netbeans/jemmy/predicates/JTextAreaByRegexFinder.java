package org.netbeans.jemmy.predicates;

import javax.swing.JTextArea;
import java.awt.Component;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class JTextAreaByRegexFinder implements Predicate<Component> {
    private final Pattern regExpPattern;

    public JTextAreaByRegexFinder(String regex) {
        regExpPattern = Pattern.compile(regex);
    }

    @Override
    public boolean test(Component comp) {
        JTextArea jText = (JTextArea) comp;
        return regExpPattern.matcher(jText.getText()).matches();
    }
}
