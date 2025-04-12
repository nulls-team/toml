package dev.donutquine.toml;

public interface TomlLexer {
    Iterable<TomlToken> tokenize();
}
