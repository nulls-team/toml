package dev.donutquine.toml;

import dev.donutquine.toml.util.ResourceLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.OS;

import static dev.donutquine.toml.util.Assertions.assertTableExists;
import static dev.donutquine.toml.util.Assertions.assertValueEquals;
import static org.junit.jupiter.api.Assertions.*;

public class StringsTest {
    @Test
    public void testBasicStrings() {
        TomlParser parser = new TomlParser(ResourceLoader.getStringResource("/spec/basic_strings.toml"));
        Toml toml = assertDoesNotThrow(parser::parse);

        assertValueEquals(toml.getRootTable(), "I'm a string. \"You can quote me\". Name\tJosé\nLocation\tSF.", "str");
    }

    @Test
    public void testMultilineBasicStrings() {
        TomlParser parser = new TomlParser(ResourceLoader.getStringResource("/spec/multiline_basic_strings.toml"));
        Toml toml = assertDoesNotThrow(parser::parse);

        String unixString = assertValueEquals(toml.getRootTable(), "Roses are red\nViolets are blue", "str2");
        String windowsString = assertValueEquals(toml.getRootTable(), "Roses are red\r\nViolets are blue", "str3");

        if (OS.current() == OS.WINDOWS) {
            assertValueEquals(toml.getRootTable(), windowsString, "str1");
        } else {
            assertValueEquals(toml.getRootTable(), unixString, "str1");
        }
    }
}
