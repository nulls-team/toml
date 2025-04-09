package dev.donutquine.toml.exceptions;

import dev.donutquine.toml.Location;

public class ForbiddenCharInLiteralString extends TomlException {
    public ForbiddenCharInLiteralString(String line, Location location) {
        super("Forbidden char met in literal string.", line, location);
    }
}
