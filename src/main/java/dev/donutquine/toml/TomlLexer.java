package dev.donutquine.toml;

import java.io.IOException;

public interface TomlLexer {
    Iterable<TomlToken> tokenize() throws IOException;
}
