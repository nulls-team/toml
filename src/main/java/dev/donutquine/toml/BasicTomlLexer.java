package dev.donutquine.toml;

import dev.donutquine.toml.exceptions.TomlException;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    /* language=RegExp */
    @SuppressWarnings("RegExpUnnecessaryNonCapturingGroup")
    private static final String UNSIGNED_INTEGER_REGEX = "(?:0|[1-9](?:_?[0-9]+)*)";
    /* language=RegExp */
    private static final String INTEGER_REGEX = "[+-]?" + UNSIGNED_INTEGER_REGEX;
    /* language=RegExp */
    private static final String HEX_INTEGER_REGEX = "0x[0-9A-F](?:_?[0-9A-F]+)*";
    /* language=RegExp */
    private static final String OCT_INTEGER_REGEX = "0o[0-7](?:_?[0-7]+)*";
    /* language=RegExp */
    private static final String BIN_INTEGER_REGEX = "0b[01](?:_?[01]+)*";
    /* language=RegExp */
    private static final String BOOLEAN_REGEX = "true|false";
    /* language=RegExp */
    @SuppressWarnings("RegExpUnnecessaryNonCapturingGroup")
    private static final String ZERO_PREFIXABLE_INTEGER = "(?:[0-9](?:_?[0-9]+)*)";
    /* language=RegExp */
    private static final String SPECIAL_FLOAT = "nan|inf";
    /* language=RegExp */
    @SuppressWarnings("RegExpUnnecessaryNonCapturingGroup")
    private static final String FLOAT_FRAC_PART = "(?:\\." + ZERO_PREFIXABLE_INTEGER + ')';
    /* language=RegExp */
    @SuppressWarnings("RegExpUnnecessaryNonCapturingGroup")
    private static final String FLOAT_EXP_PART = "(?:[Ee][+-]?" + ZERO_PREFIXABLE_INTEGER + ')';
    /* language=RegExp */
    @SuppressWarnings("RegExpUnnecessaryNonCapturingGroup")
    private static final String FLOAT_REGEX = "[+-]?(?:(?:" + UNSIGNED_INTEGER_REGEX + "(?:" + FLOAT_FRAC_PART + FLOAT_EXP_PART + "?|" + FLOAT_EXP_PART + ")" + ")|" + SPECIAL_FLOAT + ")";

    private final String string;

    private int position;
    private int line, column;

    private boolean valueRequired;

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
        TomlTokenType tokenType = null;

        if (CharsetValidator.isWhitespace(current)) {
            readWhitespace(buffer);

            tokenType = TomlTokenType.WHITESPACE;
        } else if (current == NEWLINE || current == '\r' && nextChar() == NEWLINE) {
            buffer.append((char) readChar());
            tokenType = TomlTokenType.NEWLINE;
            valueRequired = false;
        } else if (current == COMMENT_START_SYMBOL) {
            buffer.append((char) readChar());
            readUntil(buffer, NEWLINE);
            tokenType = TomlTokenType.COMMENT;
        } else if (current == COMMA) {
            buffer.append((char) readChar());
            tokenType = TomlTokenType.COMMA;
            valueRequired = false;
        } else if (current == PERIOD) {
            buffer.append((char) readChar());
            tokenType = TomlTokenType.PERIOD;
            valueRequired = false;
        } else if (current == EQUALS) {
            buffer.append((char) readChar());
            tokenType = TomlTokenType.EQUALS;
            valueRequired = true;
        } else if (current == BRACKET_START) {
            buffer.append((char) readChar());
            tokenType = TomlTokenType.BRACKET_START;
            valueRequired = false;
        } else if (current == BRACKET_END) {
            buffer.append((char) readChar());
            tokenType = TomlTokenType.BRACKET_END;
            valueRequired = false;
        } else if (current == BASIC_STRING_QUOTE) {
            nextBasicString(buffer);
            tokenType = TomlTokenType.BASIC_STRING;
        } else if (current == LITERAL_STRING_QUOTE) {
            nextLiteralString(buffer);
            tokenType = TomlTokenType.LITERAL_STRING;
            valueRequired = false;
        } else if (valueRequired) {
            if (current == BRACE_START) {
                buffer.append((char) readChar());
                tokenType = TomlTokenType.BRACE_START;
            } else if (current == BRACE_END) {
                buffer.append((char) readChar());
                tokenType = TomlTokenType.BRACE_END;
            } else {
                LexemeRegexMatchResult matchResult = nextRegexMatch(
                    new LexemeRegex(FLOAT_REGEX, TomlTokenType.FLOAT),
                    new LexemeRegex(INTEGER_REGEX, TomlTokenType.INTEGER),
                    new LexemeRegex(HEX_INTEGER_REGEX, TomlTokenType.HEX_INTEGER),
                    new LexemeRegex(OCT_INTEGER_REGEX, TomlTokenType.OCT_INTEGER),
                    new LexemeRegex(BIN_INTEGER_REGEX, TomlTokenType.BIN_INTEGER),
                    new LexemeRegex(BOOLEAN_REGEX, TomlTokenType.BOOLEAN)
                );

                if (matchResult != null) {
                    buffer.append(matchResult.lexeme);
                    skip(matchResult.lexeme.length());
                    tokenType = matchResult.type;
                }
            }

            valueRequired = false;
        } else if (CharsetValidator.isUnquotedKeyChar(current)) {
            readUnquotedKey(buffer);
            tokenType = TomlTokenType.IDENT;
        }

        if (tokenType == null) {
            readUntil(buffer, character -> CharsetValidator.isWhitespace(character) || CharsetValidator.isNewline(character));
            tokenType = TomlTokenType.UNKNOWN;
        }

        String value = buffer.toString();
        return new TomlToken(tokenType, new Location(startLine, startColumn), new Location(line, column), value);
    }

    private LexemeRegexMatchResult nextRegexMatch(LexemeRegex... lexemeRegexps) {
        for (LexemeRegex lexeme : lexemeRegexps) {
            String match = getRegexMatch(lexeme.regex);
            if (match != null) {
                return new LexemeRegexMatchResult(match, lexeme.type);
            }
        }


        return null;
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

    private void nextBasicString(StringBuilder buffer) {
        readChar(); // consume "

        boolean escaped = false;

        while (position < string.length()) {
            int character = peekChar();

            if (character == BASIC_STRING_QUOTE && !escaped) {
                break;
            }

            buffer.append((char) readChar());

            if (character == '\\') {
                escaped = true;
                continue;
            }

            escaped = false;
        }

        readChar(); // consume "
    }

    private void nextLiteralString(StringBuilder buffer) {
        readChar(); // consume '

        while (position < string.length()) {
            int character = peekChar();

            if (character == LITERAL_STRING_QUOTE) {
                break;
            }

            buffer.append((char) readChar());
        }

        readChar(); // consume '
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

    private void skip(int count) {
        while (position < string.length() && count > 0) {
            readChar();
            count--;
        }
    }

    private String getRegexMatch(String regex) {
        Pattern pattern = Pattern.compile("^(" + regex + ")");

        Matcher matcher = pattern.matcher(string);
        matcher.region(position, string.length());

        if (matcher.find() && matcher.start() == position) {
            return matcher.group();
        }

        return null;
    }

    private static class LexemeRegex {
        public final String regex;
        public final TomlTokenType type;

        private LexemeRegex(String regex, TomlTokenType type) {
            this.regex = regex;
            this.type = type;
        }
    }

    private static class LexemeRegexMatchResult {
        public final String lexeme;
        public final TomlTokenType type;

        private LexemeRegexMatchResult(String lexeme, TomlTokenType type) {
            this.lexeme = lexeme;
            this.type = type;
        }
    }
}
