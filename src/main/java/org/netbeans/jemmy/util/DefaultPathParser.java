package org.netbeans.jemmy.util;

import java.util.Objects;
import java.util.regex.Pattern;

public final class DefaultPathParser implements PathParser {
    private final String separator;

    public DefaultPathParser(String separator) {
        this.separator = Objects.requireNonNull(separator);
    }

    @Override
    public String[] parse(String path) {
        if (Objects.requireNonNull(path).isEmpty()) {
            return new String[] {};
        }

        return path.split(Pattern.quote(separator), -1);
    }
}
