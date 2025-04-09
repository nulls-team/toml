package dev.donutquine.toml;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LazyTomlLexer implements TomlLexer {
    private final StringReader reader;

    public LazyTomlLexer(StringReader tomlReader) {
        this.reader = tomlReader;
    }

    public Iterable<TomlToken> tokenize() throws IOException {
        Iterator<TomlToken> tokenIterator = new TomlTokenIterator();

        return () -> tokenIterator;
    }

    private class TomlTokenIterator implements Iterator<TomlToken> {
        private final List<Integer> tokenCharacters = new ArrayList<>();

        private int character;

        private int position;
        private int line, column;

        public TomlTokenIterator() throws IOException {
            this.character = reader.read();
            position++;
        }

        @Override
        public boolean hasNext() {
            return this.character != -1;
        }

        @Override
        public TomlToken next() {
            try {
                while (!isTokenCompleted()) {
                    this.tokenCharacters.add(character);
                    this.character = reader.read();
                    position++;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            return null;
        }

        private boolean isTokenCompleted() {
            return false;
        }
    }
}
