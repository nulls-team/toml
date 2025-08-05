package dev.donutquine.toml;

import dev.donutquine.toml.util.ResourceLoader;
import org.junit.jupiter.api.Test;

import static dev.donutquine.toml.util.Assertions.assertArrayValueEquals;
import static org.junit.jupiter.api.Assertions.*;

public class ArraysTest {
    @Test
    public void testMatrix() {
        TomlParser parser = new TomlParser(ResourceLoader.getStringResource("/own/arrays.toml"));
        Toml toml = assertDoesNotThrow(parser::parse);

        assertTrue(toml.getRootTable().getArray("empty").isEmpty());
        assertArrayValueEquals(toml.getRootTable().getArray("numbers"), new Integer[]{1, 2, 3, 4, 5}, Integer.class);

        TomlArray matrix = toml.getRootTable().getArray("matrix");
        Integer[][] expectations = {
                {1, 2, 3},
                {4, 5, 6},
                {7, 8, 9}
        };
        assertEquals(matrix.getSize(), expectations.length);
        for (int i = 0; i < matrix.getSize(); i++) {
            TomlArray row = matrix.getArray(i);
            assertArrayValueEquals(row, expectations[i], Integer.class);
        }
    }
}
