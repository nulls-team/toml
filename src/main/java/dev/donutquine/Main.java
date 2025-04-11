package dev.donutquine;

import dev.donutquine.toml.Toml;
import dev.donutquine.toml.TomlParser;
import dev.donutquine.toml.exceptions.TomlException;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, TomlException {
        TomlParser parser = new TomlParser("# test comment\n[[v]]'nan' = \"test \n\rstring aha\"");
        Toml toml = parser.parse();

        System.out.println(toml);
    }
}