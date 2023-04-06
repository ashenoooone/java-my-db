package com.digdes.school.DB;

import java.util.ArrayList;
import java.util.List;

public class Column {
    private final String name;
    private final ColumnType type;
    private final List<String> availableOperators;

    public Column(String name, ColumnType type) {
        this.name = name;
        this.type = type;
        switch (type) {
            case STRING:
                this.availableOperators = new ArrayList<>(List.of("=", "!=", "like", "ilike"));
                break;
            case BOOLEAN:
                this.availableOperators = new ArrayList<>(List.of("and", "or"));
                break;
            case DOUBLE, LONG:
                this.availableOperators = new ArrayList<>(List.of("=", "!=", "<=", "<", ">=", ">"));
                break;
            default:
                throw new IllegalArgumentException("Неподдерживаемый тип");
        }
    }

    public String getName() {
        return name;
    }

    public ColumnType getType() {
        return type;
    }

    public List<String> getAvailableOperators() {
        return new ArrayList<>(availableOperators);
    }

    @Override
    public String toString() {
        return "Column{" +
                "name='" + name + '\'' +
                ", type=" + type +
                '}';
    }
}
