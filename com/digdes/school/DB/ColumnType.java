package com.digdes.school.DB;

public enum ColumnType {
    STRING,
    BOOLEAN,
    DOUBLE,
    LONG;
    public Class<?> getTypeClass() {
        return switch (this) {
            case STRING -> String.class;
            case LONG -> Long.class;
            case DOUBLE -> Double.class;
            case BOOLEAN -> Boolean.class;
        };
    }
}
