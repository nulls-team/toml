package dev.donutquine;

import dev.donutquine.toml.Toml;
import dev.donutquine.toml.TomlParser;
import dev.donutquine.toml.exceptions.TomlException;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, TomlException {
        TomlParser parser = new TomlParser("# test comment\nnan = -123_12321");
        Toml toml = parser.parse();

        System.out.println(toml);
    }
}