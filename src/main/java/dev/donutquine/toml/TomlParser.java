package dev.donutquine.toml;

import dev.donutquine.toml.exceptions.TomlException;

import java.io.IOException;
import java.util.Iterator;
import java.util.function.Function;

public class TomlParser {
    private static final Function<String, TomlLexer> DEFAULT_LEXER = BasicTomlLexer::new;

    private final TomlLexer lexer;
    private Iterator<TomlToken> iterator;

    public TomlParser(String toml) {
        this(DEFAULT_LEXER.apply(toml));
    }

    public TomlParser(TomlLexer lexer) {
        this.lexer = lexer;
    }

    public Toml parse() throws IOException, TomlException {
        Toml toml = new Toml();

        Iterable<TomlToken> tokens = lexer.tokenize();
        iterator = tokens.iterator();
        while (iterator.hasNext()) {
            TomlToken token = iterator.next();

            if (isKey(token.getType())) {
                parseKeyValuePair(toml, token);
            } else if (isTable(token.getType())) {

            } else if (token.getType() != TomlTokenType.COMMENT && token.getType() != TomlTokenType.NEWLINE) {
                throw new IllegalStateException("Unexpected token type: " + token.getType());
            }
        }

        return toml;
    }

    private void parseKeyValuePair(Toml toml, TomlToken token) {
        TomlTable table = toml.getCurrentTable();

        String key = token.getValue();
        while (iterator.hasNext()) {
            TomlToken dotToken = iterator.next();
            switch (dotToken.getType()) {
                // Dotted key
                case DOT:
                    if (iterator.hasNext()) {
                        TomlToken keyToken = iterator.next();
                        if (!isKey(keyToken.getType())) {
                            throw new IllegalStateException("Key token was expected, but received " + keyToken);
                        }

                        table = table.getTable(key);
                        key = keyToken.getValue();
                    }
                    break;
                case EQUALS:
                    Object value = parseValue();
                    if (value != null) {
                        table.setObject(key, value);
                    }

                    return;
            }
        }
    }

    private Object parseValue() {
        if (!iterator.hasNext()) {
            return null;
        }

        Object value = null;

        TomlToken valueToken = iterator.next();
        switch (valueToken.getType()) {
            case BASIC_STRING:
            case LITERAL_STRING:
                value = valueToken.getValue();
                break;
            case INTEGER:
                value = Integer.parseInt(valueToken.getValue());
                break;
            case FLOAT:
            case BARE_KEY:
                if (valueToken.getType() == TomlTokenType.BARE_KEY) {
                    if (valueToken.getValue().equals("nan")) {
                        return Float.NaN;
                    } else if (valueToken.getValue().equals("inf")) {
                        return Float.POSITIVE_INFINITY;
                    } else {
                        // Unsupported values
                        return null;
                    }
                }

                value = Float.parseFloat(valueToken.getValue());
        }

        return value;
    }

    private static boolean isKey(TomlTokenType type) {
        switch (type) {
            case BARE_KEY:
            case BASIC_STRING:
            case LITERAL_STRING:
            case INTEGER:
            case FLOAT:
                return true;
        }

        return false;
    }

    private boolean isTable(TomlTokenType type) {
        return type == TomlTokenType.LEFT_BRACKET;
    }
}
