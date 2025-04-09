package dev.donutquine.toml;

import java.io.IOException;
import java.io.StringReader;
import java.util.function.Function;

public class TomlParser {
    private static final Function<StringReader, TomlLexer> DEFAULT_LEXER = LazyTomlLexer::new;

    private final TomlLexer lexer;

    public TomlParser(String toml) {
        this(new StringReader(toml));
    }

    public TomlParser(StringReader tomlReader) {
        this(DEFAULT_LEXER.apply(tomlReader));
    }

    public TomlParser(TomlLexer lexer) {
        this.lexer = lexer;
    }

    public Toml parse() throws IOException {
        Toml toml = new Toml();

        Iterable<TomlToken> tokens = lexer.tokenize();
        for (TomlToken token : tokens) {
            System.out.println(token);
        }

        return toml;
    }
}
