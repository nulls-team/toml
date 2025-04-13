package dev.donutquine.toml;

public class Toml {
    private final TomlTable rootTable = new BasicTomlTable();

    private TomlTable currentTable = rootTable;

    public TomlTable findTableByPath(Iterable<String> tablePath) {
        TomlTable table = rootTable;
        for (String key : tablePath) {
            TomlArray array = table.getArray(key);
            if (array != null) {
                // Note: array size is 1 at least if array exists
                table = array.getTable(array.getSize() - 1);
            } else {
                table = table.computeIfAbsent(key, (k) -> new BasicTomlTable());
            }
        }

        return table;
    }

    public TomlTable getRootTable() {
        return rootTable;
    }

    public void setCurrentTable(TomlTable table) {
        this.currentTable = table;
    }

    TomlTable getCurrentTable() {
        return currentTable;
    }

    @Override
    public String toString() {
        return "Toml{" + rootTable + '}';
    }
}
