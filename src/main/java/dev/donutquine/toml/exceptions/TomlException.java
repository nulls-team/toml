package dev.donutquine.toml.exceptions;

import dev.donutquine.toml.Location;

import java.util.Arrays;

public abstract class TomlException extends Throwable {
    private final String line;
    private final Location location;
    private final String message;

    private String detailMessage;

    public TomlException(String message, String line, Location location) {
        super();

        this.message = message;
        this.line = line;
        this.location = location;
    }

    @Override
    public String getMessage() {
        if (detailMessage == null) {
            StringBuilder builder = new StringBuilder();
            builder.append(message);
            builder.append('\n');

            builder.append(line);
            builder.append('\n');

            char[] pointer = new char[location.getColumn()];
            Arrays.fill(pointer, 0, pointer.length - 1, ' ');
            pointer[pointer.length - 1] = '^';

            builder.append(pointer);

            detailMessage = builder.toString();
        }

        return detailMessage;
    }
}
