package dev.donutquine.toml;

import dev.donutquine.toml.util.StringEscaper;

public final class TomlToken {
    private final TomlTokenType type;
    private final Location start, end;
    private final String value;

    public TomlToken(TomlTokenType type, Location start, Location end, String value) {
        this.type = type;
        this.start = start;
        this.end = end;
        this.value = value;
    }

    public TomlTokenType getType() {
        return type;
    }

    public Location getStart() {
        return start;
    }

    public Location getEnd() {
        return end;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.format("TomlToken{type=%s, value=\"%s\", %s - %s}", type, StringEscaper.escape(value), start, end);
    }
}
