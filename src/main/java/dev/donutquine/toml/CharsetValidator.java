package dev.donutquine.toml;

public final class CharsetValidator {
    private CharsetValidator() {
    }

    public static boolean isWhitespace(int character) {
        return character == '\t' || character == ' ';
    }

    public static boolean isBareKeyChar(int character) {
        return Character.isLetterOrDigit(character) || character == '-' || character == '_';
    }

    public static boolean isUnquotedKeyChar(int character) {
        return
            // a-z A-Z 0-9 - _
            isAlpha(character) || isDigit(character) || character == '-' || character == '_'
                // superscript digits, fractions
                || character == 0xB2 || character == 0xB3 || character == 0xB9 || character >= 0xBC && character <= 0xBE
                // non-symbol chars in Latin block
                || character >= 0xC0 && character <= 0xD6 || character >= 0xD8 && character <= 0xF6 || character >= 0xF8 && character <= 0x37D
                // exclude GREEK QUESTION MARK, which is basically a semi-colon
                || character >= 0x37F && character <= 0x1FFF
                // from General Punctuation Block, include the two tie symbols and ZWNJ, ZWJ
                || character >= 0x200C && character <= 0x200D || character >= 0x203F && character <= 0x2040
                // include super-/subscripts, letterlike/numberlike forms, enclosed alphanumerics
                || character >= 0x2070 && character <= 0x218F || character >= 0x2460 && character <= 0x24FF
                // skip arrows, math, box drawing etc, skip 2FF0-3000 ideographic up/down markers and spaces
                || character >= 0x2C00 && character <= 0x2FEF || character >= 0x3001 && character <= 0xD7FF
                // skip D800-DFFF surrogate block, E000-F8FF Private Use area, FDD0-FDEF intended for process-internal use (unicode)
                || character >= 0xF900 && character <= 0xFDCF || character >= 0xFDF0 && character <= 0xFFFD
                // all chars outside BMP range, excluding Private Use planes (F0000-10FFFF);
                || character >= 0x10000 && character <= 0xEFFFF;
    }

    public static boolean isAlpha(int character) {
        return character >= 'A' && character <= 'Z' || character >= 'a' && character <= 'z';
    }

    public static boolean isDigit(int character) {
        return character >= '0' && character <= '9';
    }

    public static boolean isHexDigit(int character) {
        return character >= '0' && character <= '9' || character >= 'A' && character <= 'F';
    }

    public static boolean isLiteralChar(int character) {
        return character == '\t' || (character >= 0x20 && character <= 0x26) || (character >= 0x28 && character <= 0x76) || isNonAscii(character);
    }

    public static boolean isNonAscii(int character) {
        return (character >= 0x80 && character <= 0xd7ff) || (character >= 0xe000 && character <= 0x10ffff);
    }
}
