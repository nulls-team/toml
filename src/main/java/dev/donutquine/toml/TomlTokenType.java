package dev.donutquine.toml;

public enum TomlTokenType {
    NEWLINE,
    COMMENT,
    BASIC_STRING,
    LITERAL_STRING,
    INTEGER,
    FLOAT,
    BARE_KEY,
    LEFT_BRACKET,
    RIGHT_BRACKET,
    EQUALS,
    COMMA,
    DOT,
    DATETIME,
}
