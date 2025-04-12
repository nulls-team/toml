package dev.donutquine;

import dev.donutquine.toml.Toml;
import dev.donutquine.toml.TomlParser;
import dev.donutquine.toml.exceptions.TomlException;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, TomlException {
        TomlParser parser = new TomlParser("# test comment\nf1 = -0.0\nf2= { #test comment\ntest.\"google.com\" = [123,    \n\n\n\n\"test!\"],test2=1#testcomment2\n }");
        Toml toml = parser.parse();

        System.out.println(toml);
    }
}