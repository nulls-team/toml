package dev.donutquine.toml;

public class Toml {
    private final TomlTable rootTable = new BasicTomlTable();

    private TomlTable currentTable = rootTable;

    public void setCurrentTableByPath(String... tableName) {
        TomlTable table = rootTable;
        for (String key : tableName) {
            table = table.computeIfAbsent(key, (k) -> new BasicTomlTable());
        }

        this.currentTable = table;
    }

    public void setCurrentTable(TomlTable table) {
        this.currentTable = table;
    }

    public TomlTable getTable(String tableName) {
        return rootTable.getTable(tableName);
    }

    public TomlTable getRootTable() {
        return rootTable;
    }

    TomlTable getCurrentTable() {
        return currentTable;
    }
}
