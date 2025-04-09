package dev.donutquine.toml;

import dev.donutquine.toml.exceptions.TomlException;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;

public class TomlParser {
    private static final Function<String, TomlLexer> DEFAULT_LEXER = BasicTomlLexer::new;

    private final TomlLexer lexer;

    private Iterator<TomlToken> iterator;

    private TomlToken unhandledToken;

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

            if (isTable(token.getType())) {
                parseTableDeclaration(toml);
            } else if (isKey(token.getType())) {
                parseKeyValuePair(toml, token);
            } else if (token.getType() != TomlTokenType.COMMENT && token.getType() != TomlTokenType.NEWLINE) {
                throw new IllegalStateException("Unexpected token type: " + token.getType());
            }
        }

        return toml;
    }

    private void parseKeyValuePair(Toml toml, TomlToken token) {
        List<String> keyPath = new ArrayList<>(tryParseKey());
        keyPath.add(0, token.getValue());

        String key = keyPath.remove(keyPath.size() - 1);

        TomlTable table = toml.getCurrentTable();
        for (String tableKey : keyPath) {
            table = table.computeIfAbsent(tableKey, (k) -> new BasicTomlTable());
        }

        TomlToken equalsToken = unhandledToken;
        if (equalsToken.getType() == TomlTokenType.EQUALS) {
            Object value = parseValue();
            if (value != null) {
                table.setObject(key, value);
            }

            return;
        }

        throw new IllegalStateException("Unexpected token received " + equalsToken);
    }

    private void parseTableDeclaration(Toml toml) {
        List<String> keyPath = tryParseKey();
        if (keyPath.isEmpty()) {
            throw new IllegalStateException("Key path was expected, but not found");
        }

        if (unhandledToken.getType() != TomlTokenType.RIGHT_BRACKET) {
            throw new IllegalStateException("Unexpected token: " + unhandledToken);
        }

        toml.setTable(keyPath.toArray(String[]::new));
    }

    private List<String> tryParseKey() {
        List<String> keys = new ArrayList<>();

        while (iterator.hasNext()) {
            TomlToken keyToken = iterator.next();

            // Dotted key
            if (keyToken.getType() == TomlTokenType.DOT) {
                if (iterator.hasNext()) {
                    keyToken = iterator.next();
                    if (!isKey(keyToken.getType())) {
                        throw new IllegalStateException("Key token was expected, but received " + keyToken);
                    }

                    keys.add(keyToken.getValue());
                } else {
                    throw new IllegalStateException("Key token was expected, but received nothing");
                }
            } else if (isKey(keyToken.getType())) {
                if (keys.isEmpty()) {
                    keys.add(keyToken.getValue());
                } else {
                    throw new IllegalStateException("Key token wasn't expected, but received " + keyToken);
                }
            } else {
                this.unhandledToken = keyToken;
                break;
            }
        }

        return keys;
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
