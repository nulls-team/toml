package dev.donutquine;

import dev.donutquine.toml.Toml;
import dev.donutquine.toml.TomlParser;
import dev.donutquine.toml.exceptions.TomlException;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, TomlException {
        TomlParser parser = new TomlParser("# test comment\nf1 = -0.0\nf2=5e+22\nb=true\ni=+3_03_1123__21");
        Toml toml = parser.parse();

        System.out.println(toml);
    }
}