package dev.donutquine.toml;

public class Location {
    private final int line, column;

    public Location(int line, int column) {
        this.line = line;
        this.column = column;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    @Override
    public String toString() {
        return String.format("%d:%d", line, column);
    }
}
