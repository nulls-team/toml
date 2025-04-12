package dev.donutquine.toml.util;

import java.util.regex.MatchResult;
import java.util.regex.Pattern;

public class StringEscaper {
    private static final Pattern SHORT_UNICODE_PATTERN = Pattern.compile("\\\\u([0-9A-F]{4})");
    private static final Pattern LONG_UNICODE_PATTERN = Pattern.compile("\\\\U([0-9A-F]{8})");

    /**
     * Escapes given string to make it safe to be printed.
     *
     * @param string The input String.
     * @return The output String.
     **/
    public static String escape(String string) {
        return string.replace("\\", "\\\\")
            .replace("\t", "\\t")
            .replace("\b", "\\b")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\f", "\\f")
            .replace("\"", "\\\"");
    }

    public static Object unescape(String string) {
        String unescaped = string.replace("\\b", "\b")
            .replace("\\t", "\t")
            .replace("\\n", "\n")
            .replace("\\f", "\f")
            .replace("\\r", "\r")
            .replace("\\\"", "\"")
            .replace("\\\\", "\\");

        String shortUnicodeUnescaped = SHORT_UNICODE_PATTERN.matcher(unescaped).replaceAll(StringEscaper::unescapeUnicode);
        return LONG_UNICODE_PATTERN.matcher(shortUnicodeUnescaped).replaceAll(StringEscaper::unescapeUnicode);
    }

    private static String unescapeUnicode(MatchResult match) {
        return Character.toString(Integer.parseInt(match.group(1), 16));
    }
}
