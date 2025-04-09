package dev.donutquine.toml;

public final class TomlToken {
    private final TomlTokenType type;
    private final int line, column;
    private final int length;
    private final String value;

    public TomlToken(TomlTokenType type, int line, int column, int length, String value) {
        this.type = type;
        this.line = line;
        this.column = column;
        this.length = length;
        this.value = value;
    }

    public TomlTokenType getType() {
        return type;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public int getLength() {
        return length;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.format("TomlToken{type=%s, value='%s', %d:%d (%d)}", type, value, line, column, length);
    }
}
