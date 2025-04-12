package dev.donutquine.toml;

import dev.donutquine.toml.exceptions.TomlException;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;

public class TomlParser {
    private static final Function<String, TomlLexer> DEFAULT_LEXER = BasicTomlLexer::new;

    private final TomlLexer lexer;

    private Iterator<TomlToken> iterator;

    private TomlToken currentToken;

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

        TomlToken token = getNextToken();
        while (token != null) {
            if (isTable(token.getType())) {
                parseTableDeclaration(toml);
            } else if (isKey(token.getType())) {
                parseKeyValuePair(toml.getCurrentTable());
            } else if (!isSkippableToken(token)) {
                throw new IllegalStateException("Unexpected token type: " + token.getType());
            }

            token = getNextToken();
        }

        return toml;
    }

    private void parseKeyValuePair(TomlTable currentTable) {
        List<String> keyPath = new ArrayList<>(tryParseKey());
        if (keyPath.isEmpty()) {
            throw new IllegalStateException("Key path was expected, but not found");
        }

        String key = keyPath.remove(keyPath.size() - 1);

        TomlTable table = currentTable;
        for (String tableKey : keyPath) {
            table = table.computeIfAbsent(tableKey, (k) -> new BasicTomlTable());
        }

        TomlToken equalsToken = getCurrentToken();
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
        boolean isArray = false;

        TomlToken token = getNextToken();
        if (token == null) {
            throw new IllegalStateException("Unexpected end of file");
        }

        if (token.getType() == TomlTokenType.BRACKET_START) {
            isArray = true;
        }

        List<String> keyPath = new ArrayList<>(tryParseKey());
        if (keyPath.isEmpty()) {
            throw new IllegalStateException("Key path was expected, but not found");
        }

        if (currentToken.getType() != TomlTokenType.BRACKET_END) {
            throw new IllegalStateException("Unexpected token: " + currentToken);
        }

        if (isArray) {
            token = getNextToken();
            if (token == null) {
                throw new IllegalStateException("Unexpected end of file");
            }

            if (token.getType() != TomlTokenType.BRACKET_END) {
                throw new IllegalStateException("Unexpected token: " + currentToken);
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

    private List<String> tryParseKey() {
        List<String> keys = new ArrayList<>();

        TomlToken token = getCurrentToken();
        while (true) {
            while (isSkippableToken(token)) {
                token = getNextToken();
                if (token == null) {
                    return keys;
                }
            };

            // Dotted key
            if (token.getType() == TomlTokenType.PERIOD) {
                token = getNextToken();
                if (token != null) {
                    if (!isKey(token.getType())) {
                        throw new IllegalStateException("Key token was expected, but received " + token);
                    }

                    keys.add(token.getValue());
                } else {
                    throw new IllegalStateException("Key token was expected, but received nothing");
                }
            } else if (isKey(token.getType())) {
                if (keys.isEmpty()) {
                    keys.add(token.getValue());
                } else {
                    throw new IllegalStateException("Key token wasn't expected, but received " + token);
                }
            } else {
                break;
            }

            token = getNextToken();
        }

        return keys;
    }

    private Object parseValue() {
        TomlToken token;
        do {
            token = getNextToken();
            if (token == null) {
                return null;
            }
        } while (isSkippableToken(token));

        switch (token.getType()) {
            case BASIC_STRING:
            case LITERAL_STRING:
                return token.getValue();
            case BOOLEAN:
                return Boolean.parseBoolean(token.getValue());
            case INTEGER:
                return Integer.parseInt(token.getValue().replaceAll("_", ""));
            case HEX_INTEGER:
                return Integer.parseInt(token.getValue().replaceAll("(^0x|_)", ""), 16);
            case OCT_INTEGER:
                return Integer.parseInt(token.getValue().replaceAll("(^0o|_)", ""), 8);
            case BIN_INTEGER:
                return Integer.parseInt(token.getValue().replaceAll("(^0b|_)", ""), 2);
            case FLOAT:
                if (token.getValue().equals("nan")) {
                    return Float.NaN;
                } else if (token.getValue().equals("inf")) {
                    return Float.POSITIVE_INFINITY;
                }

                return Float.parseFloat(token.getValue());
            case BRACE_START:
                return parseInlineTable();
            case BRACKET_START:
                return parseArray();
        }

        return null;
    }

    private static boolean isSkippableToken(TomlToken token) {
        return token.getType() == TomlTokenType.WHITESPACE || token.getType() == TomlTokenType.COMMENT || token.getType() == TomlTokenType.NEWLINE;
    }

    private TomlToken getCurrentToken() {
        return currentToken;
    }

    private TomlToken getNextToken() {
        return currentToken = iterator.hasNext() ? iterator.next() : null;
    }

    private TomlArray parseArray() {
        TomlArray array = new TomlArray();

        boolean commaFound = false;


        TomlToken token = getCurrentToken();

        while (true) {
            if (token.getType() == TomlTokenType.BRACKET_END) {
                break;
            } else if (token.getType() == TomlTokenType.COMMA) {
                if (commaFound) {
                    throw new IllegalStateException("Comma occurs several times in a row.");
                }

                commaFound = true;
            } else {
                commaFound = false;
            }

            Object value = parseValue();
            if (value != null) {
                array.addObject(value);
            }

            token = getNextToken();
            if (token == null) {
                return null;
            }
        }

        return array;
    }

    private TomlTable parseInlineTable() {
        BasicTomlTable table = new BasicTomlTable();

        boolean commaFound = false;

        while (true) {
            TomlToken token;
            do {
                token = getNextToken();
                if (token == null) {
                    return null;
                }
            } while (isSkippableToken(token));

            if (token.getType() == TomlTokenType.BRACE_END) {
                if (commaFound) {
                    throw new IllegalStateException("Trailing comma is forbidden here.");
                }

                break;
            } else if (token.getType() == TomlTokenType.COMMA) {
                if (commaFound) {
                    throw new IllegalStateException("Comma occurs several times in a row.");
                }

                commaFound = true;
            } else {
                parseKeyValuePair(table);
                commaFound = false;
            }
        }

        return table;
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
