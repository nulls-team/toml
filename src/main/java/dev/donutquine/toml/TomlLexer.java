package dev.donutquine.toml;

import dev.donutquine.toml.exceptions.TomlException;

import java.io.IOException;

public interface TomlLexer {
    Iterable<TomlToken> tokenize() throws TomlException, IOException;
}
