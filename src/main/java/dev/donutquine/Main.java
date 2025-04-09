package dev.donutquine;

import dev.donutquine.toml.Toml;
import dev.donutquine.toml.TomlParser;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        TomlParser parser = new TomlParser("test = value");
        Toml toml = parser.parse();
    }
}