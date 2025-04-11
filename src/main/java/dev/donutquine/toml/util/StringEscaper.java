package dev.donutquine.toml.util;

public class StringEscaper {
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
}
