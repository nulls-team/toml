package dev.donutquine.toml;

import dev.donutquine.toml.exceptions.ForbiddenCharInLiteralString;
import dev.donutquine.toml.exceptions.TomlException;
import dev.donutquine.toml.exceptions.UnknownLexemeException;

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
    private static final char LEFT_BRACKET = '[';
    private static final char RIGHT_BRACKET = ']';
    private static final char EQUALS = '=';
    private static final char COMMA = ',';
    private static final char DOT = '.';
    public static final char PLUS_SIGN = '+';
    public static final char MINUS_SIGN = '-';
    public static final char UNDERSCORE = '_';

    private final String string;

    private int position;
    private int line, column;

    public BasicTomlLexer(String toml) {
        this.string = toml;
    }

    @Override
    public Iterable<TomlToken> tokenize() throws TomlException {
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

    private TomlToken next() throws TomlException {
        skipWhitespace();

        if (position >= string.length()) {
            return null;
        }

        int startLine = line;
        int startColumn = column;
        int current = peekChar();

        StringBuilder buffer = new StringBuilder();
        TomlTokenType tokenType;

        if (current == NEWLINE_CHAR) {
            buffer.append((char) readChar());
            tokenType = TomlTokenType.NEWLINE;
        } else if (current == COMMENT_START_SYMBOL) {
            buffer.append((char) readChar());
            readUntil(buffer, NEWLINE_CHAR);
            tokenType = TomlTokenType.COMMENT;
        } else if (current == BASIC_STRING_START) {
            readChar(); // consume "
            nextBasicString(buffer);
            tokenType = TomlTokenType.BASIC_STRING;
        } else if (current == LITERAL_STRING_START) {
            readChar(); // consume '
            nextLiteralString(buffer);
            tokenType = TomlTokenType.LITERAL_STRING;
        } else if (current == PLUS_SIGN || current == MINUS_SIGN) {
            buffer.append((char) readChar());

            tokenType = tryReadNumber(buffer);

            if (tokenType == null) {
                throwException(UnknownLexemeException::new);
            }
        } else if (current == LEFT_BRACKET) {
            buffer.append((char) readChar());
            tokenType = TomlTokenType.LEFT_BRACKET;
        } else if (current == RIGHT_BRACKET) {
            buffer.append((char) readChar());
            tokenType = TomlTokenType.RIGHT_BRACKET;
        } else if (current == EQUALS) {
            buffer.append((char) readChar());
            tokenType = TomlTokenType.EQUALS;
        } else if (current == COMMA) {
            buffer.append((char) readChar());
            tokenType = TomlTokenType.COMMA;
        } else if (current == DOT) {
            buffer.append((char) readChar());
            tokenType = TomlTokenType.DOT;
        } else if (isBareKeyChar(current)) {
            tokenType = readBareKey(buffer);
        } else {
            throwException(UnknownLexemeException::new);
            return null;
        }

        String value = buffer.toString();
        return new TomlToken(tokenType, new Location(startLine, startColumn), new Location(line, column), value);
    }

    private TomlTokenType tryReadNumber(StringBuilder buffer) {
        return null;
    }

    private TomlTokenType readBareKey(StringBuilder buffer) {
        while (position < string.length()) {
            int c = peekChar();
            if (!isBareKeyChar(c)) break;
            buffer.append((char) readChar());
        }

        return TomlTokenType.BARE_KEY;
    }

    private boolean isBareKeyChar(int c) {
        return Character.isLetterOrDigit(c) || c == MINUS_SIGN || c == UNDERSCORE;
    }

    private void skipWhitespace() {
        while (position < string.length()) {
            int c = peekChar();
            if (isWhitespace(c)) {
                readChar();
            } else {
                break;
            }
        }
    }

    private boolean isWhitespace(int character) {
        return character == '\t' || character == ' ';
    }

    private void nextLiteralString(StringBuilder buffer) throws TomlException {
        while (true) {
            int character = peekChar();
            if (character == EOF || character == LITERAL_STRING_END) {
                readChar();
                break;
            }

            if (!isLiteralChar(character)) {
                throwException(ForbiddenCharInLiteralString::new);
            }

            buffer.append((char) readChar());
        }
    }

    private boolean isLiteralChar(int character) {
        return character == '\t' || (character >= 0x20 && character <= 0x26) || (character >= 0x28 && character <= 0x76) || isNonAscii(character);
    }

    private boolean isNonAscii(int character) {
        return (character >= 0x80 && character <= 0xd7ff) || (character >= 0xe000 && character <= 0x10ffff);
    }

    private void throwException(BiFunction<String, Location, TomlException> exceptionFactory) throws TomlException {
        String line = string.split("\r?\n")[this.line];

        throw exceptionFactory.apply(line, new Location(this.line, column));
    }

    private void nextBasicString(StringBuilder buffer) {
        readUntil(buffer, BASIC_STRING_END);

        char lastChar = buffer.charAt(buffer.length() - 1);
        while (lastChar == ESCAPE_CHAR) {
            buffer.append(BASIC_STRING_END);
            readUntil(buffer, ESCAPE_CHAR);
            lastChar = buffer.charAt(buffer.length() - 1);
        }

        readChar();
    }

    private void readUntil(StringBuilder buffer, char until) {
        while (true) {
            int character = peekChar();
            if (character == EOF || character == until) {
                break;
            }

            buffer.append((char) readChar());
        }
    }

    private int peekChar() {
        if (position >= string.length()) {
            return EOF;
        }

        return string.charAt(position);
    }

    private int readChar() {
        if (position >= string.length()) {
            return EOF;
        }

        char character = string.charAt(position++);
        column++;

        if (character == NEWLINE_CHAR) {
            line++;
            column = 0;
        }

        return character;
    }
}
