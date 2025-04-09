package dev.donutquine.toml;

public class Toml {
    private final TomlTable rootTable = new BasicTomlTable();

    private TomlTable currentTable = rootTable;

    public void setTable(String tableName) {
        this.currentTable = rootTable.computeIfAbsent(tableName, name -> new BasicTomlTable());
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
