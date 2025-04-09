package dev.donutquine.toml;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class BasicTomlLexer implements TomlLexer {
    private final LazyTomlLexer lexer;

    public BasicTomlLexer(StringReader tomlReader) {
        this.lexer = new LazyTomlLexer(tomlReader);
    }

    public Iterable<TomlToken> tokenize() throws IOException {
        List<TomlToken> tokens = new ArrayList<>();

        for (TomlToken token : lexer.tokenize()) {
            tokens.add(token);
        }

        return tokens;
    }
}
