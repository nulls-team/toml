package dev.donutquine.toml.exceptions;

import dev.donutquine.toml.Location;

public class UnknownLexemeException extends TomlException {
    public UnknownLexemeException(String line, Location location) {
        super("Unexpected character. ", line, location);
    }
}
