package dev.donutquine.toml;

import dev.donutquine.toml.util.ResourceLoader;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CommentTest {
    @Test
    public void test() {
        TomlParser parser = new TomlParser(ResourceLoader.getStringResource("/spec/comments.toml"));

        Toml toml = assertDoesNotThrow(parser::parse);
        assertTrue(toml.getCurrentTable().isEmpty());
    }
}
