package dev.donutquine.toml;

import dev.donutquine.toml.exceptions.ForbiddenCharInLiteralString;
import dev.donutquine.toml.exceptions.TomlException;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class BasicTomlLexer implements TomlLexer {
    private static final int EOF = -1;

    private static final char COMMENT_START_SYMBOL = '#';
    private static final char BASIC_STRING_QUOTE = '"';
    private static final char LITERAL_STRING_QUOTE = '\'';
    private static final char ESCAPE_CHAR = '\\';
    private static final char NEWLINE = '\n';
    private static final char BRACKET_START = '[';
    private static final char BRACKET_END = ']';
    private static final char BRACE_START = '{';
    private static final char BRACE_END = '}';
    private static final char EQUALS = '=';
    private static final char COMMA = ',';
    private static final char PERIOD = '.';
    private static final char PLUS_SIGN = '+';
    private static final char MINUS_SIGN = '-';
    private static final char UNDERSCORE = '_';

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
        if (position >= string.length()) {
            return null;
        }

        int startLine = line;
        int startColumn = column;
        int current = peekChar();

        StringBuilder buffer = new StringBuilder();
        TomlTokenType tokenType;

        if (CharsetValidator.isWhitespace(current)) {
            readWhitespace(buffer);

            tokenType = TomlTokenType.WHITESPACE;
        } else if (current == NEWLINE || current == '\r' && nextChar() == NEWLINE) {
            buffer.append((char) readChar());
            tokenType = TomlTokenType.NEWLINE;
        } else if (current == COMMENT_START_SYMBOL) {
            buffer.append((char) readChar());
            readUntil(buffer, NEWLINE);
            tokenType = TomlTokenType.COMMENT;
        } else if (current == COMMA) {
            buffer.append((char) readChar());
            tokenType = TomlTokenType.COMMA;
        } else if (current == PERIOD) {
            buffer.append((char) readChar());
            tokenType = TomlTokenType.PERIOD;
        } else if (current == EQUALS) {
            buffer.append((char) readChar());
            tokenType = TomlTokenType.EQUALS;
        } else if (current == BASIC_STRING_QUOTE) {
            readChar(); // consume "
            nextBasicString(buffer);
            tokenType = TomlTokenType.BASIC_STRING;
        } else if (current == LITERAL_STRING_QUOTE) {
            readChar(); // consume '
            nextLiteralString(buffer);
            tokenType = TomlTokenType.LITERAL_STRING;
        } else if (current == '0' && nextChar() == 'x') {
            buffer.append((char) readChar());
            tokenType = TomlTokenType.HEX_INTEGER;
        } else if (current == '0' && nextChar() == 'o') {
            buffer.append((char) readChar());
            tokenType = TomlTokenType.OCT_INTEGER;
        } else if (current == '0' && nextChar() == 'b') {
            buffer.append((char) readChar());
            tokenType = TomlTokenType.BIN_INTEGER;
        } else if (CharsetValidator.isUnquotedKeyChar(current)) {
            readUnquotedKey(buffer);
            tokenType = TomlTokenType.IDENT;
        } else if (current == PLUS_SIGN || current == MINUS_SIGN || CharsetValidator.isDigit(current)) {
            buffer.append((char) readChar());
            tokenType = tryReadNumber(buffer);  // TODO
        } else if (current == BRACKET_START) {
            buffer.append((char) readChar());
            tokenType = TomlTokenType.BRACKET_START;
        } else if (current == BRACKET_END) {
            buffer.append((char) readChar());
            tokenType = TomlTokenType.BRACKET_END;
        } else if (current == BRACE_START) {
            buffer.append((char) readChar());
            tokenType = TomlTokenType.BRACE_START;
        } else if (current == BRACE_END) {
            buffer.append((char) readChar());
            tokenType = TomlTokenType.BRACE_END;
        } else {
            readUntil(buffer, CharsetValidator::isWhitespace);
            tokenType = TomlTokenType.UNKNOWN;
        }

        String value = buffer.toString();
        return new TomlToken(tokenType, new Location(startLine, startColumn), new Location(line, column), value);
    }

    private TomlTokenType tryReadNumber(StringBuilder buffer) {
        return TomlTokenType.UNKNOWN;
    }

    private void readWhitespace(StringBuilder buffer) {
        while (position < string.length()) {
            int c = peekChar();
            if (CharsetValidator.isWhitespace(c)) {
                buffer.append((char) readChar());
            } else {
                break;
            }
        }
    }

    private void readUnquotedKey(StringBuilder buffer) {
        while (position < string.length()) {
            int character = peekChar();
            if (CharsetValidator.isUnquotedKeyChar(character)) {
                buffer.append((char) readChar());
            } else {
                break;
            }
        }
    }

    private void nextLiteralString(StringBuilder buffer) throws TomlException {
        while (true) {
            int character = peekChar();
            if (character == EOF || character == LITERAL_STRING_QUOTE) {
                readChar();
                break;
            }

            if (!CharsetValidator.isLiteralChar(character)) {
                throwException(ForbiddenCharInLiteralString::new);
            }

            buffer.append((char) readChar());
        }
    }

    private void throwException(BiFunction<String, Location, TomlException> exceptionFactory) throws TomlException {
        String line = string.split("\r?\n")[this.line];

        throw exceptionFactory.apply(line, new Location(this.line, column));
    }

    private void nextBasicString(StringBuilder buffer) {
        readUntil(buffer, BASIC_STRING_QUOTE);

        char lastChar = buffer.charAt(buffer.length() - 1);
        while (lastChar == ESCAPE_CHAR) {
            buffer.append(BASIC_STRING_QUOTE);
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

    private void readUntil(StringBuilder buffer, Function<Integer, Boolean> untilFunction) {
        while (true) {
            int character = peekChar();
            if (character == EOF || untilFunction.apply(character)) {
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

    private int nextChar() {
        if (position + 1 >= string.length()) {
            return EOF;
        }

        return string.charAt(position + 1);
    }

    private int readChar() {
        if (position >= string.length()) {
            return EOF;
        }

        char character = string.charAt(position++);
        column++;

        if (character == NEWLINE) {
            line++;
            column = 0;
        }

        return character;
    }
}
