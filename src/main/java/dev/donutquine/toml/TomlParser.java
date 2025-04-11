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

    private void parseKeyValuePair(Toml toml, TomlToken keyToken) {
        List<String> keyPath = new ArrayList<>(tryParseKey(keyToken));

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
        if (!iterator.hasNext()) {
            throw new IllegalStateException("Unexpected end of file");
        }

        boolean isArray = false;

        TomlToken token = iterator.next();

        if (token.getType() == TomlTokenType.BRACKET_START) {
            isArray = true;

            if (iterator.hasNext()) {
                token = iterator.next();
            } else {
                throw new IllegalStateException("Unexpected end of file");
            }
        }

        List<String> keyPath = new ArrayList<>(tryParseKey(token));
        if (keyPath.isEmpty()) {
            throw new IllegalStateException("Key path was expected, but not found");
        }

        if (unhandledToken.getType() != TomlTokenType.BRACKET_END) {
            throw new IllegalStateException("Unexpected token: " + unhandledToken);
        }

        if (isArray) {
            if (!iterator.hasNext()) {
                throw new IllegalStateException("Unexpected end of file");
            }

            token = iterator.next();

            if (token.getType() != TomlTokenType.BRACKET_END) {
                throw new IllegalStateException("Unexpected token: " + unhandledToken);
            }

            String arrayKey = keyPath.remove(keyPath.size() - 1);

            TomlTable table = toml.getCurrentTable();
            for (String tableKey : keyPath) {
                table = table.computeIfAbsent(tableKey, (k) -> new BasicTomlTable());
            }

            TomlArray array = table.computeIfAbsent(arrayKey, (k) -> new TomlArray());
            if (array != null) {
                table = new BasicTomlTable();
                array.addTable(table);
                toml.setCurrentTable(table);
            } else {
                throw new IllegalStateException("Array not found");
            }
        } else {
            toml.setCurrentTableByPath(keyPath.toArray(String[]::new));
        }
    }

    private List<String> tryParseKey(TomlToken keyToken) {
        List<String> keys = new ArrayList<>();

        while (true) {
            // Dotted key
            if (keyToken.getType() == TomlTokenType.PERIOD) {
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
            } else if (keyToken.getType() != TomlTokenType.WHITESPACE) {
                this.unhandledToken = keyToken;
                break;
            }

            if (iterator.hasNext()) {
                keyToken = iterator.next();
            } else {
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
        while (valueToken.getType() == TomlTokenType.WHITESPACE) {
            if (!iterator.hasNext()) {
                return null;
            }

            valueToken = iterator.next();
        }

        switch (valueToken.getType()) {
            case BASIC_STRING:
            case LITERAL_STRING:
                value = valueToken.getValue();
                break;
            case INTEGER:
                value = Integer.parseInt(valueToken.getValue().replaceAll("_", ""));
                break;
            case HEX_INTEGER:
                value = Integer.parseInt(valueToken.getValue().replaceAll("(^0x|_)", ""), 16);
                break;
            case OCT_INTEGER:
                value = Integer.parseInt(valueToken.getValue().replaceAll("(^0o|_)", ""), 8);
                break;
            case BIN_INTEGER:
                value = Integer.parseInt(valueToken.getValue().replaceAll("(^0b|_)", ""), 2);
                break;
            case FLOAT:
            case IDENT:
                if (valueToken.getType() == TomlTokenType.IDENT) {
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
                break;
        }

        return value;
    }

    private static boolean isKey(TomlTokenType type) {
        switch (type) {
            case IDENT:
            case BASIC_STRING:
            case LITERAL_STRING:
            case INTEGER:
            case FLOAT:
                return true;
        }

        return false;
    }

    private boolean isTable(TomlTokenType type) {
        return type == TomlTokenType.BRACKET_START;
    }
}
