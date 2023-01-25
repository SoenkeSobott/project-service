package org.soenke.sobott.enums;

public enum Structure {
    Wall("Wall"),
    Column("Column"),
    Culvert("Culvert"),
    Shoring("Shoring");

    private final String value;

    Structure(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
