package dev.donutquine.toml;

import dev.donutquine.toml.exceptions.ForbiddenCharInLiteralString;
import dev.donutquine.toml.exceptions.TomlException;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class BasicTomlLexer implements TomlLexer {
    private static final int EOF = -1;

    private static final char COMMENT_START_SYMBOL = '#';
    private static final char BASIC_STRING_START = '"';
    private static final char BASIC_STRING_END = '"';
    private static final char LITERAL_STRING_START = '\'';
    private static final char LITERAL_STRING_END = '\'';
    private static final char ESCAPE_CHAR = '\\';
    private static final char NEWLINE_CHAR = '\n';

    private final StringReader reader;

    private StringBuilder lineBuilder = new StringBuilder();

    private int position;
    private int line, column = 1;

    public BasicTomlLexer(StringReader tomlReader) {
        this.reader = tomlReader;
    }

    @Override
    public Iterable<TomlToken> tokenize() throws TomlException, IOException {
        List<TomlToken> tokens = new ArrayList<>();

        while (true) {
            TomlToken token = next();
            if (token == null) {
                break;
            }

            tokens.add(token);
        }

        return tokens;
    }

    private TomlToken next() throws TomlException, IOException {
        StringBuilder buffer = new StringBuilder();

        int startLine = line;
        int startColumn = column;

        reader.reset();

        TomlTokenType tokenType;

        while (true) {
            int character = readChar();
            if (character == EOF) {
                return null;
            }

            if (character == COMMENT_START_SYMBOL) {
                buffer.append((char) character);

                readUntil(buffer, NEWLINE_CHAR);

                tokenType = TomlTokenType.COMMENT;

                break;
            } else if (character == BASIC_STRING_START) {
                nextBasicString(buffer);

                tokenType = TomlTokenType.BASIC_STRING;
                break;
            } else if (character == LITERAL_STRING_START) {
                nextLiteralString(buffer);

                tokenType = TomlTokenType.LITERAL_STRING;
                break;
            }
        }

        reader.mark(position);

        String value = buffer.toString();

        return new TomlToken(tokenType, new Location(startLine, startColumn), new Location(line, column), value);
    }

    private void nextLiteralString(StringBuilder buffer) throws TomlException, IOException {
        while (true) {
            int character = readChar();
            if (character == EOF || character == LITERAL_STRING_END) {
                break;
            }

            if (!isLiteralChar(character)) {
                throwException(ForbiddenCharInLiteralString::new);
            }

            buffer.append((char) character);
        }
    }

    private boolean isLiteralChar(int character) {
        return character == '\t' || (character >= 0x20 && character <= 0x26) || (character >= 0x28 && character <= 0x76) || isNonAscii(character);
    }

    private boolean isNonAscii(int character) {
        return (character >= 0x80 && character <= 0xd7ff) || (character >= 0xe000 && character <= 0x10ffff);
    }

    private void throwException(BiFunction<String, Location, TomlException> exceptionFactory) throws TomlException, IOException {
        int column = this.column;

        while (true) {
            int character = readChar();
            if (character == EOF || character == NEWLINE_CHAR) {
                break;
            }
        }

        throw exceptionFactory.apply(lineBuilder.toString(), new Location(line, column));
    }

    private void nextBasicString(StringBuilder buffer) throws IOException {
        readUntil(buffer, BASIC_STRING_END);

        char lastChar = buffer.charAt(buffer.length() - 1);
        while (lastChar == ESCAPE_CHAR) {
            buffer.append(BASIC_STRING_END);
            readUntil(buffer, ESCAPE_CHAR);
            lastChar = buffer.charAt(buffer.length() - 1);
        }
    }

    private void readUntil(StringBuilder buffer, char until) throws IOException {
        while (true) {
            int character = readChar();
            if (character == EOF || character == until) {
                break;
            }

            buffer.append((char) character);
        }
    }

    private int readChar() throws IOException {
        int character = reader.read();
        if (character == EOF) {
            return character;
        }

        lineBuilder.append((char) character);

        position++;
        column++;

        if (character == NEWLINE_CHAR) {
            lineBuilder = new StringBuilder();

            line++;
            column = 0;
        }

        return character;
    }
}
