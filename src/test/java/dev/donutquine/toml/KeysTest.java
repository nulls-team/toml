package dev.donutquine.toml;

import dev.donutquine.toml.util.ResourceLoader;
import org.junit.jupiter.api.Test;

import static dev.donutquine.toml.util.Assertions.assertTableExists;
import static dev.donutquine.toml.util.Assertions.assertValueEquals;
import static org.junit.jupiter.api.Assertions.*;

public class KeysTest {
    @Test
    public void testBareKeys() {
        TomlParser parser = new TomlParser(ResourceLoader.getStringResource("/spec/bare_keys.toml"));
        Toml toml = assertDoesNotThrow(parser::parse);

        assertTrue(toml.getRootTable().has("key"));
        assertTrue(toml.getRootTable().has("bare_key"));
        assertTrue(toml.getRootTable().has("bare-key"));
        assertTrue(toml.getRootTable().has("1234"));
    }

    @Test
    public void testQuotedKeys() {
        TomlParser parser = new TomlParser(ResourceLoader.getStringResource("/spec/quoted_keys.toml"));
        Toml toml = assertDoesNotThrow(parser::parse);

        assertTrue(toml.getRootTable().has("127.0.0.1"));
        assertTrue(toml.getRootTable().has("character encoding"));
        assertTrue(toml.getRootTable().has("ʎǝʞ"));
        assertTrue(toml.getRootTable().has("key2"));
        assertTrue(toml.getRootTable().has("quoted \"value\""));
    }

    @Test
    public void testDiscourageEmptyKey() {
        TomlParser parser = new TomlParser(ResourceLoader.getStringResource("/spec/discourage_empty_key.toml"));
        Toml toml = assertDoesNotThrow(parser::parse);

        assertTrue(toml.getRootTable().has(""));
        assertEquals("blank", toml.getRootTable().getString(""));
    }

    @Test
    public void testDiscourageLiteralEmptyKey() {
        TomlParser parser = new TomlParser(ResourceLoader.getStringResource("/spec/discourage_literal_empty_key.toml"));
        Toml toml = assertDoesNotThrow(parser::parse);

        assertTrue(toml.getRootTable().has(""));
        assertEquals("blank", toml.getRootTable().getString(""));
    }

    @Test
    public void testDottedKeys() {
        TomlParser parser = new TomlParser(ResourceLoader.getStringResource("/spec/dotted_keys.toml"));
        Toml toml = assertDoesNotThrow(parser::parse);

        assertValueEquals(toml.getRootTable(), "Orange", "name");
        ValueAccessor physicalTable = assertTableExists(toml.getRootTable(), "physical");
        assertValueEquals(physicalTable, "orange", "color");
        assertValueEquals(physicalTable, "round", "shape");
        assertValueEquals(assertTableExists(toml.getRootTable(), "site"), true, "google.com");
    }

    @Test
    public void testDottedKeysWhitespaced() {
        TomlParser parser = new TomlParser(ResourceLoader.getStringResource("/spec/dotted_keys_whitespaced.toml"));
        Toml toml = assertDoesNotThrow(parser::parse);

        ValueAccessor fruitTable = assertTableExists(toml.getRootTable(), "fruit");
        assertValueEquals(fruitTable, "banana", "name");
        assertValueEquals(fruitTable, "yellow", "color");
        assertValueEquals(fruitTable, "banana", "flavor");
    }

    @Test
    public void testDottedIndirectDefinition() {
        TomlParser parser = new TomlParser(ResourceLoader.getStringResource("/spec/dotted_indirect_definition.toml"));
        Toml toml = assertDoesNotThrow(parser::parse);

        ValueAccessor fruitTable = assertTableExists(toml.getRootTable(), "fruit");
        ValueAccessor appleTable = assertTableExists(fruitTable, "apple");
        assertValueEquals(appleTable, true, "smooth");
        assertValueEquals(fruitTable, 2, "orange");
    }
}
